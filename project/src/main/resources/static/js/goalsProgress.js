import { loadLayout } from "./layout.js";

let selectedGoal = null;
let currentProgress = 0;
let currentJoinPk = null;

// max progress now comes from backend instead of being hardcoded
let maxProgress = 3;

document.addEventListener("DOMContentLoaded", async () => {
    await loadLayout();
    await loadSelectedGoal();
});

async function loadSelectedGoal() {
    const container = document.getElementById("goal-progress-content");

    if (!container) {
        console.error("goal-progress-content not found");
        return;
    }

    const joinPk = sessionStorage.getItem("selectedJoinPk");

    if (!joinPk) {
        container.innerHTML = "<p>No goal selected.</p>";
        return;
    }

    currentJoinPk = Number(joinPk);

    try {
        const response = await fetch(`/api/goals/plan-goal/${currentJoinPk}`, {
            method: "GET",
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error("Failed to fetch saved goal progress");
        }

        const data = await response.json();
        console.log("Loaded goal progress:", data);

        selectedGoal = {
            id: data.goalPk,
            name: data.goal
        };

        currentProgress = data.progressCount || 0;
        maxProgress = data.requiredIterations || 3;

        // store progress info in sessionStorage so it stays easy to access
        sessionStorage.setItem("goalCurrentProgress", currentProgress);
        sessionStorage.setItem("goalMaxProgress", maxProgress);

        renderGoal();
    } catch (err) {
        console.error("Error loading selected goal progress:", err);
        container.innerHTML = "<p>Could not load selected goal.</p>";
    }
}

function getPercent(progress) {
    return Math.round((progress / maxProgress) * 100);
}

function renderGoal() {
    const container = document.getElementById("goal-progress-content");

    if (!container) {
        console.error("goal-progress-content not found");
        return;
    }

    const savedProgress = Number(sessionStorage.getItem("goalCurrentProgress")) || 0;
    const savedMaxProgress = Number(sessionStorage.getItem("goalMaxProgress")) || 3;

    const percent = getPercent(savedProgress);
    const isComplete = savedProgress >= savedMaxProgress;

    // normal button text
    let buttonText = "Add One";

    // if only one step is left, show "Complete Goal" instead
    if (savedMaxProgress - savedProgress === 1) {
        buttonText = "Complete Goal";
    }

    container.innerHTML = `
        <h2 class="goal-name">${selectedGoal?.name || "Weekly Goal"}</h2>
        <p class="goal-description">
            Complete your selected goal to unlock your cheat day reward.
        </p>

        <p class="goal-progress-text">${savedProgress} / ${savedMaxProgress} (${percent}%)</p>

        <div class="full-progress-bar">
            <div class="full-progress-fill" style="width: ${percent}%"></div>
        </div>

        ${
            !isComplete
                ? `<button id="update-btn" class="goal-btn update-btn">${buttonText}</button>`
                : `<p class="goal-progress-text" style="color:#4caf50; font-weight:700;">🎉 Goal completed! Dessert unlocked.</p>`
        }
    `;

    if (!isComplete) {
        const updateBtn = document.getElementById("update-btn");
        if (updateBtn) {
            updateBtn.addEventListener("click", updateProgress);
        }
    } else {
        showSavedOrRandomDessert();
    }
}

async function updateProgress() {
    if (!currentJoinPk) return;

    try {
        const response = await fetch("/api/goals/update", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify({
                joinPk: currentJoinPk
            })
        });

        if (!response.ok) {
            throw new Error("Failed to update progress");
        }

        const data = await response.json();
        console.log("Updated progress:", data);

        currentProgress = data.progressCount || 0;
        maxProgress = data.requiredIterations || maxProgress;

        // keep sessionStorage updated too
        sessionStorage.setItem("goalCurrentProgress", currentProgress);
        sessionStorage.setItem("goalMaxProgress", maxProgress);

        renderGoal();
    } catch (err) {
        console.error("Error updating progress:", err);
        alert("Could not update goal progress.");
    }
}

function getDessertStorageKey() {
    const startDate = sessionStorage.getItem("weeklyPlanSunday");

    if (!startDate) {
        return null;
    }

    return `dessert-${startDate}`;
}

async function showSavedOrRandomDessert() {
    const savedDessertKey = getDessertStorageKey();

    if (!savedDessertKey) {
        console.error("No weeklyPlanSunday found in sessionStorage");
        return;
    }

    const savedDessert = sessionStorage.getItem(savedDessertKey);

    if (savedDessert) {
        try {
            const dessert = JSON.parse(savedDessert);
            console.log("Using saved dessert for this week:", dessert);
            renderDessert(dessert);
            return;
        } catch (err) {
            console.error("Saved dessert could not be parsed, fetching a new one.", err);
            sessionStorage.removeItem(savedDessertKey);
        }
    }

    await fetchAndSaveRandomDessert(savedDessertKey);
}

async function fetchAndSaveRandomDessert(savedDessertKey) {
    const rewardSection = document.getElementById("reward-section");
    const rewardCard = document.getElementById("reward-card");

    try {
        const response = await fetch("/api/cheat-day/random-dessert", {
            method: "GET",
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch random dessert: ${response.status}`);
        }

        const text = await response.text();

        if (!text || !text.trim()) {
            throw new Error("Random dessert endpoint returned an empty body");
        }

        const dessert = JSON.parse(text);
        console.log("Random dessert for this week:", dessert);

        sessionStorage.setItem(savedDessertKey, JSON.stringify(dessert));
        renderDessert(dessert);
    } catch (err) {
        console.error("Error fetching dessert:", err);

        if (rewardSection && rewardCard) {
            rewardSection.classList.remove("hidden");
            rewardCard.innerHTML = `
                <div class="reward-card">
                    <h3>Cheat Day Dessert</h3>
                    <p>Could not load dessert right now.</p>
                </div>
            `;
        }
    }
}

function renderDessert(dessert) {
    const rewardSection = document.getElementById("reward-section");
    const rewardCard = document.getElementById("reward-card");

    if (!rewardSection || !rewardCard) {
        console.error("reward-section or reward-card not found");
        return;
    }

    rewardSection.classList.remove("hidden");

    const button = document.createElement("button");
    button.classList.add("reward-card-btn");

    button.innerHTML = `
        <div class="reward-card">
            <h3>${dessert.name || "Cheat Day Dessert"}</h3>
            <p>${dessert.description || "Enjoy your reward."}</p>
            ${
                dessert.imagePath
                    ? `<img src="${dessert.imagePath}" alt="${dessert.name || "Dessert"}" style="width:150px; height:150px; object-fit:cover; border-radius:12px; margin-top:12px;">`
                    : ""
            }
        </div>
    `;

    button.addEventListener("click", () => {
        sessionStorage.setItem("selectedRecipeId", dessert.id);
        window.location.href = "/html/displayRecipes.html";
    });

    rewardCard.innerHTML = "";
    rewardCard.appendChild(button);
}