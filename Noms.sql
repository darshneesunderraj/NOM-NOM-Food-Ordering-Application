CREATE DATABASE Noms;
USE Noms;
CREATE TABLE FoodCategories (
    CategoryID INT PRIMARY KEY AUTO_INCREMENT,
    CategoryName VARCHAR(500) NOT NULL
);
-- Create a table for food items
CREATE TABLE FoodItems (
    ItemID INT PRIMARY KEY AUTO_INCREMENT,
    ItemName VARCHAR(100) NOT NULL,
    Description TEXT,
    Price DECIMAL(100, 20) NOT NULL,
    CategoryID INT,
    FOREIGN KEY (CategoryID) REFERENCES FoodCategories(CategoryID)
);

-- Insert sample food categories
INSERT INTO FoodCategories (CategoryName) VALUES
('Appetizers'),
('Main Courses'),
('Desserts');

-- Insert sample food items
INSERT INTO foodItems (ItemName, Description, Price, CategoryID) VALUES
('Biryani', 'Indian Special', 20.99, 2),
('Burger', 'Chicken/Beef/Pork/Fish', 15.99, 2),
('Chicken Cheese Balls', 10.99, 2),
('Ice Cream', 'All Flavors', 10.99, 2),
('Naan', 'Bread/Roti', 15.99, 2),
('Butter Chicken', 'Indian Special', 20.99, 2);

