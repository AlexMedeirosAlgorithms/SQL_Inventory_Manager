/*
 * CSC6302 Module 07 Project 07
 * Clopyright Alexander Medeiros 04/28/2024
 */

// Application Layer
package inventorymanager;

// Import statements
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;


public class InventoryManagerApp {
    private static final Scanner scanner = new Scanner(System.in); // Global scanner

    public static void main(String[] args) {
        System.out.println("Please enter your credentials.");
        if (!fetchCredentials()) {
            System.out.println("Invalid credentials provided.");
            scanner.close();
            return; // Exit if credentials are not valid
        }
        
        boolean running = true;
        while (running) {
            System.out.println("\nMain Menu:");
            System.out.println("1. View Inventory");
            System.out.println("2. Add Inventory");
            System.out.println("3. Remove Inventory");
            System.out.println("4. Exit");

             System.out.print("Enter your choice: ");
            String input = scanner.nextLine(); // Read the whole line as a string

            try {
                int choice = Integer.parseInt(input); // Try to parse the input as an integer

                switch (choice) {
                    case 1:
                        viewInventory();
                        break;
                    case 2:
                        addInventory();
                        break;
                    case 3:
                        removeInventory();
                        break;
                    case 4:
                        running = false;
                        System.out.println("Exiting the Inventory Manager App.");
                        break;
                    default:
                        System.out.println("Invalid choice! Please select a valid option.");
                        break;
                }
            } catch (NumberFormatException e) {
                // Handle non-integer inputs
                if (input.equalsIgnoreCase("exit")) {
                    running = false;
                    System.out.println("Exiting the Inventory Manager App.");
                } else {
                    System.out.println("Invalid input! Please enter a number or 'exit'.");
                }
            }
        }
        scanner.close(); 
    }

    private static boolean fetchCredentials() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        return BusinessLogic.setCredentials(username, password);
    }
    
    private static void viewInventory() {
        List<String> items = BusinessLogic.getItems();
        if (items.isEmpty()) {
            System.out.println("No items currently in inventory.");
            return;
        }

        System.out.println("Available Items:");
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i));
        }

        System.out.print("Select an item number or 0 to return: ");
        int itemIndex = scanner.nextInt() - 1;
        scanner.nextLine(); // Consume the newline left after nextInt
        if (itemIndex >= 0 && itemIndex < items.size()) {
            String selectedItem = items.get(itemIndex);

            System.out.println("Details for " + selectedItem + ":");
            System.out.println("1. View Description");
            System.out.println("2. View Quantity");
            System.out.println("3. View Location");
            System.out.println("4. View Alerts");

            System.out.print("Enter your choice: ");
            int option = scanner.nextInt();
            scanner.nextLine(); 

            switch (option) {
                case 1: // View Description
                    String description = BusinessLogic.getDescription(selectedItem);
                    System.out.println("Description: " + description);
                    break;
                case 2: // View Quantity
                    int quantity = BusinessLogic.getQuantity(selectedItem);
                    System.out.println("Quantity: " + quantity);
                    break;
                case 3: // View Location
                    String location = BusinessLogic.getLocation(selectedItem);
                    System.out.println("Location: " + location);
                    break;
                case 4: // View Alerts
                    String alerts = BusinessLogic.getAlerts(selectedItem);
                    System.out.println("Alerts: " + alerts);
                    break;
                default:
                    System.out.println("Invalid option. Please choose a valid number.");
                    break;
            }
        }
    }



    private static void addInventory() {
        System.out.print("Enter item name: ");
        String itemName = scanner.nextLine();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); 

        // Ask for room 
        String roomName = handleNameSelection("room", BusinessLogic.getRoomNames(), BusinessLogic::addRoom);
        // Then ask for location
        String locationName = handleNameSelection("location", BusinessLogic.getLocationNames(), (name) -> BusinessLogic.addLocation(name, roomName));
        // Lastly, ask for category
        String categoryName = handleNameSelection("category", BusinessLogic.getCategoryNames(), BusinessLogic::addCategory);

        boolean success = BusinessLogic.addNewItemWithNames(itemName, description, quantity, categoryName, locationName, roomName);
        if (success) {
            System.out.println("Item added successfully!");
        } else {
            System.out.println("Failed to add item.");
        }
    }

    private static void removeInventory() {
        List<String> items = BusinessLogic.getItems(); // Fetches current inventory
        if (items.isEmpty()) {
            System.out.println("No items currently in inventory.");
            return;
        }

        System.out.println("Available Items:");
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i));
        }

        System.out.print("Enter the number of the item you want to remove: ");
        int itemIndex = Integer.parseInt(scanner.nextLine()) - 1;

        if (itemIndex >= 0 && itemIndex < items.size()) {
            String itemNameToRemove = items.get(itemIndex);
            boolean success = BusinessLogic.removeItem(itemNameToRemove);
            if (success) {
                System.out.println("Item removed successfully!");
            } else {
                System.out.println("Failed to remove item. It may not exist or another error occurred.");
            }
        } else {
            System.out.println("Invalid selection. No item removed.");
        }
    }

    
    private static String handleNameSelection(String type, List<String> names, Function<String, Boolean> addFunction) {
        if (names.isEmpty()) {
            System.out.println("No " + type + "s available. Please add a new one.");
        } else {
            names.forEach(name -> System.out.println("- " + name));
        }

        System.out.print("Enter " + type + " name (or type 'new' to add a new one): ");
        String name = scanner.nextLine();
        if ("new".equalsIgnoreCase(name)) {
            System.out.print("Enter new " + type + " name: ");
            name = scanner.nextLine();
            boolean added = addFunction.apply(name);
            if (!added) {
                System.out.println("Failed to add new " + type + ". Please try again.");
                name = handleNameSelection(type, names, addFunction);  // Recursive call to try again
            }
        }
        return name;
    }
}