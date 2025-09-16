package com.example.demo;

import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Advanced Azure PostgreSQL utilities for AAD authentication
 * 
 * This class provides additional utilities for working with Azure PostgreSQL
 * including connection testing, health checks, and advanced operations.
 */
public class AzurePostgreSqlUtils {
    
    private static final Logger log = Logger.getLogger(AzurePostgreSqlUtils.class.getName());
    
    /**
     * Perform a comprehensive health check of the database connection
     */
    public static boolean performHealthCheck(Connection connection) {
        try {
            log.info("Performing comprehensive health check...");
            
            // Check connection validity
            if (!connection.isValid(30)) {
                log.warning("Connection is not valid");
                return false;
            }
            
            // Test basic query execution
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1 as health_check")) {
                
                if (rs.next() && rs.getInt("health_check") == 1) {
                    log.info("✅ Basic query execution: PASSED");
                } else {
                    log.warning("❌ Basic query execution: FAILED");
                    return false;
                }
            }
            
            // Check database permissions
            if (checkDatabasePermissions(connection)) {
                log.info("✅ Database permissions: SUFFICIENT");
            } else {
                log.warning("⚠️ Database permissions: LIMITED");
            }
            
            // Check SSL connection
            if (checkSslConnection(connection)) {
                log.info("✅ SSL connection: ACTIVE");
            } else {
                log.warning("❌ SSL connection: NOT ACTIVE");
            }
            
            // Display connection metadata
            displayConnectionMetadata(connection);
            
            log.info("Health check completed successfully");
            return true;
            
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Health check failed", e);
            return false;
        }
    }
    
    /**
     * Check database permissions for the current user
     */
    private static boolean checkDatabasePermissions(Connection connection) throws SQLException {
        String sql = "SELECT " +
            "has_database_privilege(current_database(), 'CREATE') as can_create, " +
            "has_database_privilege(current_database(), 'CONNECT') as can_connect, " +
            "has_schema_privilege('public', 'CREATE') as can_create_schema, " +
            "has_schema_privilege('public', 'USAGE') as can_use_schema";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                boolean canCreate = rs.getBoolean("can_create");
                boolean canConnect = rs.getBoolean("can_connect");
                boolean canCreateSchema = rs.getBoolean("can_create_schema");
                boolean canUseSchema = rs.getBoolean("can_use_schema");
                
                log.info("Permission Summary:");
                log.info("  - Database CREATE: " + (canCreate ? "✅" : "❌"));
                log.info("  - Database CONNECT: " + (canConnect ? "✅" : "❌"));
                log.info("  - Schema CREATE: " + (canCreateSchema ? "✅" : "❌"));
                log.info("  - Schema USAGE: " + (canUseSchema ? "✅" : "❌"));
                
                return canConnect && canUseSchema; // Minimum required permissions
            }
        }
        return false;
    }
    
    /**
     * Check if SSL connection is active
     */
    private static boolean checkSslConnection(Connection connection) throws SQLException {
        String sql = "SELECT ssl_is_used() as ssl_active";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getBoolean("ssl_active");
            }
        }
        return false;
    }
    
    /**
     * Display detailed connection metadata
     */
    private static void displayConnectionMetadata(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        
        log.info("Connection Metadata:");
        log.info("  - Database Product: " + metaData.getDatabaseProductName());
        log.info("  - Database Version: " + metaData.getDatabaseProductVersion());
        log.info("  - Driver Name: " + metaData.getDriverName());
        log.info("  - Driver Version: " + metaData.getDriverVersion());
        log.info("  - JDBC Version: " + metaData.getJDBCMajorVersion() + "." + metaData.getJDBCMinorVersion());
        log.info("  - Connection URL: " + metaData.getURL());
        log.info("  - Username: " + metaData.getUserName());
        log.info("  - Read Only: " + connection.isReadOnly());
        log.info("  - Auto Commit: " + connection.getAutoCommit());
        log.info("  - Transaction Isolation: " + getIsolationLevelName(connection.getTransactionIsolation()));
    }
    
    /**
     * Get human-readable transaction isolation level name
     */
    private static String getIsolationLevelName(int level) {
        switch (level) {
            case Connection.TRANSACTION_NONE:
                return "NONE";
            case Connection.TRANSACTION_READ_UNCOMMITTED:
                return "READ_UNCOMMITTED";
            case Connection.TRANSACTION_READ_COMMITTED:
                return "READ_COMMITTED";
            case Connection.TRANSACTION_REPEATABLE_READ:
                return "REPEATABLE_READ";
            case Connection.TRANSACTION_SERIALIZABLE:
                return "SERIALIZABLE";
            default:
                return "UNKNOWN (" + level + ")";
        }
    }

    /**
     * Display Azure-specific PostgreSQL configuration
     */
    public static void displayAzureConfiguration(Connection connection) {
        log.info("Azure PostgreSQL Configuration:");
        
        try (Statement stmt = connection.createStatement()) {
            
            // Check Azure-specific settings
            String[] azureSettings = {
                "azure.extensions",
                "shared_preload_libraries", 
                "log_statement",
                "log_min_duration_statement",
                "max_connections",
                "ssl"
            };
            
            for (String setting : azureSettings) {
                try (ResultSet rs = stmt.executeQuery("SHOW " + setting)) {
                    if (rs.next()) {
                        log.info("  - " + setting + ": " + rs.getString(1));
                    }
                } catch (SQLException e) {
                    log.fine("Could not retrieve setting: " + setting);
                }
            }
            
        } catch (SQLException e) {
            log.log(Level.WARNING, "Could not retrieve Azure configuration", e);
        }
    }
    
    /**
     * Create a simple performance test
     */
    public static void performanceTest(Connection connection, int iterations) {
        log.info("Performing simple performance test with " + iterations + " iterations...");
        
        long startTime = System.currentTimeMillis();
        
        try {
            for (int i = 0; i < iterations; i++) {
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT " + i + " as iteration, NOW() as current_time")) {
                    
                    if (rs.next()) {
                        // Just verify we can read the result
                        rs.getInt("iteration");
                        rs.getTimestamp("current_time");
                    }
                }
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            double avgTime = (double) duration / iterations;
            
            log.info("Performance Test Results:");
            log.info("  - Total time: " + duration + " ms");
            log.info("  - Average per query: " + String.format("%.2f", avgTime) + " ms");
            log.info("  - Queries per second: " + String.format("%.2f", 1000.0 / avgTime));
            
        } catch (SQLException e) {
            log.log(Level.WARNING, "Performance test failed", e);
        }
    }
}