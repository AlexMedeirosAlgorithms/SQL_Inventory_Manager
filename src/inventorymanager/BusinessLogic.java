/*
 * CSC6302 Module 07 Project 07
 * Clopyright Alexander Medeiros 04/28/2024
 */

// Business Logic Layer
package inventorymanager;

// Import statements
import java.util.List;

public class BusinessLogic {
    private static String username;
    private static String password;

    public static boolean setCredentials(String user, String pass) {
        username = user;
        password = pass;
        return DAL.validateCredentials(username, password);
    }

    public static List<String> getItems() {
        return DAL.getItems(username, password);
    }

    public static String getDescription(String itemName) {
        return DAL.getDescription(itemName, username, password);
    }

    public static int getQuantity(String itemName) {
        return DAL.getQuantity(itemName, username, password);
    }
    
    public static boolean removeItem(String itemName) {
        return DAL.removeItem(itemName, username, password);
    }

    public static boolean addNewItem(String itemName, String description, int quantity, int categoryId, int locationId, int roomId) {
        return DAL.addNewItem(itemName, description, quantity, categoryId, locationId, roomId, username, password);
    }
    
    public static List<String> getCategoryNames() {
        return DAL.getCategoryNames(username, password);
    }

    public static boolean addCategory(String categoryName) {
        return DAL.addCategory(categoryName, username, password);
    }
    
    public static List<String> getLocationNames() {
        return DAL.getLocationNames(username, password);
    }

    public static List<String> getRoomNames() {
        return DAL.getRoomNames(username, password);
    }
   
    public static boolean addRoom(String roomName) {
        return DAL.addRoom(roomName, username, password);
    }
     public static boolean addNewItemWithNames(String itemName, String description, int quantity, String categoryName, String locationName, String roomName) {
        return DAL.addNewItemWithNames(itemName, description, quantity, categoryName, locationName, roomName, username, password);
    }
     
    public static String getLocation(String itemName) {
    return DAL.getLocation(itemName, username, password);
    }
    
    public static boolean addLocation(String locationName, String roomName) {
        return DAL.addLocation(locationName, roomName, username, password);
    }
    
    public static List<String> getLocationsByRoom(String roomName) {
    return DAL.getLocationsByRoom(roomName, username, password); 
}


    public static String getAlerts(String itemName) {
        return DAL.getAlerts(itemName, username, password);
    }
    
}
