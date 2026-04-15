const API_BASE = "http://localhost:8080/api/recipes";
const USER_PK = 1; 

async function createRecipe(data, file) {

    const formData = new FormData();

    // IMPORTANT: must match @RequestPart("recipe")
    formData.append(
        "recipe",
        new Blob([JSON.stringify(data)], { type: "application/json" })
    );

    // Must match @RequestPart("image")
    if (file) {
        formData.append("image", file);
    }

    const res = await fetch(API_BASE, {
        method: "POST",
        headers: {
            "X-USER-PK": USER_PK
        },
        body: formData
    });

    if (!res.ok) {
        const error = await res.json();
        throw new Error(error.error || "Failed to create recipe");
    }

    return res.json();
}
// -------------------
const form = document.getElementById("add-recipe-form");

form.addEventListener("submit", async (e) => {
    e.preventDefault();

    try {
        const recipeData = buildRecipeDTO();
        console.log("Sending:", recipeData);

        const file = document.getElementById("imageInput").files[0];
        const result = await createRecipe(recipeData, file);

        alert("Recipe created! ID: " + result.recipe_pk);
        window.location.href = "/html/recipes.html";

    } catch (err) {
        console.error(err);
        alert("Error creating recipe: " + err.message);
    }
});
// -------------------
function buildRecipeDTO() {

    // Ingredients
    const ingredientRows = document.querySelectorAll(".ingredient-row");

    const ingredients = Array.from(ingredientRows).map(row => ({
        amount: parseFloat(row.querySelector('input[name="quantity"]').value),
        unit: row.querySelector('select[name="unit"]').value,
        ingredientDesc: row.querySelector('input[name="ingredient"]').value
    }));

    // Allergens (from dietary-restrictions)
    const allergenPks = getCheckedValues("dietary-restrictions");

    // Health (from intolerances)
    const healthPks = getCheckedValues("intolerances");

    // Beliefs (from preferences)
    const beliefPks = getCheckedValues("preferences");

    return {
        name: document.getElementById("recipe-name").value,
        prepTime: parseInt(document.getElementById("prep-time").value),
        cookTime: parseInt(document.getElementById("cook-time").value),
        cost: parseInt(document.getElementById("recipe-cost").value),
        difficultyPk: mapDifficultyToPk(
            document.getElementById("difficulty").value
        ),
        servings: parseInt(document.getElementById("serving-size").value),
        description: document.getElementById("prep-steps").value,
        ingredients,
        allergenPks,
        healthPks,
        beliefPks
    };
}
// ------------------
function getCheckedValues(containerId) {
    const checkboxes = document
        .getElementById(containerId)
        .querySelectorAll("input[type='checkbox']:checked");

    return Array.from(checkboxes).map(cb => mapValueToPk(cb.value));
}
// ------------------
function mapDifficultyToPk(value) {
    const map = {
        "Beginner": 1,
        "Easy": 2,
        "Medium": 3,
        "Hard": 4,
        "Expert": 5
    };

    return map[value] || null;
}
// ------------------
function mapValueToPk(value) {
    const map = {
        // Allergens
        "peanuts": 1,
        "tree-nuts": 2,
        "eggs": 3,
        "soy": 4,
        "wheat": 5,
        "fish": 6,
        "shellfish": 7,
        "sesame": 8,

        // Health
        "lactose": 1,
        "low-sugar": 2,
        "gluten-free": 3,
        "low-sodium": 4,

        // Beliefs
        "vegetarian": 1,
        "vegan": 2,
        "kosher": 3,
        "keto": 4,
        "halal": 5
    };

    return map[value] || null;
}
// Import layout and initialize page
import { loadLayout } from "./layout.js";

document.addEventListener("DOMContentLoaded", async () => {
    await loadLayout();

    console.log("Add Recipe page loaded");
});
// --------------------
// Ingredient row management
const container = document.getElementById("ingredients-container");
const addButton = document.getElementById("add-ingredient-btn");

function addIngredientRow() {
    const row = document.createElement("div");
    row.classList.add("ingredient-row");

    row.innerHTML = `
        <input type="number" name="quantity" min="1" placeholder="Amount" required>

        <select name="unit">
            <option value="">Unit</option>
            <option value="cups">Cups</option>
            <option value="tablespoons">Tablespoons</option>
            <option value="teaspoons">Teaspoons</option>
            <option value="grams">Grams</option>
            <option value="litres">Litres</option>
            <option value="ml">Millilitres</option>
            <option value="pieces">Pieces</option>
        </select>

        <input type="text" name="ingredient" placeholder="Ingredient" required>

        <button type="button" class="remove-btn">
            <i class="fa-solid fa-trash"></i>
        </button>
    `;

    row.querySelector(".remove-btn").addEventListener("click", () => {
        row.remove();
    });

    container.appendChild(row);
}

addIngredientRow();

addButton.addEventListener("click", addIngredientRow);
// --------------------
// Dropdown management
const buttons = document.querySelectorAll(".dropdown-btn");
const dropdowns = document.querySelectorAll(".options");

buttons.forEach(button => {
    button.addEventListener("click", function (e) {
        e.stopPropagation(); // Prevent outside click from firing

        const targetId = this.getAttribute("data-target");
        const targetDropdown = document.getElementById(targetId);

        dropdowns.forEach(drop => {
            if (drop !== targetDropdown) {
                drop.style.display = "none";
            }
        });

        targetDropdown.style.display =
            targetDropdown.style.display === "block" ? "none" : "block";
    });
});

dropdowns.forEach(drop => {
    drop.addEventListener("click", function (e) {
        e.stopPropagation();
    });
});

document.addEventListener("click", function () {
    dropdowns.forEach(drop => {
        drop.style.display = "none";
    });
});
// --------------------
// Image preview
const imageInput = document.getElementById("imageInput");
const previewImage = document.getElementById("previewImage");

imageInput.addEventListener("change", function () {
    const file = this.files[0];

    if (file) {
        const reader = new FileReader();

        reader.onload = function (e) {
            previewImage.src = e.target.result;
            previewImage.style.display = "block";
        };

        reader.readAsDataURL(file);
    }
});
// --------------------
