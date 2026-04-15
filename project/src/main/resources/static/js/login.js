const token = new URLSearchParams(window.location.search).get("token");

const isUUID = token =>
  typeof token === "string" &&
  /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i.test(token);

if (isUUID(token)) {
  validateEmailReset(token);
}

function removeTokenFromUrl() {
  const url = new URL(window.location);
  url.searchParams.delete("token");
  window.history.replaceState({}, document.title, url.pathname);
}

function removeSignupTokenFromUrl() {
  const url = new URL(window.location);
  url.searchParams.delete("signuptoken");
  window.history.replaceState({}, document.title, url.pathname);
}

function showSignup() {
  document.getElementById("loginForm").classList.remove("active");
  document.getElementById("signupForm").classList.add("active");
  document.getElementById("forgotForm").classList.remove("active");

  const imageContent = document.querySelector(".image-content");
  imageContent.innerHTML = `
     <h2>MealMajor</h2>
        <p>
            Sign up to start your journey of meal planning with us today
        </p>
    `; 
}

function showLogin() {
  document.getElementById("loginForm").classList.add("active");
  document.getElementById("signupForm").classList.remove("active");
  document.getElementById("forgotForm").classList.remove("active");
  document.getElementById("resetForm").classList.remove("active");

  const imageContent = document.querySelector(".image-content");
  imageContent.innerHTML = `
     <h2>MealMajor</h2>
        <p>
            Sign into your account to continue your meal planning journey 
        </p>
    `;
}

// eslint-disable-next-line no-unused-vars
function showForgotPassword() {
  document.getElementById("loginForm").classList.remove("active");
  document.getElementById("signupForm").classList.remove("active");
  document.getElementById("forgotForm").classList.add("active");
 

  const imageContent = document.querySelector(".image-content");
  imageContent.innerHTML = `
     <h2>Don't Worry!</h2>
        <p>
           We'll help you reset your password and get back into your account back safely!
        </p>
     `;
}

function showUserForm(signuptoken) {
  console.log("redirect");
 window.location.href="/html/userProfile.html?signuptoken="+signuptoken;
}

async function showResetForm() {
  document.getElementById("loginForm").classList.remove("active");
  document.getElementById("signupForm").classList.remove("active");
  document.getElementById("forgotForm").classList.remove("active");

  document.getElementById("resetForm").classList.add("active");

  const imageContent = document.querySelector(".image-content");
  imageContent.innerHTML = `
    <h2>Reset Password</h2>
    <p>Choose a new password to secure your account</p>
  `;

  removeTokenFromUrl();
}

function showHomepage() {
 window.location.href="/html/homepage.html";
}

const path = window.location.pathname;

if (
  path === "/" ||
  path === "/login.html" ||
  path === "/html/login.html"
)
{
document
  .getElementById(`loginFormSubmit`)
  .addEventListener("submit", function (e) {
    e.preventDefault(); /* prevents the website to reload and to go into another page */
    handleFormSubmit(this, "Signing in...");
  });

document
  .getElementById(`signupFormSubmit`)
  .addEventListener("submit", function (e) {
    e.preventDefault();
    if (validateSignupForm(this)) {
      /* prevents the website to reload and to go into another page */
      handleFormSubmit(this, "Creating account...");
    }
  });

document
  .getElementById(`forgotFormSubmit`)
  .addEventListener("submit", function (e) {
    e.preventDefault();
    handleFormSubmit(this, "Sending instructions...");
  });

  document
  .getElementById(`resetFormSubmit`)
  .addEventListener("submit", function (e) {
    e.preventDefault();
    handleFormSubmit(this, "Updating password...");
  });

  document.getElementById("previousBtn").addEventListener("click", (e) => {
    e.preventDefault(); // prevents any default action
    showSignup();  // switches from UserForm back to SignupForm

    const signupButton = document.querySelector("#signupFormSubmit .btn-primary");
    const loading = signupButton.querySelector(".loading");
    const btnText = signupButton.querySelector(".btn-text");

    loading.classList.remove("show");
    signupButton.disabled = false;
    btnText.textContent = "Create Account"; // reset text
});
}
if (path.includes("/html/userProfile.html")){
 document
  .getElementById(`userProfileFormSubmit`)
  .addEventListener("submit", function(e) {
    e.preventDefault(); // stop reload
    handleFormSubmit(this, "Saving your profile..."); // custom loading text
  });

//-------------------Added for the "Other" box pop up feature for user profile form-------------------
 setupOtherToggle("otherAllergy", "otherAllergyText");
setupOtherToggle("otherIntolerance", "otherIntoleranceText");

}

function validatePasswordMatch(form, passwordSelector, confirmSelector)
{
const password= form.querySelector(passwordSelector).value;
const confirmPassword = form.querySelector(confirmSelector).value;

if (password !== confirmPassword) {
    showError("Passwords do not match");
    return false;
  }

  if (password.length < 8) {
    showError("Passwords must be at least 8 characters long!");
    return false;
  }
  return true;
}

