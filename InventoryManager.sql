/*
 * CSC6302 Module 07 Project 07
 * Clopyright Alexander Medeiros 04/28/2024
 * Inventory Manager database creation script, defines tables, inserts data, and defines procedures and functions
 * Works alongside the Inventory Manager App
 */

-- Drop Database if exists
DROP DATABASE IF EXISTS InventoryManager;

-- Create Database
CREATE DATABASE InventoryManager;

-- Use Database
USE InventoryManager;

-- Table: Categories
CREATE TABLE IF NOT EXISTS Categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL
);

-- Table: Rooms
CREATE TABLE IF NOT EXISTS Rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(255) NOT NULL
);

-- Table: LocationNames
CREATE TABLE IF NOT EXISTS LocationNames (
    location_id INT AUTO_INCREMENT PRIMARY KEY,
    location_name VARCHAR(255) NOT NULL
);

-- Table: Items
CREATE TABLE IF NOT EXISTS Items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(255) NOT NULL,
    description TEXT,
    quantity INT DEFAULT 0,
    category_id INT,
    location_id INT,
    room_id INT,
    FOREIGN KEY (category_id) REFERENCES Categories(category_id),
    FOREIGN KEY (room_id) REFERENCES Rooms(room_id),
    INDEX idx_category_id (category_id),
    INDEX idx_room_id (room_id)
);

-- Table: Locations
CREATE TABLE IF NOT EXISTS Locations (
    location_id INT AUTO_INCREMENT PRIMARY KEY,
    location_name_id INT,
    room_id INT,
    FOREIGN KEY (location_name_id) REFERENCES LocationNames(location_id),
    FOREIGN KEY (room_id) REFERENCES Rooms(room_id)
);

-- Table: Alerts
CREATE TABLE IF NOT EXISTS Alerts (
    alert_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    alert_type ENUM('Low Stock', 'Expiration Date') NOT NULL,
    alert_message TEXT NOT NULL,
    alert_date TIMESTAMP NOT NULL,
    FOREIGN KEY (item_id) REFERENCES Items(item_id),
    INDEX idx_item_id (item_id)
);

ALTER TABLE Alerts
DROP FOREIGN KEY alerts_ibfk_1;

ALTER TABLE Alerts
ADD CONSTRAINT alerts_ibfk_1 FOREIGN KEY (item_id) REFERENCES Items (item_id) ON DELETE CASCADE;

INSERT INTO Categories (category_name) VALUES ('Electronics'), ('Furniture'), ('Cleaning Supplies'), ('Toiletries');

INSERT INTO Rooms (room_name) VALUES 
('Office'), 
('Garage'), 
('Storage Room');


INSERT INTO LocationNames (location_name) VALUES ('Closet 1'), ('Cabinet 2'), ('Shelf C');

INSERT INTO Items (item_name, description, quantity, category_id, room_id) VALUES
('Laptop Charger', 'Charger for all types of laptops', 2, (SELECT category_id FROM Categories WHERE category_name = 'Electronics'), (SELECT room_id FROM Rooms WHERE room_name = 'Office')),
('Dining Chair', 'Wooden dining chair', 2, (SELECT category_id FROM Categories WHERE category_name = 'Furniture'), (SELECT room_id FROM Rooms WHERE room_name = 'Garage')),
('Bleach', '500ml of bleach for cleaning', 2, (SELECT category_id FROM Categories WHERE category_name = 'Cleaning Supplies'), (SELECT room_id FROM Rooms WHERE room_name = 'Storage Room')),
('Toilet Paper', '6 Rolls of Toilet Paper', 1, (SELECT category_id FROM Categories WHERE category_name = 'Toiletries'), (SELECT room_id FROM Rooms WHERE room_name = 'Storage Room'));


INSERT INTO Locations (location_name_id, room_id) VALUES
(1, 1),  -- Closet in Office
(2, 2),  -- Cabinet 2 in Garage
(3, 3);  -- Shelf C in Storage Room


-- Update location_id in Items table for the new entries
SET SQL_SAFE_UPDATES = 0;
UPDATE Items
SET location_id = 1  -- 1 is the correct location_id for Cabinet 1 in Kitchen
WHERE item_name = 'Laptop Charger';

UPDATE Items
SET location_id = 2  -- 2 is for Cabinet 2 in Garage
WHERE item_name = 'Dining Chair';

UPDATE Items
SET location_id = 3  -- 3 is for Shelf C in Storage Room
WHERE item_name = 'Bleach';

UPDATE Items
SET location_id = 3  -- 3 is for Shelf C in Storage Room
WHERE item_name = 'Toilet Paper';
SET SQL_SAFE_UPDATES = 1;

-- Insert an alert for 'Toilet Paper'
INSERT INTO Alerts (item_id, alert_type, alert_message, alert_date) VALUES
(4, 'Low Stock', 'Only 1 packs left', NOW() + INTERVAL 3 DAY);


-- Procedure to get all items
DELIMITER //
CREATE PROCEDURE GetItems()
BEGIN
    SELECT item_id, item_name FROM Items;
END //
DELIMITER ;

-- Procedure to get item description
DELIMITER //
CREATE PROCEDURE GetItemDescription(IN itemID INT)
BEGIN
    SELECT description FROM Items WHERE item_id = itemID;
END //
DELIMITER ;

-- Procedure to get item quantity
DELIMITER //
CREATE PROCEDURE GetItemQuantity(IN itemID INT)
BEGIN
    SELECT quantity FROM Items WHERE item_id = itemID;
END //
DELIMITER ;

