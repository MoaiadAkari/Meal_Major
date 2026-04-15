import { loadLayout } from "./layout.js";

document.addEventListener("DOMContentLoaded", async () => {
    await loadLayout();
    await loadGoals();
    console.log("Goals page loaded!");
});

async function loadGoals() {
    const container = document.getElementById("goals-container");

    if (!container) {
        console.error("goals-container not found");
        return;
    }

    try {
        const response = await fetch("/api/goals/random", {
            credentials: "include"
        });

        if (!response.ok) {
            console.error("Goals request failed:", await response.text());
            container.innerHTML = "<p>Could not load goals.</p>";
            return;
        }

        const goals = await response.json();
        console.log("Goals data:", goals);

        container.innerHTML = "";

        if (!goals || goals.length === 0) {
            container.innerHTML = "<p>No goals available.</p>";
            return;
        }

        for (const goal of goals) {
            createGoalCard(goal, container);
        }

    } catch (err) {
        console.error("Failed to load goals:", err);
        container.innerHTML = "<p>Error loading goals. Check console for details.</p>";
    }
}

function createGoalCard(goal, container) {
    const card = document.createElement("div");
    card.classList.add("goal-card");

    const goalName = goal.name ?? goal.title ?? "Untitled Goal";
    const goalDescription = goal.description ?? "";

    card.innerHTML = `
        <div class="goal-title">${goalName}</div>
        <div class="goal-description">${goalDescription}</div>
    `;

    card.addEventListener("click", async () => {
        await selectGoal(goal, card);
    });

    container.appendChild(card);
}

async function selectGoal(goal, card) {
    const errorMessage = document.getElementById("goal-error-message");

    if (errorMessage) {
        errorMessage.textContent = "";
        errorMessage.style.display = "none";
    }

    const startDate = sessionStorage.getItem("weeklyPlanSunday");

    if (!startDate) {
        if (errorMessage) {
            errorMessage.textContent = "Please create a weekly plan before selecting a goal.";
            errorMessage.style.display = "block";
        } else {
            alert("Please create a weekly plan before selecting a goal.");
        }
        return;
    }

    document.querySelectorAll(".goal-card").forEach(c => {
        c.classList.remove("selected");
    });
    card.classList.add("selected");

    const goalId = goal.id ?? goal.goalPk;
    const goalName = goal.name ?? goal.title ?? "Weekly Goal";

    console.log("Selecting goal:", goalId, goalName);
    console.log("Using startDate (weekly plan):", startDate);

    try {
        const response = await fetch("/api/goals/select", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify({
                goalId: goalId,
                startDate: startDate
            })
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error("Failed to save goal:", errorText);
            console.log("Payload:", { startDate, goalId });

            if (errorMessage) {
                errorMessage.textContent = errorText || "Please create a weekly plan before selecting a goal.";
                errorMessage.style.display = "block";
            } else {
                alert(errorText || "Failed to save goal. Please try again.");
            }
            return;
        }

        const data = await response.json();
        console.log("Goal saved successfully:", data);

        sessionStorage.setItem(`selectedGoalId-${startDate}`, String(goalId));
        sessionStorage.setItem("selectedGoalName", goalName);
        sessionStorage.setItem("goalData", JSON.stringify(data));

        if (data.joinPk != null) {
            sessionStorage.setItem("selectedJoinPk", String(data.joinPk));
        }

        window.location.href = "/html/goalsProgress.html";

    } catch (err) {
        console.error("Error selecting goal:", err);

        if (errorMessage) {
            errorMessage.textContent = "An error occurred while selecting the goal.";
            errorMessage.style.display = "block";
        } else {
            alert("Error selecting goal. Check console for details.");
        }
    }
}