# Canadian_Coders-SOEN341_Project_W26

Hello, this is the Canadian Coders' README file. The following text contains the project project as well as team's overall organization for the wedapp MealMajor project. 

First, MealMajor is a web app designed for students to plan their meals weekly, to track their goals and propose easy recipes. Students are often very busy throughout their semester due to classes as well as their extracurriculars. Not to mention, many students have to work to be able to pay for their education, their rent and their overall costs. Due to their busy schedule, planning their healthy and affordale meals throughout the week can become a very complex and time-consumming task, Therefeore, MealMajor is designed to facilitate their meal planning so that each meal can be easy, delicious, time and cost-effective recipes. 

This web app has multiples features: user account management, recipes management, Weekly Meal Planner as well as a cheat day feature measured with the completion of goals. Each feature contains their own attributes to ensure that it meets the needs of its users. 

1. User Account Management
User registration and login 
Profile management (diet preferences, allergies)

2. Recipe Management
Create, edit, and delete recipes. 
Recipe attributes (ingredients, prep. time, prep. steps, cost, etc.)
Search recipes
Filter recipes (Time, difficulty, cost, dietary tag, etc.)

3. Weekly Meal Planner
Create a weekly meal plan
View meals in a weekly grid
Assign recipes to:
    Day of the week
    Meal type (breakfast/lunch/dinner/snack)
Edit or remove meals from the planner
Prevent duplicates for the same week

4. Cheat Day Goals
Chooses a weekly goal for a specific week after creating a weekly plan
Can track and update the chosen goal's progress
Once goal completed, a dessert can be claimed as a reward


The Canadian Coders are composed of 6 members: 
Hirdayraj Singh Nanda (40305593), @Hirdayraj
Moaiad Akari (40328240), @MoaiadAkari
Colin Haller (40278545), @C4ller/ @Colin
Gabriel Gallant (40328473), @gabrielgallant
Lizzie Nicole Gudino cabrera (40272383), @lizziengc
Brice Bryan Iteka (40246704), @Bryanoff


Scrum Master: Colin Haller (40278545) 

Frontend team members: 
Lizzie Nicole Gudino Cabrera (40272383)-Aerospace Engineering Option C
Moaiad Akari (40328240) -Software Engineering
Gabriel Gallant (40328473) -Computer Engineering

This team was in charge of the overall web design of the login, create account, forgot password, reset password, user profile

Backend team members: 
Hirdayraj Singh Nanda (40305593) -Computer Engineering
Brice Bryan Iteka (40246704)-Aerospace Engineering Option C
Colin Haller (40278545) -Software Engineering

This team was in charge of the responses from the database to the frontend. 

DEMO directory path: project/src/main/resources/static





Sprint planning: https://docs.google.com/spreadsheets/d/1GyNEZeTK3oJ4r6cRD-WDmmkfsJ5wTx2zESemJNNLPSg/edit?gid=579141764#gid=579141764 

Contribution logs: https://docs.google.com/spreadsheets/d/1avXwzBBmENpV3YT3uQh9wD3rJz7_xi0lNP-Gs7hFBaM/edit?gid=0#gid=0

Wireframe: https://preview.uxpin.com/d8ac40541dab051434a872187c6b0bde5e808e4c#/pages/219239947/simulate/sitemap

NAMING CONVENTIONS:

- For Java and JavaScript, the naming conventions that we use for our project are camelCase and snake_case.
- The Java files also use naming from Java naming conventions.
- For constants in JavaScript the UPPER_SNAKE_CASE is particularly used, and standard snake_case is mainly used in the     Java code for DTO/PK naming, but some of them also use camelCase. 
- The class and ID names in HTML and CSS use the kebab-case naming convention. 

These naming conventions are nearly universally followed throughout the project. The reason why camelCase and snake_case was chosen for Java and JavaScript is that it is easy to remember, popular and used in many of the tutorials that we used to learn. The kebab-case was chosen for HTML and CSS for similar reasons.

camelCase: https://developer.mozilla.org/en-US/docs/Glossary/Camel_case

snake_case: https://developer.mozilla.org/en-US/docs/Glossary/Snake_case

Java naming conventions: https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html

kebab-case: https://developer.mozilla.org/en-US/docs/Glossary/Kebab_case

FRONTEND: 
The design was done with HTML, CSS and Javascript. "login.html" contains the login, create account, forgot password and reset program. The User Profile is programmed with the "userProfile.html". 

keep "USE_MOCK_API = true" for dev testing
For full app testing turn it false (requires working project)
for fetch destination make it http://localhost:8080/*name of your feature* (signup for signup page for example)


BACKEND:

We will be using Java 17 for this project because it is the newest tested and compatible version with PostgreSQL and Spring. If you do not switch to Java 17 you might encounter weird setup bugs and issues down the line. 

Your setup should be the following:

Eclipse: File → Import → Maven → Existing Maven Projects → select project/ (should be somewhere in your filesystem)
IntelliJ: Open -> select project/pom.xml
VSCode: Install Java and Maven extensions if not present the rest should just work from the repo clone

If you suddenly see thousands of changes it means the gitignore has missed some setup files. Find where the thousands are coming from, add the path to the gitignore, save it and relaunch your IDE and they shoudl go away.

Make sure you are using a PostgreSQL database names SOEN341 with user soen341_admin (you probably need to create that user before the database) and password SOEN341

To run the the spring boot app all you have to do is run projectApplication.java in your IDE. To code however make a controller (tutorials online) with the endpoint as the name of your feature (/signup for signup for example)

We are running on port 8080 (defined by default)

DEMO OF THE SIGNUP FEATURE:

https://youtu.be/DYu4uF5wvKU


References:
https://www.youtube.com/watch?v=Kuw8GJEVH0I&t=4694s
https://www.youtube.com/watch?v=Rmx4ill_iHg 
https://www.youtube.com/watch?v=ubw2hdQIl4E&t=448s 
https://www.youtube.com/watch?v=fGYQJAlLD68
https://www.youtube.com/watch?v=jeMt1DuDh28. 


