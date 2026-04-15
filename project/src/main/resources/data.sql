TRUNCATE TABLE users RESTART IDENTITY CASCADE;
TRUNCATE TABLE recipes RESTART IDENTITY CASCADE;
TRUNCATE TABLE images RESTART IDENTITY CASCADE;
TRUNCATE TABLE recipe_descriptions RESTART IDENTITY CASCADE;
TRUNCATE TABLE recipe_ingredients RESTART IDENTITY CASCADE;
TRUNCATE TABLE ref_recipe_allergens RESTART IDENTITY CASCADE;
TRUNCATE TABLE ref_recipe_diet_health RESTART IDENTITY CASCADE;
TRUNCATE TABLE ref_recipe_diet_belief RESTART IDENTITY CASCADE;
TRUNCATE TABLE ref_difficulty RESTART IDENTITY CASCADE;
TRUNCATE TABLE user_allergies RESTART IDENTITY CASCADE;
TRUNCATE TABLE daily_recipe RESTART IDENTITY CASCADE;
TRUNCATE TABLE weekly_plan RESTART IDENTITY CASCADE;
TRUNCATE TABLE ref_goal RESTART IDENTITY CASCADE;
---//Adding truncates
TRUNCATE TABLE user_diet_preferences RESTART IDENTITY CASCADE;
TRUNCATE TABLE user_intolerances RESTART IDENTITY CASCADE;
TRUNCATE TABLE join_plan_goals RESTART IDENTITY CASCADE;

INSERT INTO users (name, email, password)
VALUES (
  'Test',
  'test@email.com',
  '12345678'
);

INSERT INTO users (name, email, password)
VALUES ('CheatDayDesserts', 'cheatday@email.com', '12345678');


INSERT INTO user_diet_preferences (user_id, diet_preference)
VALUES
    (1, 'vegan'),
    (1, 'halal');

INSERT INTO user_allergies (user_id, allergy)
VALUES
    (1, 'peanuts'),
    (1, 'shellfish');

INSERT INTO user_intolerances (user_id, intolerance)
VALUES
    (1, 'lactose'),
    (1, 'low Sodium');


------------------ Reference Table population ---------------------------

--- Populating allergen table

INSERT INTO ref_recipe_allergens (allergen)
VALUES
    ('Peanuts'),
    ('Tree Nuts'),
    ('Eggs'),
    ('Soy'),
    ('Wheat'),
    ('Fish'),
    ('Shellfish'),
    ('Sesame');

--- Populating dietary health table

INSERT INTO ref_recipe_diet_health (health)
VALUES
    ('Lactose'),
    ('Low Sugar'),
    ('Gluten-Free'),
    ('Soy'),
    ('Low Sodium');

--- Populating dietary belief table

INSERT INTO ref_recipe_diet_belief (belief)
VALUES
    ('Vegetarian'),
    ('Vegan'),
    ('Kosher'),
    ('Keto'),
    ('Halal');

--- Populating dietary belief table

INSERT INTO ref_difficulty (difficulty)
VALUES
    ('Beginner'),
    ('Easy'),
    ('Medium'),
    ('Hard'),
    ('Expert');
 

-- populate goal table
-- added required_iterations so each goal can have its own target

INSERT INTO ref_goal (goal, required_iterations)
VALUES
    ('Complete the weekly plan', 1),
    ('Go grocery shopping', 1),
    ('Go to the gym', 3),
    ('Drink 2L of water', 7),
    ('Eat a healthy meal', 5),
    ('Walk 10,000 steps', 7),
    ('Sleep 8 hours', 7),
    ('Avoid fast food today', 7),
    ('Cook a homemade meal', 3),
    ('Stretch for 10 minutes', 4);

