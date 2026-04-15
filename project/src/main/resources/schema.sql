-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_pk BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    token VARCHAR(150),
    date_of_birth DATE,
    sex VARCHAR(25)
);
  
-- Diet preferences
CREATE TABLE IF NOT EXISTS user_diet_preferences (
    user_id BIGINT NOT NULL,
    diet_preference VARCHAR(255),
    CONSTRAINT fk_diet_user FOREIGN KEY (user_id)
        REFERENCES users(user_pk)
        ON DELETE CASCADE
);

-- Allergies
CREATE TABLE IF NOT EXISTS user_allergies (
    user_id BIGINT NOT NULL,
    allergy VARCHAR(255),
    CONSTRAINT fk_allergy_user FOREIGN KEY (user_id)
        REFERENCES users(user_pk)
        ON DELETE CASCADE
);

-- Intolerances
CREATE TABLE IF NOT EXISTS user_intolerances (
    user_id BIGINT NOT NULL,
    intolerance VARCHAR(255),
    CONSTRAINT fk_intolerance_user FOREIGN KEY (user_id)
        REFERENCES users(user_pk)
        ON DELETE CASCADE
);

------------------------- REFERENCE TABLES -------------------------

-- Recipe allergens reference
CREATE TABLE IF NOT EXISTS ref_recipe_allergens (
    allergen_pk INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    allergen VARCHAR(255)
);

-- Recipe diet health compliance reference (lactose free, low sodium...)
CREATE TABLE IF NOT EXISTS ref_recipe_diet_health (
    health_pk INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    health VARCHAR(255)
);

-- Recipe diet belief compliance reference (vegan, halal...)
CREATE TABLE IF NOT EXISTS ref_recipe_diet_belief (
    belief_pk INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    belief VARCHAR(255)
);

-- Difficulty levels
CREATE TABLE IF NOT EXISTS ref_difficulty (
    difficulty_pk INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    difficulty VARCHAR(20)
);

-- Goals (reference table)
CREATE TABLE IF NOT EXISTS ref_goal (
    goal_pk INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    goal VARCHAR(255),

    -- NEW: number of times a goal must be completed
    required_iterations INT NOT NULL
);

-------------------------------------- Recipes section ----------------------------------
-- Recipe main table
CREATE TABLE IF NOT EXISTS recipes (
    recipe_pk INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(150),
    prepTime INT,   -- In minutes
    cookTime INT,   -- In minutes
    cost INT,
    difficulty INT REFERENCES ref_difficulty(difficulty_pk),
    servings INT,
    owner_user_fk BIGINT REFERENCES users(user_pk) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS images (
    recipe_fk INT REFERENCES recipes(recipe_pk) ON DELETE CASCADE,
    file_path TEXT
);

-- Ingredients
CREATE TABLE IF NOT EXISTS recipe_ingredients (
    ingredient_pk INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recipe_fk INTEGER NOT NULL REFERENCES recipes(recipe_pk) ON DELETE CASCADE,
    amount DECIMAL(10,3),
    unit VARCHAR(50),
    ingredient_desc VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS recipe_descriptions (
    recipe_fk INT REFERENCES recipes(recipe_pk) ON DELETE CASCADE,
    description TEXT
);
------------------------- Weekly Plan section -------------------------

CREATE TABLE IF NOT EXISTS daily_recipe (
    daily_pk INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    breakfast_fk INT REFERENCES recipes(recipe_pk) ON DELETE SET NULL,
    morning_fk INT REFERENCES recipes(recipe_pk) ON DELETE SET NULL,
    lunch_fk INT REFERENCES recipes(recipe_pk) ON DELETE SET NULL,
    afternoon_fk INT REFERENCES recipes(recipe_pk) ON DELETE SET NULL,
    dinner_fk INT REFERENCES recipes(recipe_pk) ON DELETE SET NULL,
    night_fk INT REFERENCES recipes(recipe_pk) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS weekly_plan (
    weekly_pk INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_fk INT REFERENCES users(user_pk) ON DELETE CASCADE,
    start_date DATE,
    end_date DATE,
    mon INT REFERENCES daily_recipe(daily_pk) ON DELETE SET NULL,
    tue INT REFERENCES daily_recipe(daily_pk) ON DELETE SET NULL,
    wed INT REFERENCES daily_recipe(daily_pk) ON DELETE SET NULL,
    thu INT REFERENCES daily_recipe(daily_pk) ON DELETE SET NULL,
    fri INT REFERENCES daily_recipe(daily_pk) ON DELETE SET NULL,
    sat INT REFERENCES daily_recipe(daily_pk) ON DELETE SET NULL,
    sun INT REFERENCES daily_recipe(daily_pk) ON DELETE SET NULL
);
------------------------- JOIN TABLES ------------------------------

CREATE TABLE IF NOT EXISTS join_recipes_allergens (
    join_pk INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recipe_fk INT REFERENCES recipes(recipe_pk) ON DELETE CASCADE,
    allergen_fk INT REFERENCES ref_recipe_allergens(allergen_pk)
);

CREATE TABLE IF NOT EXISTS join_recipes_diet_health (
    join_pk INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recipe_fk INT REFERENCES recipes(recipe_pk) ON DELETE CASCADE,
    health_fk INT REFERENCES ref_recipe_diet_health(health_pk)
);

CREATE TABLE IF NOT EXISTS join_recipes_diet_belief (
    join_pk INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recipe_fk INT REFERENCES recipes(recipe_pk) ON DELETE CASCADE,
    belief_fk INT REFERENCES ref_recipe_diet_belief(belief_pk)
);

CREATE TABLE IF NOT EXISTS join_plan_goals(
    join_pk INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    weekly_fk INT REFERENCES weekly_plan(weekly_pk) ON DELETE CASCADE,
    goal_fk INT REFERENCES ref_goal(goal_pk),
    complete BOOLEAN DEFAULT FALSE,
    progress_count INT DEFAULT 0,
    CONSTRAINT check_progress_range CHECK (progress_count >= 0)
);