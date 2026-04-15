document.addEventListener("DOMContentLoaded", () => {

    let recipeId = sessionStorage.getItem("selectedRecipeId") || "";
    recipeId = parseInt(recipeId.replace(/[^0-9]/g, ""), 10);

    if (!recipeId) {
        alert("No recipe selected.");
        return;
    }

    const form = document.getElementById('edit-recipe-form');
    const addBtn = document.getElementById('add-ingredient-btn');

    const uploadBox = document.querySelector(".upload-box");
    const uploadText = document.getElementById("uploadText");
    const imageInput = document.getElementById("imageInput");

    let currentImage = null;

    fetch(`http://localhost:8080/api/recipes/${recipeId}`)
        .then(res => res.json())
        .then(recipe => {

            document.getElementById('recipe-name').value = recipe.name || "";
            document.getElementById('prep-time').value = recipe.preptime || "";
            document.getElementById('cook-time').value = recipe.cooktime || "";
            document.getElementById('recipe-cost').value = recipe.cost || "";
            document.getElementById('serving-size').value = recipe.servings || "";
            document.getElementById('difficulty').value = recipe.difficulty || "";
            document.getElementById('prep-steps').value = recipe.description || "";

            /* ---------- EXISTING IMAGE ---------- */

            if (recipe.file_path) {
                const previewImage = document.createElement("img");
                previewImage.classList.add("upload-preview");
                previewImage.src = recipe.file_path;

                uploadBox.appendChild(previewImage);
                currentImage = recipe.file_path;

                if (uploadText) uploadText.style.display = "none";
            }

            /* ---------- CHECKBOX GROUPS ---------- */

            [
                { domId: "dietary-restrictions", key: "dietaryRestrictions" },
                { domId: "intolerances", key: "intolerances" },
                { domId: "preferences", key: "preferences" }
            ].forEach(group => {

                const container = document.getElementById(group.domId);
                if (!container) return;

                const values = recipe[group.key] || [];

                container.querySelectorAll('input[type=checkbox]').forEach(cb => {
                    cb.checked = values.includes(cb.value);
                });
            });

            /* ---------- INGREDIENTS ---------- */

            const container = document.getElementById('ingredients-container');
            container.querySelectorAll('.ingredient-input').forEach(el => el.remove());

            (recipe.ingredients || []).forEach(ing => {
                const input = document.createElement('input');
                input.type = "text";
                input.value = ing;
                input.classList.add('ingredient-input');
                input.style.display = "block";
                input.style.marginTop = "5px";
                container.insertBefore(input, addBtn);
            });

        })
        .catch(err => console.error("Fetch recipe error:", err));

    /* ---------- ADD INGREDIENT ---------- */

    addBtn.addEventListener('click', () => {
        const input = document.createElement('input');
        input.type = "text";
        input.classList.add('ingredient-input');
        input.style.display = "block";
        input.style.marginTop = "5px";
        addBtn.parentNode.insertBefore(input, addBtn);
    });

    /* ---------- IMAGE UPLOAD PREVIEW ---------- */

    imageInput.addEventListener("change", function () {

        const file = this.files[0];
        if (!file) return;

        const reader = new FileReader();

        reader.onload = function (e) {

            let previewImage = document.querySelector(".upload-preview");

            if (!previewImage) {
                previewImage = document.createElement("img");
                previewImage.classList.add("upload-preview");
                uploadBox.appendChild(previewImage);
            }

            previewImage.src = e.target.result;
            currentImage = e.target.result;

            if (uploadText) uploadText.style.display = "none";
        };

        reader.readAsDataURL(file);
    });

    /* ---------- DROPDOWNS ---------- */

    document.querySelectorAll(".dropdown-btn").forEach(button => {
        button.addEventListener("click", () => {

            const targetId = button.getAttribute("data-target");
            const dropdown = document.getElementById(targetId);

            document.querySelectorAll(".options").forEach(opt => {
                if (opt !== dropdown) {
                    opt.style.display = "none";
                }
            });

            dropdown.style.display =
                dropdown.style.display === "block" ? "none" : "block";
        });
    });

    document.addEventListener("click", (e) => {
        if (!e.target.closest(".multi-select")) {
            document.querySelectorAll(".options").forEach(opt => {
                opt.style.display = "none";
            });
        }
    });

    /* ---------- FORM SUBMIT ---------- */

    form.addEventListener('submit', async (e) => {

        e.preventDefault();

        const getChecked = id =>
            Array.from(document.getElementById(id)
                .querySelectorAll('input[type=checkbox]:checked'))
                .map(cb => cb.value);

        const payload = {
            id: recipeId,
            name: document.getElementById('recipe-name').value,
            preptime: parseInt(document.getElementById('prep-time').value),
            cooktime: parseInt(document.getElementById('cook-time').value),
            cost: parseFloat(document.getElementById('recipe-cost').value) || 0,
            servings: parseInt(document.getElementById('serving-size').value),
            difficulty: document.getElementById('difficulty').value,
            description: document.getElementById('prep-steps').value,
            dietaryRestrictions: getChecked('dietary-restrictions'),
            intolerances: getChecked('intolerances'),
            preferences: getChecked('preferences'),
            ingredients: Array.from(document.querySelectorAll('.ingredient-input'))
                .map(i => i.value),
            image: currentImage
        };

        try {

            const res = await fetch(`http://localhost:8080/api/recipes/${recipeId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                const text = await res.text();
                throw new Error(`Server error ${res.status}: ${text}`);
            }

            sessionStorage.setItem("selectedRecipeId", recipeId);

            window.location.href = "/html/displayRecipes.html";

        } catch (err) {
            console.error("Update failed:", err);
            alert("Failed to update recipe.");
        }
    });

});
