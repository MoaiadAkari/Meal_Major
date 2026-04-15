import { initHeader } from "./header.js";

export async function loadPartial(selector, file) {
    const container = document.querySelector(selector);
    if (!container) return;

    const response = await fetch(file);
    container.innerHTML = await response.text();
}

export async function loadLayout() {
    await loadPartial("#header", "components/header.html");
    await loadPartial("#footer", "components/footer.html");

    // IMPORTANT: initialize component JS AFTER loading
    initNavbar();
    initHeader();
}

function initNavbar() {
    const menuOpenButton = document.querySelector("#menu-open-button");
    const menuCloseButton = document.querySelector("#menu-close-button");
    const recipesLink = document.querySelector("#recipes-link");

    if (!menuOpenButton || !menuCloseButton) return;

    menuOpenButton.addEventListener("click", () => {
        document.body.classList.toggle("show-mobile-menu");
    });

    menuCloseButton.addEventListener("click", () => {
        document.body.classList.remove("show-mobile-menu");
    });

    if (recipesLink) {
        recipesLink.addEventListener("click", () => {
            sessionStorage.setItem("plannerMode", "false");
            window.location.href = "/html/recipes.html";
        });
    }
}