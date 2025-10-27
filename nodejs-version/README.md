# Azure PostgreSQL AAD Authentication Demo (Node.js)

A Node.js Express application demonstrating Azure Active Directory (AAD) authentication for Azure Database for PostgreSQL using the DefaultAzureCredential from the Azure Identity library.

## Overview

This application showcases how to authenticate to Azure Database for PostgreSQL using Azure Active Directory credentials instead of traditional username/password authentication. It uses the `@azure/identity` library to obtain access tokens and demonstrates token management including refresh handling.

## Prerequisites

- Node.js (version 20 or higher)
- Azure subscription
- Azure Database for PostgreSQL server with AAD authentication enabled
- Proper Azure credentials configured (see Authentication Methods below)

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd azure-postgresql-aad-demo/nodejs-version
```

2. Install dependencies:
```bash
npm install
```

3. Set up environment variables:
```bash
# Required environment variables
DB_SERVER=your-postgresql-server.postgres.database.azure.com
DB_NAME=your-database-name
```

## Authentication Methods

The application uses `DefaultAzureCredential`, which attempts authentication in the following order:

1. **Environment Variables** - Service principal credentials via environment variables
2. **Managed Identity** - When running on Azure services (App Service, VM, etc.)
3. **Azure CLI** - When authenticated via `az login`
4. **Azure PowerShell** - When authenticated via Azure PowerShell
5. **Interactive Browser** - As a fallback for development

### Setting up Service Principal (Environment Variables)

```bash
AZURE_CLIENT_ID=your-client-id
AZURE_CLIENT_SECRET=your-client-secret
AZURE_TENANT_ID=your-tenant-id
```

### Setting up Azure CLI Authentication

```bash
az login
```

## Configuration

### Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `DB_SERVER` | PostgreSQL server hostname | Yes |
| `DB_NAME` | Database name | Yes |
| `AZURE_CLIENT_ID` | Service principal client ID | No* |
| `AZURE_CLIENT_SECRET` | Service principal secret | No* |
| `AZURE_TENANT_ID` | Azure tenant ID | No* |

*Required only when using service principal authentication

### Azure Database for PostgreSQL Setup

1. Enable AAD authentication on your PostgreSQL server
2. Add your Azure identity as a PostgreSQL user:
```sql
-- Connect as an AAD admin and run:
SELECT * FROM pgaadauth_create_principal('your-user-or-service-principal-name', false, false);
```

## Usage

1. Start the application:
```bash
node app.js
```

2. The server will start on port 8000

3. Access the token endpoint:
```
GET http://localhost:8000/
```

This endpoint will display:
- Current access token
- Token expiration timestamp
- Current time for comparison

## API Endpoints

### GET /

Returns the current Azure access token and expiration information.

**Response:**
- Access token (truncated for security)
- Token expiration timestamp in ISO format
- Current timestamp for comparison

## Code Structure

### Key Components

- **DefaultAzureCredential**: Handles Azure authentication automatically
- **Token Management**: Automatic token acquisition and refresh logic
- **Express Server**: Simple web server to demonstrate token usage
- **Azure Logger**: Configured for comprehensive logging

### Token Scope

The application requests tokens for the scope:
```
https://ossrdbms-aad.database.windows.net/.default
```

This is the specific scope required for Azure Database for PostgreSQL authentication.

## Security Considerations

- Tokens are displayed in the web response for demonstration purposes only
- In production, never expose access tokens in responses
- Implement proper error handling and token storage
- Use HTTPS in production environments
- Rotate service principal credentials regularly

## Troubleshooting

### Common Issues

1. **Authentication Failed**
   - Verify Azure credentials are properly configured
   - Check that the identity has necessary permissions
   - Ensure the PostgreSQL server has AAD authentication enabled

2. **Database Connection Issues**
   - Verify environment variables are set correctly
   - Check PostgreSQL server firewall rules
   - Ensure the AAD user exists in the PostgreSQL database

3. **Token Expiration**
   - The application automatically handles token refresh
   - Check logs for authentication errors
   - Verify system clock is synchronized

### Debugging

Enable verbose logging by setting the log level:
```javascript
setLogLevel("verbose");
```

## Dependencies

- `@azure/identity`: Azure authentication library
- `express`: Web framework for Node.js
- `@azure/logger`: Azure SDK logging utilities

## Production Considerations

1. **Error Handling**: Implement comprehensive error handling for production use
2. **Security**: Remove token display endpoint and implement proper security measures
3. **Monitoring**: Add application insights or other monitoring solutions
4. **Connection Pooling**: Implement proper database connection pooling
5. **Environment Configuration**: Use proper configuration management for different environments

## Resources

- [Azure Identity Library Documentation](https://docs.microsoft.com/en-us/javascript/api/@azure/identity/)
- [DefaultAzureCredential Documentation](https://docs.microsoft.com/en-us/javascript/api/@azure/identity/defaultazurecredential/)
