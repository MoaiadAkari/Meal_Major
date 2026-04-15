import { loadLayout } from "./layout.js";

document.addEventListener("DOMContentLoaded", async () => {
    await loadLayout();

    // Get Sunday from homepage
    const sundayISO = sessionStorage.getItem("weeklyPlanSunday");
    let sundayDate;

    if (sundayISO) {
        const [year, month, day] = sundayISO.split("-");
        sundayDate = new Date(year, month - 1, day);
    } else {
        // fallback to current week's Sunday
        const today = new Date();
        sundayDate = new Date(today);
        sundayDate.setDate(today.getDate() - today.getDay());
    }

    // Render the week
    renderWeekDates(sundayDate);
    
    setupMealSelection();

    await loadWeeklyPlan(sundayDate);

    //  Setup meal cell clicks
});

// ---------------------
// Render week header and day columns
function renderWeekDates(sundayDate) {
    const weekDates = [];
    for (let i = 0; i < 7; i++) {
        // Use a new Date object to avoid mutation issues
        const d = new Date(sundayDate.getTime());
        d.setDate(d.getDate() + i);
        weekDates.push(d);
    }

    // Update header range
    const rangeDiv = document.getElementById("week-range");
    rangeDiv.textContent = `${formatDate(weekDates[0])} – ${formatDate(weekDates[6])}`;

    // Update day headers
    const dayDivs = document.querySelectorAll(".calendar-days .day");
    const daysOfWeek = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];
    for (let i = 1; i <= 7; i++) {
        const date = weekDates[i-1];
        const month = date.toLocaleString("en-US", { month: "short" });
        const dayNumber = date.getDate();
        dayDivs[i].innerHTML = `
            <span class="day-name">${daysOfWeek[i-1]} ${month}</span>
            <span class="date">${dayNumber}</span>
        `;
    }
}

// ---------------------
// Format date as "Mon DD"
function formatDate(date) {
    return date.toLocaleDateString("en-US", { month: "short", day: "numeric" });
}

// ---------------------
// Setup click handlers for meal cells
function setupMealSelection() {
    const mealCells = document.querySelectorAll(".cell");

    mealCells.forEach(cell => {
        cell.addEventListener("click", () => {

            const [day, meal] = cell.id.split("-");

            // Save which cell the user clicked
            sessionStorage.setItem("selectedDay", day);
            sessionStorage.setItem("selectedMeal", meal);

            // Enable planner mode
            sessionStorage.setItem("plannerMode", "true");

            // Redirect to recipe search
            window.location.href = "/html/recipes.html";
        });
    });
}

// ---------------------
// Assign a recipe to a meal
export function assignRecipeToMeal(day, meal, recipe) {
    const cell = document.getElementById(`${day}-${meal}`);
    if (!cell) return;

    cell.innerHTML = `
        <img src="${recipe.file_path}" class="recipe-image">
    `;
}

// ---------------------
// Mark snack as "No Snack"
export function markNoSnack(day, meal) {
    const cell = document.getElementById(`${day}-${meal}`);
    if (!cell) return;

    cell.textContent = "No Snack";
    cell.classList.add("no-snack");
}

async function loadWeeklyPlan(sundayDate) {

    document.querySelectorAll(".cell").forEach(cell => {
        cell.innerHTML = "";
    });

    const startDate = sundayDate.toISOString().split("T")[0];

    const response = await fetch(`/api/weekly-plan/week?start=${startDate}`, {
        credentials: "include"
    });

    if (!response.ok) {
        console.error("Failed to load weekly plan");
        return;
    }

    const data = await response.json();

    for (const day in data) {

        const meals = data[day];

        for (const meal in meals) {

            const recipeId = meals[meal];
            if (!recipeId) continue;

            const mealName = meal.replace("_fk", "");

            const recipeRes = await fetch(`/api/recipes/${recipeId}`);
            
            if (!recipeRes.ok) continue;
            const recipe = await recipeRes.json();

            assignRecipeToMeal(day, mealName, recipe);
        }
    }
}