-- Procedure to update item quantity
DELIMITER //
CREATE PROCEDURE UpdateItemQuantity(IN itemID INT, IN newQuantity INT)
BEGIN
    UPDATE Items SET quantity = newQuantity WHERE item_id = itemID;
END //
DELIMITER ;

-- Procedure to delete an item
DELIMITER //
CREATE PROCEDURE RemoveItem(
    IN itemName VARCHAR(255)
)
BEGIN
    DELETE FROM Items WHERE item_name = itemName;
END //
DELIMITER ;

-- Procedure to retrieve an item id by item name
DELIMITER //

CREATE PROCEDURE GetItemIdByName(IN itemName VARCHAR(255))
BEGIN
    SELECT ItemID
    FROM Items
    WHERE Item_Name = itemName;
END //

DELIMITER ;


-- Procedure to add a new item
DELIMITER //

CREATE PROCEDURE AddNewItem(
    IN itemName VARCHAR(255),
    IN itemDesc TEXT,
    IN itemQuantity INT,
    IN catID INT,
    IN locID INT,
    IN roomID INT
)
BEGIN
    INSERT INTO Items (item_name, description, quantity, category_id, location_id, room_id)
    VALUES (itemName, itemDesc, itemQuantity, catID, locID, roomID);
END //

DELIMITER ;


-- Procedure to Check for cetegories
DELIMITER //

CREATE PROCEDURE CheckCategoryExists(IN catName VARCHAR(255), OUT catExists BIT)
BEGIN
    DECLARE cnt INT;
    SELECT COUNT(*) INTO cnt FROM Categories WHERE category_name = catName;
    IF cnt > 0 THEN
        SET catExists = 1;  -- Indicates category exists
    ELSE
        SET catExists = 0;  -- Indicates category does not exist
    END IF;
END //

DELIMITER ;

-- Procedure to add item considering categories, location, and room
DELIMITER //

CREATE PROCEDURE AddItemWithName(
    IN itemName VARCHAR(255),
    IN itemDesc TEXT,
    IN itemQuantity INT,
    IN catName VARCHAR(255),
    IN locName VARCHAR(255),
    IN roomName VARCHAR(255)
)
BEGIN
    DECLARE catID INT;
    DECLARE locID INT;
    DECLARE roomID INT;

    -- Get the category ID
    SELECT category_id INTO catID FROM Categories WHERE category_name = catName LIMIT 1;
    -- Get the location ID
    SELECT location_id INTO locID FROM LocationNames WHERE location_name = locName LIMIT 1;
    -- Get the room ID
    SELECT room_id INTO roomID FROM Rooms WHERE room_name = roomName LIMIT 1;

    -- Insert the new item
    INSERT INTO Items (item_name, description, quantity, category_id, location_id, room_id)
    VALUES (itemName, itemDesc, itemQuantity, catID, locID, roomID);
END //

DELIMITER ;

-- Procedure to get the item location
DELIMITER $$

CREATE PROCEDURE GetItemLocation(IN itemName VARCHAR(255))
BEGIN
    SELECT ln.location_name, r.room_name
    FROM Items i
    JOIN Locations l ON i.location_id = l.location_id
    JOIN LocationNames ln ON l.location_name_id = ln.location_id
    JOIN Rooms r ON l.room_id = r.room_id
    WHERE i.item_name = itemName;
END$$

DELIMITER ;

-- Procedure to get an item alert
DELIMITER //

CREATE PROCEDURE GetItemAlerts(IN itemName VARCHAR(255))
BEGIN
    SELECT a.alert_message AS alert_description
    FROM Alerts a
    JOIN Items i ON a.item_id = i.item_id  
    WHERE i.item_name = itemName;
END//

DELIMITER ;

-- Procedure to add a location
DELIMITER //

CREATE PROCEDURE AddLocation(
    IN locationName VARCHAR(255),
    IN roomName VARCHAR(255)
)
BEGIN
    DECLARE locationID INT;
    DECLARE roomID INT;

    -- Check if the location already exists
    SELECT location_id INTO locationID FROM LocationNames WHERE location_name = locationName LIMIT 1;
    IF locationID IS NULL THEN
        -- If the location doesn't exist, insert it into the LocationNames table
        INSERT INTO LocationNames (location_name) VALUES (locationName);
        SET locationID = LAST_INSERT_ID(); -- Get the ID of the newly inserted location
    END IF;

    -- Get the ID of the room
    SELECT room_id INTO roomID FROM Rooms WHERE room_name = roomName LIMIT 1;

    -- Insert the location into the Locations table
    INSERT INTO Locations (location_name_id, room_id) VALUES (locationID, roomID);
END //

DELIMITER ;

-- Procedure to get location by room name
DELIMITER //

CREATE PROCEDURE GetLocationsByRoom(IN selectedRoomName VARCHAR(255))
BEGIN
    SELECT ln.location_name
    FROM Locations l
    JOIN LocationNames ln ON l.location_name_id = ln.location_id
    JOIN Rooms r ON l.room_id = r.room_id
    WHERE r.room_name = selectedRoomName;
END //

DELIMITER ;


-- procedure to get location by room id
DELIMITER //
CREATE PROCEDURE GetLocationsByRoomID(IN roomID INT)
BEGIN
    SELECT ln.location_name
    FROM Locations l
    JOIN LocationNames ln ON l.location_name_id = ln.location_id
    WHERE l.room_id = roomID;
END //
DELIMITER ;


/*
-- Check existing table contents
SELECT * FROM LocationNames;

-- Check existing categories
SELECT * FROM Categories;

-- Check existing rooms
SELECT * FROM Rooms;

-- Check existing items
SELECT * FROM Items;

-- Check existing alerts
SELECT * FROM Alerts;

*/



