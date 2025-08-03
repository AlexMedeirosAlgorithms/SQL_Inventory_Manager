
CSC6302 Database Principles 
Module 07 Project 07
Alexander Medeiros 04/29/2024

This is a read me file to accompany the Inventory Manager database application built in JAVA using NetBeans. 

The source files are located in 'InventoryManager\src\inventorymanager'. The files contained are the "DAL", "BusinessLogic", and "InventoryManagerApp".

The database creation SQL file is location in the parent directory "InventoryManager".

The connector file is also located in the source file directory, and the parent directory. 


The DAL calls the stored procedures in the database, the BusinessLogic manages the workflow methods, and the InventoryManagerApp manages the user interaction and system printout.
 
This program uses caching to store the commonly queried attributes such as categories, room names, and locations.

When the tables associated with these attributes are updated, the cache is invalidated to allow new chache. This is defined in the DAL.


-------------------- Application Usage Instructions -------------------- 

When the application begins, the user is prompted to enter the username and password to access the database. 

A main menu will display, and the user is prompted with 4 selections: 

1. View Inventory
2. Add Inventory
3. Remove Inventory
4. Exit

These selections 1,2,3 are the main workflows. Each workflow being to view invetory stored in the database, add inventory to the database, or remove inventory from the database. 

The user will enter the number for the selection, and the work flow will execute.


-------------------- Workflow #1 - Viewing the inventory -------------------- 

When main menu option #1 is selected, the program will display the availible items. 

Available Items:
1. Laptop Charger
2. Dining Chair
3. Bleach
4. Toilet Paper

The user will enter the number associated with the item, and another prompt will display to allow the user to view its attributes

After an item is selected, another prompt displays:
1. View Description
2. View Quantity
3. View Location
4. View Alerts

The user will make a selection by entering the associated number, and the workflow will execute. The attributes will display for the user. Then the user will be returned to
the main menu to make another selection.


--------------------  Workflow #2 - Adding inventory -------------------- 

When main menu option #2 is selected, a series of prompts will display. 

A prompt will display and the user will enter the item name.
A prompt will display and the user will enter the item description.
A prompt will display and the user will enter the item quantity.

A new prompt will display showing the availible rooms to store the item. To select a room type its name, or enter 'new' to create a new room, then type the new room name when prompted. 

Another prompt will display showing the availible locations. To select a location type its name, or enter 'new' to create a new location, then type the location name when prompted. 

Another prompt will display showing the availible categories. To select a category type its name, or enter 'new' to create a new category, then type the category name when prompted.

The user will then be returned to the main menu.


--------------------  Workflow #3 - Removing inventory -------------------- 

When main menu option #3 is selected, a prompt will display the avilible items in the inventory.

A user will select an item to remove by typing the associated number. The item will be removed and a prompt will confirm the item has been removed. 

The user is then returned to the main menu.


--------------------  Exiting the application -------------------- 

A select of 4 at the main menu will exit the app. A selection of '0' will return the user to the main menu.
