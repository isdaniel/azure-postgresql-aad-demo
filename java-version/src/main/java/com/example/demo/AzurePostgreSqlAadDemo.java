package com.example.demo;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Azure PostgreSQL AAD Demo Application
 * 
 * This application demonstrates how to connect to Azure PostgreSQL Flexible Server
 * using Azure Active Directory (AAD) authentication with Managed Identity.
 * 
 * Prerequisites:
 * 1. Azure PostgreSQL Flexible Server with AAD authentication enabled
 * 2. User-assigned or System-assigned Managed Identity
 * 3. Managed Identity granted access to the PostgreSQL database
 * 4. Application running in Azure environment (VM, App Service, etc.)
 */
public class AzurePostgreSqlAadDemo {

    private static final Logger log = Logger.getLogger(AzurePostgreSqlAadDemo.class.getName());

    static {
        // Configure logging format
        System.setProperty("java.util.logging.SimpleFormatter.format", 
            "[%1$tF %1$tT] [%4$-7s] %5$s %n");
    }

    public static void main(String[] args) {
        try {
            log.info("=== Azure PostgreSQL AAD Authentication Demo ===");
            
            // Load application properties
            log.info("Loading application properties...");
            Properties properties = loadApplicationProperties();
            
            // Establish database connection using AAD authentication
            log.info("Connecting to Azure PostgreSQL with AAD Managed Identity...");
            try (Connection connection = establishConnection(properties)) {
                
                // Test the connection
                testConnection(connection);
                
                // Perform comprehensive health check
                AzurePostgreSqlUtils.performHealthCheck(connection);
                
                // Display Azure-specific configuration
                AzurePostgreSqlUtils.displayAzureConfiguration(connection);

                log.info("Demo completed successfully!");
                
            } catch (SQLException e) {
                log.log(Level.SEVERE, "Database operation failed: " + e.getMessage(), e);
                log.severe("SQL State: " + e.getSQLState());
                log.severe("Error Code: " + e.getErrorCode());
                if (e.getCause() != null) {
                    log.severe("Cause: " + e.getCause().getClass().getName() + " - " + e.getCause().getMessage());
                }
                throw e;
            }
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Application failed: " + e.getMessage(), e);
            if (e.getCause() != null) {
                log.severe("Root cause: " + e.getCause().getClass().getName() + " - " + e.getCause().getMessage());
            }
            System.exit(1);
        }
    }

    /**
     * Load application properties from the resources folder
     */
    private static Properties loadApplicationProperties() throws Exception {
        Properties properties = new Properties();
        try (var inputStream = AzurePostgreSqlAadDemo.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            
            if (inputStream == null) {
                throw new RuntimeException("Could not find application.properties file");
            }
            
            properties.load(inputStream);
            
            log.info("Database URL: " + properties.getProperty("url"));
            log.info("User (Client ID): " + properties.getProperty("user"));
            
            return properties;
        }
    }

    /**
     * Establish connection to Azure PostgreSQL using AAD authentication
     */
    private static Connection establishConnection(Properties properties) throws SQLException {
        String url = properties.getProperty("url");
        
        if (url == null || url.trim().isEmpty()) {
            throw new SQLException("Database URL is not configured");
        }
        
        log.info("Attempting to connect with URL: " + url);
        log.info("User (Client ID): " + properties.getProperty("user"));
        
        try {
            // The Azure PostgreSQL Authentication Plugin will automatically:
            // 1. Detect the Managed Identity environment
            // 2. Acquire an access token from Azure AD
            // 3. Use the token for authentication
            log.info("Calling DriverManager.getConnection...");
            Connection connection = DriverManager.getConnection(url, properties);
            
            // Configure connection properties
            connection.setAutoCommit(true);
            
            log.info("Successfully connected to Azure PostgreSQL using AAD authentication");
            return connection;
        } catch (SQLException e) {
            log.severe("SQLException during connection attempt:");
            log.severe("  Message: " + e.getMessage());
            log.severe("  SQL State: " + e.getSQLState());
            log.severe("  Error Code: " + e.getErrorCode());
            if (e.getCause() != null) {
                log.severe("  Cause: " + e.getCause().getClass().getName() + " - " + e.getCause().getMessage());
            }
            throw e;
        }
    }

