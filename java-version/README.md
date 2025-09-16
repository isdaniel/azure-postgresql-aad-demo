# Azure PostgreSQL AAD Authentication Demo

This Java application demonstrates how to connect to Azure PostgreSQL Flexible Server using Azure Active Directory (AAD) authentication with Managed Identity.

## Overview

The application showcases:
- **Passwordless authentication** using Azure Managed Identity
- **Secure connection** to Azure PostgreSQL Flexible Server
- **CRUD operations** with proper error handling
- **Best practices** for Azure PostgreSQL integration

## Prerequisites

1. **Azure PostgreSQL Flexible Server** with AAD authentication enabled
2. **Managed Identity** (User-assigned or System-assigned) configured in your Azure environment
3. **Database permissions** granted to the Managed Identity
4. **Java 11 or higher** installed
5. **Maven 3.6+** for building the project
6. **Azure environment** (VM, App Service, AKS, etc.) where the application will run

## Configuration

The application is pre-configured with your Azure PostgreSQL details:

- **Azure PostgreSQL Server FQDN**: 
- **Client ID (Managed Identity) name**:

### Key Configuration Files

1. **pom.xml**: Maven dependencies including PostgreSQL driver and Azure Identity extensions
2. **application.properties**: Database connection configuration with AAD authentication
3. **schema.sql**: Database schema initialization script

## How It Works

### Authentication Flow

1. The application uses the **Azure PostgreSQL Authentication Plugin**
2. The plugin automatically detects the Managed Identity environment
3. It acquires an **access token** from Azure AD using the Managed Identity
4. The token is used for **passwordless authentication** to PostgreSQL

### Code Structure

```
src/main/java/com/example/demo/
├── AzurePostgreSqlAadDemo.java   # Main application class
└── Todo.java                     # Domain model class

src/main/resources/
├── application.properties         # Database configuration
└── schema.sql                    # Database schema
```

## Building and Running

### 1. Build the Project

```bash
cd azure-postgresql-aad-demo
mvn clean compile
```

### 2. Run the Application

```bash
mvn exec:java -Dexec.mainClass="com.example.demo.AzurePostgreSqlAadDemo"
```

### 3. Alternative: Package and Run

```bash
mvn clean package
java -cp target/classes:target/dependency/* com.example.demo.AzurePostgreSqlAadDemo
```

## Expected Output

When successful, you should see output similar to:

```
[2025-09-16 07:31:32] [INFO   ] === Azure PostgreSQL AAD Authentication Demo === 
[2025-09-16 07:31:32] [INFO   ] Loading application properties... 
[2025-09-16 07:31:32] [INFO   ] Database URL: jdbc:postgresql://pg-lab-dd-14.postgres.database.azure.com:5432/postgres?sslmode=require&authenticationPluginClassName=com.azure.identity.extensions.jdbc.postgresql.AzurePostgresqlAuthenticationPlugin 
[2025-09-16 07:31:32] [INFO   ] User (Client ID): jpwest-im 
[2025-09-16 07:31:32] [INFO   ] Connecting to Azure PostgreSQL with AAD Managed Identity... 
[2025-09-16 07:31:32] [INFO   ] Attempting to connect with URL: jdbc:postgresql://pg-lab-dd-14.postgres.database.azure.com:5432/postgres?sslmode=require&authenticationPluginClassName=com.azure.identity.extensions.jdbc.postgresql.AzurePostgresqlAuthenticationPlugin 
[2025-09-16 07:31:32] [INFO   ] User (Client ID): jpwest-im 
[2025-09-16 07:31:32] [INFO   ] Calling DriverManager.getConnection... 
[2025-09-16 07:31:34] [INFO   ] Successfully connected to Azure PostgreSQL using AAD authentication 
[2025-09-16 07:31:34] [INFO   ] Testing database connection... 
[2025-09-16 07:31:34] [INFO   ] PostgreSQL Version: PostgreSQL 14.17 on x86_64-pc-linux-gnu, compiled by gcc (Ubuntu 7.5.0-3ubuntu1~18.04) 7.5.0, 64-bit 
[2025-09-16 07:31:34] [INFO   ] Current Database: postgres 
[2025-09-16 07:31:34] [INFO   ] Current User: jpwest-im 
[2025-09-16 07:31:34] [INFO   ] Connection test successful! 
[2025-09-16 07:31:34] [INFO   ] Performing comprehensive health check... 
[2025-09-16 07:31:34] [INFO   ] ✅ Basic query execution: PASSED 
[2025-09-16 07:31:34] [INFO   ] Permission Summary: 
[2025-09-16 07:31:34] [INFO   ]   - Database CREATE: ✅ 
[2025-09-16 07:31:34] [INFO   ]   - Database CONNECT: ✅ 
[2025-09-16 07:31:34] [INFO   ]   - Schema CREATE: ✅ 
[2025-09-16 07:31:34] [INFO   ]   - Schema USAGE: ✅ 
[2025-09-16 07:31:34] [INFO   ] ✅ Database permissions: SUFFICIENT 
[2025-09-16 07:31:34] [INFO   ] Connection Metadata: 
[2025-09-16 07:31:34] [INFO   ]   - Database Product: PostgreSQL 
[2025-09-16 07:31:34] [INFO   ]   - Database Version: 14.17 
[2025-09-16 07:31:34] [INFO   ]   - Driver Name: PostgreSQL JDBC Driver 
[2025-09-16 07:31:34] [INFO   ]   - Driver Version: 42.7.5 
[2025-09-16 07:31:34] [INFO   ]   - JDBC Version: 4.2 
[2025-09-16 07:31:34] [INFO   ]   - Connection URL: jdbc:postgresql://pg-lab-dd-14.postgres.database.azure.com:5432/postgres?sslmode=require&authenticationPluginClassName=com.azure.identity.extensions.jdbc.postgresql.AzurePostgresqlAuthenticationPlugin 
[2025-09-16 07:31:34] [INFO   ]   - Username: jpwest-im 
[2025-09-16 07:31:34] [INFO   ]   - Read Only: false 
[2025-09-16 07:31:34] [INFO   ]   - Auto Commit: true 
[2025-09-16 07:31:34] [INFO   ]   - Transaction Isolation: READ_COMMITTED 
[2025-09-16 07:31:34] [INFO   ] Health check completed successfully 
[2025-09-16 07:31:34] [INFO   ] Azure PostgreSQL Configuration: 
[2025-09-16 07:31:34] [INFO   ]   - azure.extensions: PGAUDIT,PG_BUFFERCACHE,PG_STAT_STATEMENTS,UUID-OSSP,VECTOR,PG_VISIBILITY 
[2025-09-16 07:31:34] [INFO   ]   - shared_preload_libraries: pg_cron,pg_stat_statements,pgaudit,azure,pg_qs,pgaadauth,pgms_stats,pgms_wait_sampling,pg_availability 
[2025-09-16 07:31:34] [INFO   ]   - log_statement: mod 
[2025-09-16 07:31:34] [INFO   ]   - log_min_duration_statement: -1 
[2025-09-16 07:31:34] [INFO   ]   - max_connections: 4985 
[2025-09-16 07:31:34] [INFO   ]   - ssl: on 
[2025-09-16 07:31:34] [INFO   ] Demo completed successfully! 

=== Demo execution completed ===
```

## Key Features

### Secure Authentication
- **No hardcoded passwords** in configuration
- **Automatic token refresh** handled by Azure Identity
- **Encrypted connections** using SSL/TLS

### Error Handling
- **Comprehensive exception handling** for database operations
- **Connection timeout** and retry logic
- **Detailed logging** for troubleshooting

### CRUD Operations
- **Create**: Insert new records with prepared statements
- **Read**: Query data with proper result set handling
- **Update**: Modify existing records safely
- **Delete**: Remove records with confirmation

## Troubleshooting

### Common Issues

1. **Authentication Failures**
   - Verify Managed Identity is properly configured
   - Check that the identity has database permissions
   - Ensure the application is running in Azure environment

2. **Connection Timeouts**
   - Verify network connectivity to PostgreSQL server
   - Check firewall rules and network security groups
   - Confirm SSL configuration

3. **Permission Errors**
   - Ensure the Managed Identity exists in PostgreSQL as a user
   - Grant necessary database permissions to the identity
   - Verify role assignments in Azure

### Debug Mode

To enable verbose logging, add this JVM argument:
```bash
java -Djava.util.logging.level=FINE -cp target/classes:target/dependency/* com.example.demo.AzurePostgreSqlAadDemo
```

## Security Best Practices

1. **Use SSL connections** (already configured)
2. **Principle of least privilege** for database permissions
3. **Regular token rotation** (handled automatically)
4. **Network isolation** using private endpoints when possible
5. **Audit logging** enabled on PostgreSQL server

## Dependencies

- **PostgreSQL JDBC Driver** (42.7.5): Database connectivity
- **Azure Identity Extensions** (1.2.0): AAD authentication plugin
- **Azure Identity** (1.15.1): Managed Identity support
- **SLF4J Simple** (2.0.16): Logging framework

## References

- [Azure PostgreSQL Flexible Server Documentation](https://learn.microsoft.com/en-us/azure/postgresql/flexible-server/)
- [Azure Identity for Java](https://learn.microsoft.com/en-us/java/api/overview/azure/identity-readme)
- [Managed Identity Documentation](https://learn.microsoft.com/en-us/azure/active-directory/managed-identities-azure-resources/)
- [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/documentation/)

## License

This demo application is provided as-is for educational and demonstration purposes.