function validateSignupForm(form) {
  if (!validatePasswordMatch(form, 'input[name="newPassword"]', 'input[name="confirmPassword"]')) {
        return false; 
    }
 return true;
}

async function handleFormSubmit(form, loadingText) {
  const button = form.querySelector(".btn-primary");
  const loading = button.querySelector(".loading");
  const btnText = button.querySelector('.btn-text');
  const originalText = btnText.textContent;

  /* show loading spinner*/ 
  loading.classList.add("show");
  btnText.textContent = loadingText;
  button.disabled = true;

    if (form.id === "loginFormSubmit") {
      const data ={ 
        email: form.querySelector('input[type="email"]').value,
        password: form.querySelector('input[type="password"]').value
      };
    try {
      const result= await UserInput("http://localhost:8080/login", data)
    
  
      if (result.success){

        if(result.userId){
          sessionStorage.setItem("userId", result.userId);
        }

        showSuccessMessage("Login Successful! Redirecting...");
        showHomepage();
      }
      else {
        showError("Incorrect Email or Password! Please try again.");
      }
      
      }
        catch (error) {
        console.error(error.stack); //change here
        showError("Network error");        
       
      }
      finally  {
        loading.classList.remove("show");
        button.disabled = false;
        btnText.textContent = originalText;
    } 
  
    } else if (form.id === "signupFormSubmit") {
      if (!validatePasswordMatch(form,'input[name="newPassword"]', 'input[name="confirmPassword"]')) {
      return; 
  }

      const data = {
        firstName: form.querySelector('input[placeholder="First Name"]').value,
        lastName: form.querySelector('input[placeholder="Last Name"]').value,
        email: form.querySelector('input[placeholder="Email Address"]').value,
        dob: form.querySelector('#dob').value,
        sex: form.querySelector('#sex').value,
        password: form.querySelector('#signupForm input[name="newPassword"]').value,
        confirmPassword: form.querySelector('#signupForm input[name="confirmPassword"]').value,
      };

  try {
      const result= await UserInput("http://localhost:8080/signup", data)
    
  console.log(result);
        if (result.success){
     showSuccessMessage( "Account created! Now set your profile info.");
        
      console.log("yup");
      setTimeout(() => {
       showUserForm(result.token);
      }, 1500);
      
      }
      else {
        showError(result.message|| "Sign up failed");
      }
    

      }
      catch (error) {
        console.error(error.stack); //change here
        showError("Network error");        
       
      }
      finally  {
        loading.classList.remove("show");
        button.disabled = false;
        btnText.textContent = originalText;
    } 


      
    } else if (form.id === "userProfileFormSubmit") {
      const signuptoken = new URLSearchParams(window.location.search).get("signuptoken");
      if (signuptoken) {
        removeSignupTokenFromUrl();
      }

      const allergies = Array.from(form.querySelectorAll('input[name="foodAllergies"]:checked'))
        .map(input => input.value);
      const intolerances = Array.from(form.querySelectorAll('input[name="intolerances"]:checked'))
        .map(input => input.value);
      const dietPrefs = Array.from(form.querySelectorAll('input[name="preferences"]:checked'))
        .map(input => input.value);

      // combine with signup data
      const data = {
        token : signuptoken,
        preferences : {
        allergies: allergies,
        dietPreferences: dietPrefs,
        intolerances: intolerances
        }
      };
      try {
      const result= await UserInput("http://localhost:8080/userPreferences", data)
    
  
        if (result.success){

          if (result.userId) {
            sessionStorage.setItem("userId", result.userId);
          }
          
          showSuccessMessage("Sign Up Successful! Redirecting...");
          showHomepage();

      }
      else {
        console.log(result);
        showError(result.message || "Sign up failed");
      }
      setTimeout(() => {
        window.location.href="/";
      }, 1500);
      

      }
      catch (error) {
        console.error(error.stack); //change here
        showError("Network error");        
       
      }
      finally  {
        loading.classList.remove("show");
        button.disabled = false;
        btnText.textContent = originalText;
    } 

     
    } else if(form.id === "resetFormSubmit"){

     if (!validatePasswordMatch(form,'input[name="newPassword"]', 'input[name="confirmPassword"]')) {
      console.log("passwords don't match");
    loading.classList.remove("show");
    button.disabled = false;
    btnText.textContent = originalText;
    return;
    
  }
      try {
    const data ={ 
        newPassword: form.querySelector('input[name="newPassword"]').value,
        confirm_password: form.querySelector('input[name="confirmPassword"]').value,
        token : token
      };
      const result= await UserInput("http://localhost:8080/reset-password",data )
      
  
        showSuccessMessage(result.message);   
        showLogin();
      
      }
        catch (error) {
        console.error(error.stack); //change here
        showError("Network error");        
       
      }
      finally  {
        loading.classList.remove("show");
        button.disabled = false;
        btnText.textContent = originalText;
    } 
    }
    else {
    const email = form.querySelector('input[type="email"]').value;
    try {
        const result = await fetch("http://localhost:8080/reset-request", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email })
        }).then(res => res.json());

        if (result.success) {
            showSuccessMessage(result.message);
        } else {
            showError(result.message || "Failed to send reset instructions");
        }
    } catch (error) {
        showError("Network error");
        console.log(error);
    } finally {
        loading.classList.remove("show");
        button.disabled = false;
        btnText.textContent = originalText;
    };

}}

