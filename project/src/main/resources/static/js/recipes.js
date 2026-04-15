import { loadLayout } from "./layout.js";

const plannerMode = sessionStorage.getItem("plannerMode") === "true";

document.addEventListener("DOMContentLoaded", async () => {

    // When adding a recipe to weekly plan, the search and search results functionalities are used from this page
    // However elements like the title, header and featured recipes section are not relevant in this mode so they will be hidden
    if (plannerMode) {
        const header = document.querySelector("#header");
        if (header) header.style.display = "none";
    
        // Hide title, subtitle, and create button
        const title = document.querySelector(".hero-details .title");
        const subtitle = document.querySelector(".hero-details .subtitle");
        const createBtn = document.querySelector(".buttons");

        if (title) title.style.display = "none";
        if (subtitle) subtitle.style.display = "none";
        if (createBtn) createBtn.style.display = "none";
    }
    await loadLayout();
    await loadPreferences();

    console.log("Recipes page loaded");

    setupFilterSidebar();
    setupSearch();

    if (!plannerMode) {
        await displayRecipes(null);
    } else {
        const featuredRecipes = document.querySelector(".section-title");
        if (featuredRecipes) featuredRecipes.style.display = "none";

        const container = document.getElementById("recipe-container");
        container.innerHTML = "<p>Search for recipes or apply filters to see results.</p>";
    }
});

// The filter options are not always present on the page as they are made visible only when the filter button is clicked
// This initializes the needed buttons and elements
function setupFilterSidebar() {
    const openButton = document.getElementById("filter-open-button");
    const closeButton = document.getElementById("filter-close");
    const sidebar = document.getElementById("filter-sidebar");
    const overlay = document.getElementById("filter-popout");

    openButton.addEventListener("click", () => {
        sidebar.classList.add("active");
        overlay.classList.add("active");
    });

    closeButton.addEventListener("click", closeFilter);
    overlay.addEventListener("click", closeFilter);

    function closeFilter() {
        sidebar.classList.remove("active");
        overlay.classList.remove("active");
    }
}

// Initializes the search features 
function setupSearch() {
    const searchButton = document.getElementById("search-button");
    const applyFiltersButton = document.getElementById("apply-filters");
    const searchBar = document.getElementById("search-bar");

    searchButton.addEventListener("click", searchRecipes);
    applyFiltersButton.addEventListener("click", searchRecipes);
    searchBar.addEventListener("keydown", (event) => {
        if (event.key === "Enter") {
            event.preventDefault();
            searchRecipes();
        }
    });
}

// Search and filter criteria sent to the backend to retrieve the recipes that match those criteria
async function searchRecipes() {
    const name = document.querySelector(".search-bar input").value;
    const maxTime = document.getElementById("filter-time").value;
    const difficulty = document.getElementById("filter-difficulty").value;
    const maxCost = document.getElementById("filter-cost").value;

    const selectedPreferences = Array.from(
        document.getElementById("filter-diet").selectedOptions
    ).map(option => option.value);

    const userId = sessionStorage.getItem("userId");
    const weekStart = sessionStorage.getItem("weeklyPlanSunday");

    if (!userId || !weekStart) {
        console.warn("Planner mode cannot filter without userId or weekStart");
    }

    const filterData = {
        name: name || null,
        maxTime: maxTime ? parseInt(maxTime) : null,
        difficulty: difficulty || null,
        maxCost: maxCost ? parseFloat(maxCost) : null,
        preferences: selectedPreferences.length > 0 ? selectedPreferences : null,
        excludePlanned: plannerMode,
        userId: userId ? parseInt(userId) : null,
        weekStart: weekStart || null
    };

    console.log("plannerMode:", plannerMode);
    console.log("userId:", userId);
    console.log("weekStart:", weekStart);

    console.log("Sending filter:", filterData);

    try {
        const response = await fetch("http://localhost:8080/api/recipes/search", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(filterData)
        });

        if (!response.ok) throw new Error("Search request failed");

        const recipes = await response.json();
        displayRecipes(recipes);

    } catch (error) {
        console.error("Error fetching recipes:", error);
    }
}

async function loadPreferences() {

    try {
        const response = await fetch("http://localhost:8080/api/recipes/preferences");

        if (!response.ok) {
            throw new Error("Failed to fetch preferences");
        }

        const preferences = await response.json();

        const select = document.getElementById("filter-diet");

        if (!select) {
            console.error("filter-diet select not found in HTML");
            return;
        }

        select.innerHTML = "";

        preferences.forEach(pref => {
            const option = document.createElement("option");
            option.value = pref;
            option.textContent = pref.replaceAll("_", " ");
            select.appendChild(option);
        });

    } catch (error) {
        console.error("Error loading preferences:", error);
    }
}


// Fetch and display recipes for the featured recipes section
async function getRandomRecipes() {
    const response = await fetch("/recipes/random"); // fix fetch according to what colin said
    const data= await response.json();
    return data;
}


// Creates the clickable recipe cards with the information from the randomly selected recipes
async function displayRecipes(recipes) {
  const container = document.getElementById("recipe-container");
  container.innerHTML = "";

  const container_title = document.getElementById("recipe-cards-title");

  let selectedRecipes;

  if(recipes === null){
    selectedRecipes = await getRandomRecipes();
  }
  else{
    if (!Array.isArray(recipes) || recipes.length === 0) {
        container.innerHTML = "<p>No recipes found.</p>";
        return;
    }
    selectedRecipes = recipes;

    container_title.innerHTML = "Search Results";
  }

  selectedRecipes.forEach(recipe => {
    const link = document.createElement("a");
    link.href = `/html/displayRecipes.html?id=${recipe.id}`;
    link.classList.add("recipe-link");

    const card = document.createElement("div");
    card.classList.add("recipe-card");

    card.innerHTML = `
      <img src="${recipe.file_path}" class="recipe-image">
      <div class="recipe-content">
        <div class="recipe-title">${recipe.name}</div>
        <div class="recipe-cost">Cost: ${recipe.cost}$</div>
        <div class="recipe-difficulty">Difficulty: ${recipe.difficulty}</div>
        <div class="recipe-prepTime">Prep Time: ${recipe.prepTime} mins</div>
        <div class="recipe-cookTime">Cook Time: ${recipe.cookTime} mins</div>
        <div class="recipe-description">${recipe.description}</div>
      </div>
    `;

    const recipeId=recipe.id; 
    console.log("Recipe name:", recipe.name, "| id:", recipeId);
    console.log("Navigating to DisplayRecipes with ID:", recipeId);

    card.addEventListener("click", () => {

        if (plannerMode) {

            const day = sessionStorage.getItem("selectedDay");
            const meal = sessionStorage.getItem("selectedMeal");
            const sunday = sessionStorage.getItem("weeklyPlanSunday");

            fetch("/api/weekly-plan/add", {
                method: "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    day: day,
                    meal: meal,
                    recipeId: recipe.id,
                    weekStart: new Date(sunday).toISOString().split("T")[0]
                })
            }).then(() => {
                sessionStorage.removeItem("plannerMode");
                window.location.href = "/html/homepage.html";
            });

        } else {
            sessionStorage.setItem("selectedRecipeId", recipe.id);
            window.location.href = "/html/displayRecipes.html";

        }

    });

    container.appendChild(card);
  });
}


// ---------------------
