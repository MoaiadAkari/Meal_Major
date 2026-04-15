// Import layout and initialize page
import { loadLayout } from "./layout.js";

// Tracking which week it is from current week (0 = current week, +1 = next week, -1 = previous week)
let weekOffset = 0;

document.addEventListener("DOMContentLoaded", async () => {
    // Saved offset is needed to return the user to the week that they last interacted with when they come back to this page
    const savedOffset = sessionStorage.getItem("plannerWeekOffset");
    if (savedOffset !== null) {
        weekOffset = parseInt(savedOffset);
    }
    
    await loadLayout();
    await loadWeeklyPlan();
    renderWeekDates();
    updateWeeklyPlanSunday();

    // Each time that the weeks are changed, the offset must be updated and dates and recipes load
    document.getElementById("next-week").addEventListener("click", async () => {
        weekOffset++;
        sessionStorage.setItem("plannerWeekOffset", weekOffset);
        renderWeekDates();
        updateWeeklyPlanSunday();
        await loadWeeklyPlan();
    });

    document.getElementById("prev-week").addEventListener("click", async () => {
        weekOffset--;
        sessionStorage.setItem("plannerWeekOffset", weekOffset);
        renderWeekDates();
        updateWeeklyPlanSunday();
        await loadWeeklyPlan();
    });

    // When the modify weekly plan button is clicked, it has to only be for the currently toggled week
    // The information of the week must then be sent to that page
    const createBtn = document.getElementById("create-weekly-plan");
    if (createBtn) {
        createBtn.addEventListener("click", () => {
            const today = new Date();
            const dayOfWeek = today.getDay();
            const sunday = new Date(today);
            sunday.setDate(today.getDate() - dayOfWeek + weekOffset * 7);

            // Save the Sunday of the current week to sessionStorage
            sessionStorage.setItem("weeklyPlanSunday", sunday.toISOString().split("T")[0]);

            // Navigate to weeklyPlan.html
            window.location.href = "/html/weeklyPlan.html";
        });
    }
});

// Creates a button for the inputted recipe in that week
// The button links to the display recipes page that is will be in planner mode so that the delete button will remove the recipe from the weekly plan instead of deleting the recipe
async function placeRecipe(day, meal, recipe) { // example: placeRecipe("mon", "breakfast", recipes[0]) "mon" and "breakfast" will be combined to form id "mon-breakfast"

    const cell = document.getElementById(`${day}-${meal}`);
    if (!cell) return;

    const button = document.createElement("button");
    button.classList.add("recipe-btn");

    button.innerHTML = `
        <img src="${recipe.file_path}" class="recipe-image">
    `;

    button.addEventListener("click", () => {
        const weekDates = getCurrentWeekDates();
        const sunday = weekDates[0];
        
        sessionStorage.setItem("plannerMode", "true");
        sessionStorage.setItem("selectedRecipeId", recipe.id);
        sessionStorage.setItem("selectedDay", day);
        sessionStorage.setItem("selectedMeal", meal);
        sessionStorage.setItem("weeklyPlanSunday", sunday.toISOString().split("T")[0]);
        sessionStorage.setItem("plannerWeekOffset", weekOffset);

        window.location.href = "/html/displayRecipes.html";
    });

    cell.innerHTML = "";
    cell.appendChild(button);
}

// Get an array of Dates for the current week (Sun → Sat)
function getCurrentWeekDates() {
    const today = new Date();
    const dayOfWeek = today.getDay(); // 0=Sunday, 1=Monday...
    const sunday = new Date(today);


    sunday.setDate(today.getDate() - dayOfWeek + weekOffset * 7);

    const weekDates = [];
    for (let i = 0; i < 7; i++) {
        const d = new Date(sunday);
        d.setDate(sunday.getDate() + i);
        weekDates.push(d);
    }
    return weekDates;
}

function updateWeeklyPlanSunday() {
    const sunday = getCurrentWeekDates()[0];
    const weekKey = sunday.toISOString().split("T")[0];

    sessionStorage.setItem("weeklyPlanSunday", weekKey);
}

// Format a date like "Mar 12"
function formatDate(date) {
    return date.toLocaleDateString("en-US", { month: "short", day: "numeric" });
}

// Render week range and dates in column headers
function renderWeekDates() {
    const weekDates = getCurrentWeekDates();

    // Update week range (top header)
    const rangeDiv = document.getElementById("week-range");
    rangeDiv.textContent = `${formatDate(weekDates[0])} – ${formatDate(weekDates[6])}`;

    // Update each day column header
    const dayDivs = document.querySelectorAll(".calendar-days .day");
    const daysOfWeek = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];

    for (let i = 1; i <= 7; i++) {
        const date = weekDates[i-1];
        const month = date.toLocaleDateString("en-US", { month: "short" });
        const dayNumber = date.getDate();

        dayDivs[i].innerHTML = `${daysOfWeek[i-1]} ${month}<br>${dayNumber}`;
    }
}

async function loadWeeklyPlan() {

    document.querySelectorAll(".cell").forEach(cell => {
        cell.innerHTML = "";
    });

    const sunday = getCurrentWeekDates()[0];
    const startDate = sunday.toISOString().split("T")[0];

    const response = await fetch(
        `/api/weekly-plan/week?start=${startDate}`,
        {
            credentials: "include" // include cookies for session authentication
        }
    );

    if (!response.ok) {
        console.error("Weekly plan request failed:", await response.text());
        return;
    }

    const data = await response.json();
    console.log("Weekly plan data:", data);

    for (const day in data) {

        const meals = data[day];

        for (const meal in meals) {
            const recipeId = meals[meal];

            // skip empty or null FKs
            if (recipeId === null) {
                continue;
            }

            const mealName = meal.replace("_fk", "");

            try {
                const recipeRes = await fetch(`/api/recipes/${recipeId}`);
                if (!recipeRes.ok) { 
                    continue;
                }
                const recipe = await recipeRes.json();
                placeRecipe(day, mealName, recipe);
            } catch (err) {
                console.warn("Failed to load recipe", recipeId, err);
            }
        }
    }
}