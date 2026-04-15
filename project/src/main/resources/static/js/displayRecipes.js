
document.addEventListener("DOMContentLoaded", () => {
    console.log("plannerMode value:", sessionStorage.getItem("plannerMode"));

    const plannerMode = sessionStorage.getItem("plannerMode") === "true";

    const closeBtn = document.getElementById("close-btn");

    if (plannerMode) {
        closeBtn.href = "/html/homepage.html";

        const editBtn = document.getElementById("edit-recipe-btn");

        if (editBtn) {
            editBtn.style.display = "none";
        }
    } else {
        closeBtn.href = "/html/recipes.html";
    }

    //const params = new URLSearchParams(window.location.search);
   // const recipeId = params.get('id');
    //const recipeId = sessionStorage.getItem("selectedRecipeId");
    //const recipeId = parseInt(sessionStorage.getItem("selectedRecipeId"), 10);
    let recipeId = sessionStorage.getItem("selectedRecipeId") || "";
    recipeId = recipeId.replace(/[^0-9]/g, ""); // remove all non-digit chars
    recipeId = parseInt(recipeId, 10);
    console.log("Retrieved recipe ID from sessionStorage:", recipeId);

    //console.log("Current URL:", window.location.href);
   // console.log("Recipe ID:", new URLSearchParams(window.location.search).get('id'));

    if (!recipeId) {
        console.warn("No recipe ID found in URL. Redirecting to recipes page...");
        return;
    }

    
    fetch(`http://localhost:8080/api/recipes/${recipeId}`)
    .then(response => {
        if (!response.ok)  {
        return response.text().then(text => {
            throw new Error(`Server error ${response.status}: ${text}`);
        });
    }
    return response.json();
})
    .then(recipe => {

        document.getElementById('recipe-name').textContent = recipe.name;

       
        document.getElementById('prep-time').textContent = recipe.preptime;
        document.getElementById('cook-time').textContent = recipe.cooktime; 
        document.getElementById('recipe-cost').textContent = recipe.cost||"N/A"; 
        document.getElementById('serving-size').textContent = recipe.servings;
        document.getElementById('difficulty').textContent = recipe.difficulty;

        document.getElementById('dietary-restrictions').textContent =
          recipe.dietaryRestrictions?.length ? recipe.dietaryRestrictions.join(", "): "N/A";

       document.getElementById('intolerances').textContent =
            recipe.intolerances?.length
                ? recipe.intolerances.join(", ")
                : "N/A";

       document.getElementById('preferences').textContent =
            recipe.preferences?.length
                ? recipe.preferences.join(", ") 
                : "N/A";
       
        const ingredientsList = document.getElementById('ingredients-list');
        ingredientsList.innerHTML = "";
        if (recipe.ingredients?.length) {
            recipe.ingredients.forEach(ing => {
                const li = document.createElement('li');
                li.textContent = ing;
                ingredientsList.appendChild(li);
            });
        } else {
            const li = document.createElement('li');
            li.textContent = "No ingredients available";
            ingredientsList.appendChild(li);
        }

        const prepSteps = document.getElementById('prep-steps');
        prepSteps.innerHTML = "";
            const sect = document.createElement('div');
            sect.textContent = recipe.description;
            prepSteps.appendChild(sect);
        
        const img = document.getElementById('recipe-image');

        if (recipe.file_path) {
            img.src = recipe.file_path;
            img.alt = recipe.name;
        } else {
            img.src = "images/placeholder.png";
            img.alt = "No image available";
            console.log("No image available for this recipe.");
        }

    })
    .catch(err => {
        console.error("fetch error:", err);
        alert('Error loading recipe. Make sure you are logged in and the backend is running.');
    });


});
const editBtn=document.getElementById('edit-recipe-btn');
    if(editBtn)
    {
           editBtn.addEventListener('click', () => {
              const recipeId = sessionStorage.getItem("selectedRecipeId");
            // const recipeId = new URLSearchParams(window.location.search).get('id');
            window.location.href = `/html/editRecipe.html?id=${recipeId}`;
        });
    }

const delBtn = document.getElementById("delete-recipe-btn");
const plannerMode = sessionStorage.getItem("plannerMode") === "true";

if (delBtn) {

    delBtn.addEventListener("click", async () => {

        if (plannerMode) {

            const day = sessionStorage.getItem("selectedDay");
            const meal = sessionStorage.getItem("selectedMeal");
            const sunday = sessionStorage.getItem("weeklyPlanSunday");

            const start = new Date(sunday).toISOString().split("T")[0];

            await fetch(`/api/weekly-plan/remove?day=${day}&meal=${meal}&start=${start}`, {
                method: "DELETE",
                credentials: "include"
            });

            sessionStorage.removeItem("plannerMode");
            sessionStorage.removeItem("selectedDay");
            sessionStorage.removeItem("selectedMeal");

            window.location.href = "/html/homepage.html";

        } else {

            const recipeId = sessionStorage.getItem("selectedRecipeId");

            if (!confirm("Are you sure you want to delete this recipe?")) {
                return;
            }

            const response = await fetch(`/recipes/delete/${recipeId}`, {
                method: "DELETE"
            });

            if (response.ok) {
                alert(await response.text());
                sessionStorage.removeItem("selectedRecipeId");
                window.location.href = "/html/recipes.html";
            } else {
                alert("Delete failed");
            }
        }

    });

}
