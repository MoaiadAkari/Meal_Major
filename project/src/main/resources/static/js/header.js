// backend/src/main/resources/static/js/header.js
export function initHeader() {

   const logoutBtn = document.getElementById("logout");
    if (logoutBtn) {
        console.log("[Header] Logout button found, attaching listener");
        logoutBtn.addEventListener("click", logout);
    } else {
        console.warn("[Header] Logout button not found");
    }


    console.log("[Header] initHeader called");

    const goalBtn = document.getElementById("nav-goals");
    if (!goalBtn) {
        console.warn("[Header] Goal button not found");
        return;
    }

    console.log("[Header] Goal button found, attaching listener");
    goalBtn.addEventListener("click", async (e) => {
        e.preventDefault();
        console.log("[Header] ⭐️ Goals clicked");

        // Get current week's Sunday
       let startDate = sessionStorage.getItem("weeklyPlanSunday");
       console.log("[DEBUG] weeklyPlanSunday from sessionStorage:", startDate); 

    if (!startDate) {
    console.warn("[Header] No weeklyPlanSunday found");

    // fallback if needed
        const today = new Date();
        const sunday = new Date(today);
        sunday.setDate(today.getDate() - today.getDay());
        startDate = sunday.toISOString().split("T")[0];
    }
        console.log("[Header] startDate:", startDate);

        try {
            console.log("[DEBUG] weeklyPlanSunday in session:", sessionStorage.getItem("weeklyPlanSunday"));
            // Call backend to check if user has a goal for this week
            const response = await fetch(`/api/goals/exists?startDate=${startDate}`, {
                credentials: "include"
            });

            if (!response.ok) {
                const text = await response.text();
                throw new Error(text);
            }

            const hasGoal = await response.json();
            console.log("[Header] API response (hasGoal):", hasGoal);

            if (hasGoal === true) {
                console.log("[Header] Redirect → /html/goalsProgress.html");
                window.location.href = "/html/goalsProgress.html";
            } else {
                console.log("[Header] Redirect → /html/goalsChoose.html");
                window.location.href = "/html/goalsChoose.html";
            }

        } catch (err) {
            console.error("[Header] Error checking goal:", err);
            alert("Failed to check goal.");
        }
    });
}

function logout(event) {

    event.preventDefault();

    fetch("/logOut", {
        method: "POST",
        credentials: "include",
    })
    .then(res => res.text())
    .then(() => {
        window.location.href = "/html/login.html";
    })
    .catch(err => {
        console.error(err);
        alert("Logout failed");
    });
}