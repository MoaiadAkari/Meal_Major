document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("profileForm");
    const editBtn = document.getElementById("editBtn");
    const saveBtn = document.getElementById("saveBtn");
    const cancelBtn = document.getElementById("cancelBtn");
    const returnBtn = document.getElementById("returnBtn");

    let fetchedUserData = {};

    const otherMappings = [
        {
            checkboxId: "otherAllergy",
            textId: "otherAllergyText"
        },
        {
            checkboxId: "otherIntolerance",
            textId: "otherIntoleranceText"
        }
    ];

    function setFormMode(isEditing) {
        const allInputs = form.querySelectorAll("input");

        allInputs.forEach((input) => {
            if (input.id === "email") {
                input.disabled = true;
                return;
            }

            if (input.classList.contains("other-popup")) {
                return;
            }

            input.disabled = !isEditing;
        });

        updateOtherTextState(isEditing);

        editBtn.style.display = isEditing ? "none" : "inline-block";
        saveBtn.style.display = isEditing ? "inline-block" : "none";
        cancelBtn.style.display = isEditing ? "inline-block" : "none";
    }

    function updateOtherTextState(isEditing) {
        otherMappings.forEach(({ checkboxId, textId }) => {
            const checkbox = document.getElementById(checkboxId);
            const textInput = document.getElementById(textId);

            if (!checkbox || !textInput) return;

            textInput.disabled = !(isEditing && checkbox.checked);
        });
    }

    function setCheckedValues(name, values = []) {
        const checkboxes = document.querySelectorAll(`input[name="${name}"]`);
        checkboxes.forEach((checkbox) => {
            checkbox.checked = values.includes(checkbox.value);
        });
    }

    function getCheckedValues(name) {
        return Array.from(
            document.querySelectorAll(`input[name="${name}"]:checked`)
        ).map((checkbox) => checkbox.value);
    }

    function populateForm(user) {
        document.getElementById("name").value = user.name || "";
        document.getElementById("email").value = user.email || "";

        setCheckedValues("foodAllergies", user.foodAllergies || []);
        setCheckedValues("intolerances", user.intolerances || []);
        setCheckedValues("preferences", user.preferences || []);

        if ((user.foodAllergies || []).includes("other")) {
            document.getElementById("otherAllergyText").value = user.otherAllergyText || "";
        } else {
            document.getElementById("otherAllergyText").value = "";
        }

        if ((user.intolerances || []).includes("other")) {
            document.getElementById("otherIntoleranceText").value = user.otherIntoleranceText || "";
        } else {
            document.getElementById("otherIntoleranceText").value = "";
        }

        updateOtherTextState(false);
    }

    function restoreFetchedData() {
        populateForm(fetchedUserData);
        setFormMode(false);
    }

    function setupOtherCheckboxes() {
        otherMappings.forEach(({ checkboxId, textId }) => {
            const checkbox = document.getElementById(checkboxId);
            const textInput = document.getElementById(textId);

            if (!checkbox || !textInput) return;

            checkbox.addEventListener("change", () => {
                const isEditing = !document.getElementById("name").disabled;
                textInput.disabled = !(isEditing && checkbox.checked);

                if (!checkbox.checked) {
                    textInput.value = "";
                }
            });
        });
    }

    function buildPayload() {
        const data = {
            name: document.getElementById("name").value.trim(),
            email: document.getElementById("email").value.trim(),
            foodAllergies: getCheckedValues("foodAllergies"),
            intolerances: getCheckedValues("intolerances"),
            preferences: getCheckedValues("preferences"),
            otherAllergyText: document.getElementById("otherAllergyText").value.trim(),
            otherIntoleranceText: document.getElementById("otherIntoleranceText").value.trim()
        };

        if (!data.foodAllergies.includes("other")) {
            data.otherAllergyText = "";
        }

        if (!data.intolerances.includes("other")) {
            data.otherIntoleranceText = "";
        }

        return data;
    }

    async function fetchUserProfile() {
        try {
            const response = await fetch("http://localhost:8080/users/me", {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) {
                throw new Error("Failed to fetch user profile");
            }

            const user = await response.json();
            fetchedUserData = user;
            populateForm(user);
            setFormMode(false);
        } catch (error) {
            console.error("Error fetching user profile:", error);
            alert("Could not load profile information.");
        }
    }

    editBtn.addEventListener("click", () => {
        setFormMode(true);
    });

    cancelBtn.addEventListener("click", () => {
        restoreFetchedData();
    });

    returnBtn.addEventListener("click", () => {
        window.location.href = "/html/homepage.html";
    });

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const data = buildPayload();
        console.log("Data being sent:", data);

        try {
            const response = await fetch("http://localhost:8080/users/me", {
                method: "PUT",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error("Failed to update profile");
            }

            const result = await response.json();
            fetchedUserData = result;

            alert("Profile updated successfully!");
            populateForm(result);
            setFormMode(false);
        } catch (error) {
            console.error("Error updating profile:", error);
            alert("Something went wrong while saving.");
        }
    });

    setupOtherCheckboxes();
    fetchUserProfile();
});
