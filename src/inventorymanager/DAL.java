/*
 * CSC6302 Module 07 Project 07
 * Clopyright Alexander Medeiros 04/28/2024
 */

// DAL Layer
package inventorymanager;

// Import statements
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Scanner;


public class DAL {
    // Define database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/InventoryManager";

    // Method to retrieve username and password from the user
    public static String[] getUsernameAndPassword() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            return new String[]{username, password};
        } catch (Exception e) {
            System.err.println("Error fetching credentials: " + e.getMessage());
            return new String[]{null, null};
        }
    }
    
    
    // Method to validate credentials
    public static boolean validateCredentials(String username, String password) {
        
        try (Connection conn = DriverManager.getConnection(DB_URL, username, password)) {
            return true; 
        } catch (SQLException e) {
            System.err.println("Invalid credentials: " + e.getMessage());
            return false;
        }
    }
    
        
    // Method to manage cache
    public class CacheManager {
        private static Map<String, List<String>> cache = new HashMap<>();

        public static List<String> get(String key) {
            return cache.get(key);
        }

        public static void put(String key, List<String> data) {
            cache.put(key, data);
        }

        public static boolean containsKey(String key) {
            return cache.containsKey(key);
        }

        public static void invalidate(String key) {
            cache.remove(key);
        }

        public static void invalidateAll() {
            cache.clear();
        }
    }

    
    // Method to retrieve a list of items from the database
    public static List<String> getItems(String username, String password) {
        List<String> items = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             CallableStatement stmt = conn.prepareCall("{CALL GetItems()}")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(rs.getString("item_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    
    // Method to retrieve item ID
    public static int getItemId(String itemName, String username, String password) {
        int itemId = -1;
        String sql = "SELECT item_id FROM Items WHERE Item_Name = ?";  
        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    itemId = rs.getInt("item_id");  
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemId;
    }
    
    
    // Method to retrieve item description
    public static String getDescription(String itemName, String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             CallableStatement stmt = conn.prepareCall("{CALL GetItemDescription(?)}")) {
            stmt.setInt(1, getItemId(itemName, username, password)); 
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("description");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Description not found";
    }

    
    // Method to retrieve the quantity of an item
    public static int getQuantity(String itemName, String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             CallableStatement stmt = conn.prepareCall("{CALL GetItemQuantity(?)}")) {
            stmt.setInt(1, getItemId(itemName, username, password));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Default quantity if not found
    }

    
    // Method to retrieve category names utilizing cache
    public static List<String> getCategoryNames(String username, String password) {
        final String cacheKey = "categories";
        if (CacheManager.containsKey(cacheKey)) {
            return CacheManager.get(cacheKey);
        } else {
            List<String> categories = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
                 PreparedStatement stmt = conn.prepareStatement("SELECT category_name FROM Categories")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    categories.add(rs.getString("category_name"));
                }
                CacheManager.put(cacheKey, categories); // Cache the result
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return categories;
        }
    }


    // Method to retrieve room names utilizing cache
    public static List<String> getRoomNames(String username, String password) {
        final String cacheKey = "roomNames";
        if (CacheManager.containsKey(cacheKey)) {
            return CacheManager.get(cacheKey);
        } else {
            List<String> rooms = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
                 PreparedStatement stmt = conn.prepareStatement("SELECT room_name FROM Rooms")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    rooms.add(rs.getString("room_name"));
                }
                CacheManager.put(cacheKey, rooms); // Cache the result
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return rooms;
        }
    }

    
    // Method to retrieve location names utilizing cache
    public static List<String> getLocationNames(String username, String password) {
        final String cacheKey = "locationNames";
        if (CacheManager.containsKey(cacheKey)) {
            return CacheManager.get(cacheKey);
        } else {
            List<String> locations = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
                 PreparedStatement stmt = conn.prepareStatement("SELECT location_name FROM LocationNames")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    locations.add(rs.getString("location_name"));
                }
                CacheManager.put(cacheKey, locations); // Cache the result
            } catch (SQLException e) {
                System.err.println("SQL Exception when fetching location names: " + e.getMessage());
                e.printStackTrace();
            }
            return locations;
        }
    }


    // Method to add a new category
    public static boolean addCategory(String categoryName, String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Categories (category_name) VALUES (?)")) {
            stmt.setString(1, categoryName);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                CacheManager.invalidate("categories"); // Invalidate cache after update
                return true;
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception in addCategory: " + e.getMessage());
        }
        return false;
    }

    
    // Method to add a new item  -- depreciated
    public static boolean addNewItem(String itemName, String description, int quantity, int categoryId, int locationId, int roomId, String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             CallableStatement stmt = conn.prepareCall("{CALL AddNewItem(?, ?, ?, ?, ?, ?)}")) {

            stmt.setString(1, itemName);
            stmt.setString(2, description);
            stmt.setInt(3, quantity);
            stmt.setInt(4, categoryId);
            stmt.setInt(5, locationId);
            stmt.setInt(6, roomId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("SQL Exception in addNewItem: " + e.getMessage());
            return false;
        }
    }

    
    // Method to remove an item from the database
    public static boolean removeItem(String itemName, String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, username, password)) {
            conn.setAutoCommit(false);  

            // First, delete any alerts associated with the item
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Alerts WHERE item_id = (SELECT item_id FROM Items WHERE item_name = ?)")) {
                stmt.setString(1, itemName);
                stmt.executeUpdate();
            }

            // Now, delete the item
            try (PreparedStatement stmt = conn.prepareCall("{CALL RemoveItem(?)}")) {
                stmt.setString(1, itemName);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    conn.commit();  // Commit transaction
                    return true;
                }
            }
            conn.rollback();  // Rollback transaction if deletion fails
        } catch (SQLException e) {
            System.err.println("SQL Exception in removeItem: " + e.getMessage());
        }
        return false;
    }


    // Method to add a new location
    public static boolean addLocation(String locationName, String roomName, String username, String password) {
        int roomId = getRoomIdByName(roomName, username, password);
        if (roomId == -1) {
            System.err.println("Room does not exist, cannot add location: " + roomName);
            return false;
        }
        int locationNameId = addOrFetchLocationName(locationName, username, password);
        if (locationNameId == -1) {
            System.err.println("Failed to add or fetch location name.");
            return false;
        }

        // Check if location already exists in the specific room
        if (getLocationId(locationNameId, roomId, username, password) != -1) {
            System.err.println("Location already exists in the specified room.");
            return false;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Locations (location_name_id, room_id) VALUES (?, ?)")) {
            stmt.setInt(1, locationNameId);
            stmt.setInt(2, roomId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                CacheManager.invalidate("locationNames"); // Invalidate cache
                return true;
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception in addLocation: " + e.getMessage());
        }
        return false;
    }

       
    // Method to retrieve location
    public static String getLocation(String itemName, String username, String password) {
        String locationDetail = "Location not found";
        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             CallableStatement stmt = conn.prepareCall("{CALL GetItemLocation(?)}")) {
            stmt.setString(1, itemName);  
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String locationName = rs.getString("location_name");
                    String roomName = rs.getString("room_name");
                    locationDetail = roomName + " - " + locationName; 
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception when fetching item location: " + e.getMessage());
            e.printStackTrace();
        }
        return locationDetail;
    }
   
    
    // Helper method to check if a location exists within a room
    private static int getLocationId(int locationNameId, int roomId, String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             PreparedStatement stmt = conn.prepareStatement("SELECT location_id FROM Locations WHERE location_name_id = ? AND room_id = ?")) { 
            stmt.setInt(1, locationNameId);
            stmt.setInt(2, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("location_id"); 
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception when checking location existence: " + e.getMessage());
        }
        return -1; // Return -1 if not found
    }

    
    // Method to get location by room name
    public static List<String> getLocationsByRoom(String roomName, String username, String password) {
        List<String> locations = new ArrayList<>();
        int roomId = getRoomIdByName(roomName, username, password); 
        if (roomId == -1) {
            System.err.println("Room not found.");
            return locations;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             CallableStatement stmt = conn.prepareCall("{CALL GetLocationsByRoomID(?)}")) {
            stmt.setInt(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    locations.add(rs.getString("location_name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception when fetching locations by room: " + e.getMessage());
        }
        return locations;
    }


    // Method to add a new room
    public static boolean addRoom(String roomName, String username, String password) {
        int roomId = getRoomIdByName(roomName, username, password);
        if (roomId != -1) {
            System.err.println("Room already exists: " + roomName);
            return false;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Rooms (room_name) VALUES (?)")) {
            stmt.setString(1, roomName);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                CacheManager.invalidate("roomNames"); // Invalidate cache
                return true;
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception in addRoom: " + e.getMessage());
        }
        return false;
    }

    
    // Method to add a new item -- use instead of 'addNewItem'
    public static boolean addNewItemWithNames(String itemName, String description, int quantity, String categoryName, String locationName, String roomName, String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             PreparedStatement stmt = conn.prepareStatement("CALL AddItemWithName(?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, itemName);
            stmt.setString(2, description);
            stmt.setInt(3, quantity);
            stmt.setString(4, categoryName);
            stmt.setString(5, locationName);
            stmt.setString(6, roomName);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("SQL Exception in addNewItemWithNames: " + e.getMessage());
            return false;
        }
    }
 
    
    // Method to retrieve alerts
    public static String getAlerts(String itemName, String username, String password) {
        StringBuilder alerts = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             CallableStatement stmt = conn.prepareCall("{CALL GetItemAlerts(?)}")) {
            stmt.setString(1, itemName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (alerts.length() > 0) alerts.append(", ");
                    alerts.append(rs.getString("alert_description"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alerts.toString().isEmpty() ? "No alerts" : alerts.toString();
    }

    
    // Method to add or retrieve location
    public static int addOrFetchLocationName(String locationName, String username, String password) {
    int locationNameId = getLocationNameId(locationName, username, password);
    if (locationNameId != -1) {
        return locationNameId;
    } else {
        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO LocationNames (location_name) VALUES (?)")) {
            stmt.setString(1, locationName);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                
                try (Statement stmt2 = conn.createStatement();
                     ResultSet generatedKeys = stmt2.executeQuery("SELECT LAST_INSERT_ID()")) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception in addOrFetchLocationName: " + e.getMessage());
        }
        return -1;
    }
}

    
    // Method to retrieve location ID
    private static int getLocationNameId(String locationName, String username, String password) {
        
        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             PreparedStatement stmt = conn.prepareStatement("SELECT location_id FROM LocationNames WHERE location_name = ?")) {
            stmt.setString(1, locationName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("location_id");
            }
            return -1; // Return -1 if not found
        } catch (SQLException e) {
            System.err.println("SQL Exception in getLocationNameId: " + e.getMessage());
            return -1;
        }
    }

    
    // Method to retrieve room ID using name
    private static int getRoomIdByName(String roomName, String username, String password) {
        
        try (Connection conn = DriverManager.getConnection(DB_URL, username, password);
             PreparedStatement stmt = conn.prepareStatement("SELECT room_id FROM Rooms WHERE room_name = ?")) {
            stmt.setString(1, roomName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("room_id");
            }
            return -1; // Return -1 if not found
        } catch (SQLException e) {
            System.err.println("SQL Exception in getRoomIdByName: " + e.getMessage());
            return -1;
        }
    } 
}