    /**
     * Test the database connection
     */
    private static void testConnection(Connection connection) throws SQLException {
        log.info("Testing database connection...");
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT version(), current_database(), current_user")) {
            
            if (resultSet.next()) {
                String version = resultSet.getString(1);
                String database = resultSet.getString(2);
                String currentUser = resultSet.getString(3);
                
                log.info("PostgreSQL Version: " + version);
                log.info("Current Database: " + database);
                log.info("Current User: " + currentUser);
                log.info("Connection test successful!");
            }
        }
    }

    /**
     * Create the database schema
     */
    private static void createSchema(Connection connection) throws SQLException {
        try (Scanner scanner = new Scanner(
                AzurePostgreSqlAadDemo.class
                    .getClassLoader()
                    .getResourceAsStream("schema.sql"));
             Statement statement = connection.createStatement()) {
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith("--")) {
                    statement.execute(line);
                }
            }
            
            log.info("Database schema created successfully");
        }
    }

    /**
     * Demonstrate basic CRUD operations
     */
    private static void demonstrateCrudOperations(Connection connection) throws SQLException {
        log.info("Demonstrating CRUD operations...");
        
        // Create a sample todo item
        Todo todo = new Todo(1L, "azure-demo", 
            "Congratulations! You have successfully connected to Azure PostgreSQL with AAD authentication!", 
            false);
        
        // Insert data
        insertData(todo, connection);
        
        // Read data
        Todo readTodo = readData(connection);
        if (readTodo != null) {
            log.info("Read data: " + readTodo);
            
            // Update data
            readTodo.setDetails("Updated: AAD authentication is working perfectly!");
            readTodo.setDone(true);
            updateData(readTodo, connection);
            
            // Read updated data
            Todo updatedTodo = readData(connection);
            if (updatedTodo != null) {
                log.info("Updated data: " + updatedTodo);
            }
            
            // Delete data
            deleteData(readTodo, connection);
            
            // Verify deletion
            Todo deletedTodo = readData(connection);
            if (deletedTodo == null) {
                log.info("Data successfully deleted");
            }
        }
    }

    /**
     * Insert a todo item into the database
     */
    private static void insertData(Todo todo, Connection connection) throws SQLException {
        log.info("Inserting data...");
        String sql = "INSERT INTO todo (id, description, details, done) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, todo.getId());
            statement.setString(2, todo.getDescription());
            statement.setString(3, todo.getDetails());
            statement.setBoolean(4, todo.isDone());
            
            int rowsAffected = statement.executeUpdate();
            log.info("Inserted " + rowsAffected + " row(s)");
        }
    }

    /**
     * Read a todo item from the database
     */
    private static Todo readData(Connection connection) throws SQLException {
        log.info("Reading data...");
        String sql = "SELECT * FROM todo LIMIT 1";
        
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                Todo todo = new Todo();
                todo.setId(resultSet.getLong("id"));
                todo.setDescription(resultSet.getString("description"));
                todo.setDetails(resultSet.getString("details"));
                todo.setDone(resultSet.getBoolean("done"));
                return todo;
            } else {
                log.info("No data found in the database");
                return null;
            }
        }
    }

    /**
     * Update a todo item in the database
     */
    private static void updateData(Todo todo, Connection connection) throws SQLException {
        log.info("Updating data...");
        String sql = "UPDATE todo SET description = ?, details = ?, done = ? WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, todo.getDescription());
            statement.setString(2, todo.getDetails());
            statement.setBoolean(3, todo.isDone());
            statement.setLong(4, todo.getId());
            
            int rowsAffected = statement.executeUpdate();
            log.info("Updated " + rowsAffected + " row(s)");
        }
    }

    /**
     * Delete a todo item from the database
     */
    private static void deleteData(Todo todo, Connection connection) throws SQLException {
        log.info("Deleting data...");
        String sql = "DELETE FROM todo WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, todo.getId());
            
            int rowsAffected = statement.executeUpdate();
            log.info("Deleted " + rowsAffected + " row(s)");
        }
    }
}