--- //Test Data
INSERT INTO weekly_plan (user_fk, start_date, end_date, mon, tue, wed, thu, fri, sat, sun)
VALUES (1, CURRENT_DATE, CURRENT_DATE + 6, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

----------------- Recipes declaration + join table filling ---------------------

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings) VALUES ('Scrambled Eggs', '10', '15', '5', '1', '1');
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (1, 3);
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (1, 2), (1, 3), (1, 4);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (1, 1), (1, 3), (1, 5);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (1, 'To make scrambled eggs crack however many eggs you like into a pan on medium high heat with some butter. Stir frequently so it doesn''t stick and add a little milk as it cooks. Season with salt and pepper to your liking and plate up. Eat warm for the best experience.');
INSERT INTO images(recipe_fk, file_path) VALUES (1, '/sampleImages/scrambled-eggs.jpeg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (1, '3', 'Unit', 'eggs'), (1, '1', 'Tablespoon', 'butter');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings) VALUES ('Spaghetti Bolognese', '5', '10', '8', '2', '2');
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (2, 5);
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (2, 1), (2,4);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (2, 1), (2, 3), (2, 4);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (2, 'To make spaghetti bolognese cook spaghetti according to package instructions. In a separate pan brown ground beef with onions and garlic. Add tomato sauce and simmer for 15 minutes. Serve sauce over spaghetti.');
INSERT INTO images(recipe_fk, file_path) VALUES (2, '/sampleImages/spaghetti-bolognese.jpeg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (2, '2000', 'Grams', 'spaghetti'), (2, '850', 'Millilitres', 'pasta sauce');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings) VALUES ('Grilled Cheese Sandwich', '5', '15', '10', '3', '4');
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (3, 5), (3, 8);
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (3, 2), (3,4);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (3, 1), (3, 3), (3, 4);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (3, 'To make a grilled cheese sandwich butter two slices of bread and place cheese between them. Cook in a pan on medium heat until the bread is golden brown and the cheese is melted. Serve warm.');
INSERT INTO images(recipe_fk, file_path) VALUES (3, '/sampleImages/grilled-cheese.jpeg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (3, '2', 'Pieces', 'sliced cheese'), (3, '2', 'Pieces', 'sliced bread'), (3, '1', 'Teaspoon', 'butter');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings) VALUES ('Chicken Stir Fry', '12', '25', '20', '4', '3');
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (4, 5), (4, 8);
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (4, 1), (4, 2), (4,5);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (4, 3), (4, 4);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (4, 'To make chicken stir fry cut chicken into bite-sized pieces and cook in a pan with oil until browned. Add vegetables of your choice and cook until tender. Add soy sauce and stir to combine. Serve over rice or noodles.');
INSERT INTO images(recipe_fk, file_path) VALUES (4, '/sampleImages/chicken-stir-fry.jpeg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (4, '997.903', 'Grams', 'chicken'), (4, '1500', 'grams', 'dry pasta');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings) VALUES ('Struggle meal', '2', '0', '4', '1', '1');
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (5, 3), (5, 4), (5,5);
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (5, 1), (5, 2);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (5, 1),(5, 2),(5, 3),(5, 4), (5, 5);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (5, 'Open the pack of ramen and hope yor tears hydrate it as you bite into it...');
INSERT INTO images(recipe_fk, file_path) VALUES (5, '/sampleImages/ramen.jpg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (5, '1', 'Pack', 'ramen');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings, owner_user_fk) VALUES ('Pancakes', '10', '15', '6', '2', '2',2);
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (6, 3), (6, 5);
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (6, 2);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (6, 1), (6, 3), (6, 5);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (6, 'Mix flour, eggs, milk, and sugar in a bowl to form a smooth batter. Pour a small amount into a hot buttered pan and cook until bubbles form. Flip and cook the other side until golden. Serve warm with syrup or fruit.');
INSERT INTO images(recipe_fk, file_path) VALUES (6, '/sampleImages/pancakes.jpg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (6, '1', 'Cup', 'flour'), (6, '2', 'Unit', 'eggs'), (6, '1', 'Cup', 'milk');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings) VALUES ('Caesar Salad', '15', '0', '7', '1', '2');
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (7, 3), (7, 5);
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (7, 2);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (7, 1), (7, 3), (7, 5);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (7, 'Chop romaine lettuce and place in a bowl. Add croutons and grated parmesan cheese. Drizzle Caesar dressing over the salad and toss until evenly coated. Serve fresh.');
INSERT INTO images(recipe_fk, file_path) VALUES (7, '/sampleImages/caesar-salad.jpg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (7, '1', 'Head', 'romaine lettuce'), (7, '50', 'Grams', 'croutons'), (7, '30', 'Grams', 'parmesan cheese');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings) VALUES ('Beef Tacos', '15', '20', '12', '2', '3');
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (8, 5);
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (8, 5);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (8, 3), (8, 5);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (8, 'Cook ground beef in a pan with taco seasoning until browned. Warm taco shells and fill them with the cooked beef. Add toppings like lettuce, cheese, and tomatoes. Serve immediately.');
INSERT INTO images(recipe_fk, file_path) VALUES (8, '/sampleImages/beef-tacos.jpg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (8, '300', 'Grams', 'ground beef'), (8, '6', 'Pieces', 'taco shells'), (8, '50', 'Grams', 'shredded lettuce');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings) VALUES ('Vegetable Omelette', '5', '10', '4', '1', '1');
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (9, 3);
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (9, 2), (9, 3);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (9, 1), (9, 3), (9, 5);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (9, 'Beat eggs in a bowl and pour them into a heated pan. Add chopped vegetables such as peppers, onions, and spinach. Cook until the eggs are set, fold the omelette, and serve warm.');
INSERT INTO images(recipe_fk, file_path) VALUES (9, '/sampleImages/vegetable-omelette.jpg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (9, '2', 'Unit', 'eggs'), (9, '50', 'Grams', 'mixed vegetables'), (9, '1', 'Teaspoon', 'oil');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings) VALUES ('Baked Salmon', '10', '20', '15', '2', '2');
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (10, 6);
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (10, 5);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (10, 3), (10, 4), (10, 5);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (10, 'Place salmon fillets on a baking tray and season with salt, pepper, and lemon juice. Bake in the oven at 180°C for about 20 minutes until cooked through. Serve with vegetables or rice.');
INSERT INTO images(recipe_fk, file_path) VALUES (10, '/sampleImages/baked-salmon.jpg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (10, '2', 'Pieces', 'salmon fillets'), (10, '1', 'Tablespoon', 'lemon juice'), (10, '1', 'Teaspoon', 'olive oil');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings) VALUES ('Chicken Alfredo Pasta', '10', '20', '13', '3', '3');
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (11, 5);
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (11, 1);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (11, 3), (11, 5);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (11, 'Cook pasta according to package instructions. In a pan cook chicken pieces until browned, then add Alfredo sauce. Combine the pasta with the sauce and chicken, mix well, and serve hot.');
INSERT INTO images(recipe_fk, file_path) VALUES (11, '/sampleImages/chicken-alfredo.jpg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (11, '200', 'Grams', 'pasta'), (11, '200', 'Grams', 'chicken breast'), (11, '150', 'Millilitres', 'alfredo sauce');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings) VALUES ('Greek Salad', '10', '0', '6', '1', '2');
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (12, 8);
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (12, 2);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (12, 1), (12, 3), (12, 5);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (12, 'Chop tomatoes, cucumber, and red onion. Add olives and feta cheese. Drizzle with olive oil and toss lightly before serving.');
INSERT INTO images(recipe_fk, file_path) VALUES (12, '/sampleImages/greek-salad.jpg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (12, '2', 'Unit', 'tomatoes'), (12, '1', 'Unit', 'cucumber'), (12, '50', 'Grams', 'feta cheese');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings) VALUES ('Avocado Toast', '5', '5', '4', '1', '1');
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (13, 5);
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (13, 2);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (13, 1), (13, 2), (13, 3), (13, 5);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (13, 'Toast slices of bread until golden. Mash avocado in a bowl with salt and pepper. Spread the avocado mixture over the toast and serve immediately.');
INSERT INTO images(recipe_fk, file_path) VALUES (13, '/sampleImages/avocado-toast.jpg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (13, '1', 'Unit', 'avocado'), (13, '2', 'Pieces', 'bread slices'), (13, '1', 'Pinch', 'salt');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings) VALUES ('Vegetable Fried Rice', '10', '15', '7', '2', '2');
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (14, 4);
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (14, 2);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (14, 1), (14, 2), (14, 3), (14, 5);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (14, 'Heat oil in a pan and add cooked rice. Stir in mixed vegetables and soy sauce. Cook while stirring until everything is heated through and well mixed.');
INSERT INTO images(recipe_fk, file_path) VALUES (14, '/sampleImages/vegetable-fried-rice.jpg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (14, '2', 'Cups', 'cooked rice'), (14, '100', 'Grams', 'mixed vegetables'), (14, '2', 'Tablespoons', 'soy sauce');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings, owner_user_fk) VALUES ('Banana Smoothie', '5', '0', '3', '1', '1', 2);
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (15, 2);
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (15, 2);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (15, 1), (15, 3), (15, 5);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (15, 'Place banana, milk, and ice into a blender. Blend until smooth and creamy. Pour into a glass and serve chilled.');
INSERT INTO images(recipe_fk, file_path) VALUES (15, '/sampleImages/banana-smoothie.jpg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (15, '1', 'Unit', 'banana'), (15, '1', 'Cup', 'milk'), (15, '4', 'Cubes', 'ice');

-- desert recipes (associated with user_2 only)

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings, owner_user_fk) VALUES ('Chocolate Lava Cake', 20, 15, 7, 2, 2, 2);
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (16, 3), (16, 5);  
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (16, 2); 
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (16, 2); 
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (16, 'A rich and gooey chocolate cake with a molten center. Serve warm.');
INSERT INTO images(recipe_fk, file_path) VALUES (16, '/sampleImages/chocolate-lava-cake.jpg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (16, '100', 'Grams', 'dark chocolate'), (16, '2', 'Unit', 'eggs'), (16, '50', 'Grams', 'flour'), (16, '50', 'Grams', 'sugar');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings, owner_user_fk) VALUES ('Strawberry Cheesecake', 30, 60, 10, 3, 4, 2);
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (17, 3), (17, 5);  
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (17, 2);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (17, 1), (17, 2);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (17, 'Creamy cheesecake topped with fresh strawberries on a buttery crust.');
INSERT INTO images(recipe_fk, file_path) VALUES (17, '/sampleImages/strawberry-cheesecake.jpg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (17, '200', 'Grams', 'cream cheese'), (17, '2', 'Unit', 'eggs'), (17, '100', 'Grams', 'sugar'), (17, '150', 'Grams', 'strawberries'), (17, '100', 'Grams', 'crust');

INSERT INTO recipes(name, prepTime, cookTime, cost, difficulty, servings, owner_user_fk) VALUES ('Chocolate Chip Cookies', 15, 10, 5, 2, 12, 2);
INSERT INTO join_recipes_allergens(recipe_fk, allergen_fk) VALUES (18, 3), (18, 5);  
INSERT INTO join_recipes_diet_health(recipe_fk, health_fk) VALUES (18, 2);
INSERT INTO join_recipes_diet_belief(recipe_fk, belief_fk) VALUES (18, 1), (18, 2);
INSERT INTO recipe_descriptions(recipe_fk, description) VALUES (18, 'Classic cookies loaded with chocolate chips, soft in the middle and slightly crispy on the edges.');
INSERT INTO images(recipe_fk, file_path) VALUES (18, '/sampleImages/chocolate-chip-cookies.jpg');
INSERT INTO recipe_ingredients(recipe_fk, amount, unit, ingredient_desc) VALUES (18, '200', 'Grams', 'flour'), (18, '100', 'Grams', 'sugar'), (18, '100', 'Grams', 'chocolate chips'), (18, '1', 'Unit', 'egg'), (18, '50', 'Grams', 'butter');