function validateEmailReset(token) {

  // Send token to backend to validate it
  fetch("http://localhost:8080/reset-password/validate?token="+token, {
  })
  .then(res => res.json())
  .then(data => {
    console.log(data);
  if (data.valid) {
    showResetForm(); // <-- show the second page
  } else {
    showError("Reset token is invalid or expired.");
  }
  });
  }

function showSuccessMessage(message) {
  const notification = document.createElement("div");
  notification.style.cssText = `
        position: fixed;
                top: 20px;
                right: 20px;
                background: #10b981;
                color: white;
                padding: 16px 24px;
                border-radius: 12px;
                font-weight: 600;
                box-shadow: 0 10px 25px rgba(16, 185, 129, 0.3);
                z-index: 1000;
                animation: slideInRight 0.5s ease;
        `;
  notification.textContent = message;
  document.body.appendChild(notification);

  setTimeout(() => {
    notification.remove();
  }, 4000);
}

function showError(message) {
  const notification = document.createElement("div");
  notification.style.cssText = `
        position: fixed;
                top: 20px;
                right: 20px;
                background: #b9102f;
                color: white;
                padding: 16px 24px;
                border-radius: 12px;
                font-weight: 600;
                box-shadow: 0 10px 25px rgba(16, 185, 129, 0.3);
                z-index: 1000;
                animation: slideInRight 0.5s ease;
        `;
  notification.textContent = message;
  document.body.appendChild(notification);

  setTimeout(() => {
    notification.remove();
  }, 4000);
}

// eslint-disable-next-line no-unused-vars
function socialLogin(provider) {
  const providerName = provider.charAt(0).toUpperCase() + provider.slice(1);
  showSuccessMessage(`${providerName} login initiated!`);
}

// -----------------------add the "Other" box pop up feature -------------------------------------------------

function setupOtherToggle(checkboxId, textInputId) {
  const checkbox = document.getElementById(checkboxId);
  const textInput = document.getElementById(textInputId);

  if (!checkbox || !textInput) return;

  checkbox.addEventListener('change', () => {
    if (checkbox.checked) {
      textInput.style.display = 'block';
      setTimeout(() => textInput.classList.add('show'), 10);
    } else {
      textInput.classList.remove('show');
      setTimeout(() => {
        if (!checkbox.checked) textInput.style.display = 'none';
      }, 300);
    }
  });
}

//--------------------- End of the "Other" box pop up feature -----------------------------------------------


document.querySelectorAll(`.form-control`).forEach((input) => {
  input.addEventListener("focus", function () {
    this.parentNode.querySelector("i").style.color = "#3b82f6";
    this.classList.add("focused");
  });

  input.addEventListener("blur", function () {
    if (!this.value) {
      this.parentNode.querySelector("i").style.color = "#94a3b8";
    }
    this.classList.remove("focused");
  });

  input.addEventListener("input", function () {
    if (this.type === "email") {
      const isSuccess = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.value);
      this.classList.toggle("success", isSuccess && this.value.length > 0);
      this.classList.toggle("error", !isSuccess && this.value.length > 0);
    }
  })
})

    window.addEventListener('load', function() {
            document.querySelector('.container').style.animation = 'fadeIn 1s ease';
        })
document.addEventListener('keydown', function(e) {
            if (e.altKey && e.key === 's') {
                e.preventDefault();
                showSignup();
            } else if (e.altKey && e.key === 'l') {
                e.preventDefault();
                showLogin();
            }
        })


      document.querySelectorAll('.social-btn').forEach(btn => {
            btn.addEventListener('mouseenter', function() {
                this.style.transform = 'translateY(-3px) scale(1.02)';
            })

        })

function UserInput(url, data){
    return fetch (url, {
          method: "POST",
          credentials : "include",
          headers: {"Content-Type": "application/json"},
          body: JSON.stringify(data)
        })
        .then (async(response)=>{ 
       
        let json;
        try {
          json=await response.json();
        } catch(e){
          json = { success: false, message: "Invalid response from server" };
          console.log(e);
        }
  
          
        //  if (!response.ok)
        // {
         
    
         //   throw new Error ('Network Response failed');
         // }

      //return response.json();
    return {
  success: json.success ?? false,  
  message: json.message || "Request failed",
  token: json.token || null,
  userId: json.userId || null
};
       });
}
  