# Blockchain Supply Chain Management - Web Application Guide

## Overview
The project has been successfully converted from a desktop GUI application to a modern web-based application using Java servlets, JSP, and Bootstrap for the frontend.

## Architecture

### Backend Components
- **Java Servlets**: Handle HTTP requests and responses
- **JSP Pages**: Dynamic web pages with server-side rendering
- **Service Layer**: Business logic for user, product, and system operations
- **DAO Layer**: Database access using existing JDBC implementation
- **MySQL Database**: Persistent storage with connection pooling

### Frontend Components
- **Bootstrap 5**: Responsive CSS framework
- **Font Awesome**: Icons and visual elements
- **JavaScript**: Client-side interactions and AJAX calls

## Web Application Structure

```
src/main/webapp/
├── WEB-INF/
│   ├── web.xml                 # Servlet configuration
│   └── jsp/
│       └── dashboard.jsp       # Main dashboard page
├── index.jsp                   # Landing page
└── login.jsp                   # Authentication page

src/main/java/com/supplychain/
├── servlet/
│   ├── AuthServlet.java        # Login/logout handling
│   ├── DashboardServlet.java   # Main dashboard
│   ├── ProductServlet.java     # Product management
│   └── ApiServlet.java         # REST API endpoints
├── filter/
│   └── AuthenticationFilter.java # Session management
└── service/
    ├── UserService.java        # User operations
    └── ProductService.java     # Product operations
```

## Key Features

### 1. User Authentication
- **Login System**: SHA-256 password hashing
- **Session Management**: Secure session handling
- **Role-Based Access**: Manager, Supplier, Retailer roles
- **Demo Accounts**: Pre-configured test users

### 2. Responsive Dashboard
- **Role-Specific Views**: Different content based on user role
- **Statistics Cards**: Product, transaction, and blockchain metrics
- **Quick Actions**: Easy access to common operations
- **Mobile-Friendly**: Bootstrap responsive design

### 3. Product Management
- **Create Products**: Add new products to the supply chain
- **View Products**: List and search products
- **Status Tracking**: Monitor product status and location
- **Role Permissions**: Suppliers can create, retailers can view

### 4. Security Features
- **Authentication Filter**: Protects secured pages
- **Session Validation**: Automatic logout on session expiry
- **SQL Injection Protection**: Prepared statements in DAOs
- **Password Hashing**: SHA-256 encryption

## Getting Started

### 1. Database Setup
The application automatically initializes the database on startup:
```bash
# Database will be created with:
- 3 demo users (admin, supplier1, retailer1)
- 2 sample products (Laptop, Smartphone)
- Empty transaction and blockchain tables
```

### 2. Start the Web Application
```bash
# Option 1: Use the startup script
.\START_WEB_APPLICATION.bat

# Option 2: Manual Maven command
mvn jetty:run
```

### 3. Access the Application
- **URL**: http://localhost:8080/supply-chain
- **Landing Page**: Feature overview and navigation to login
- **Login Page**: Authentication with demo accounts

## Demo Accounts

| Username  | Password    | Role     | Description |
|-----------|-------------|----------|-------------|
| admin     | password123 | MANAGER  | Full system access |
| supplier1 | password    | SUPPLIER | Product creation and management |
| retailer1 | password    | RETAILER | Product viewing and receiving |

## API Endpoints

### Authentication
- `POST /auth?action=login` - User login
- `GET /auth?action=logout` - User logout

### Dashboard
- `GET /dashboard` - Main dashboard view

### Products
- `GET /products` - List products (role-based)
- `GET /products?action=create` - Product creation form
- `POST /products?action=create` - Create new product
- `GET /products?action=view&id={id}` - View specific product

### API (AJAX)
- `POST /api/blockchain/mine` - Mine blockchain block (Manager only)

## Technology Stack

### Backend
- **Java 11**: Core programming language
- **Maven**: Build and dependency management
- **Jetty**: Embedded web server
- **MySQL**: Database system
- **JDBC**: Database connectivity

### Frontend
- **HTML5/CSS3**: Modern web standards
- **Bootstrap 5**: Responsive framework
- **JavaScript**: Client-side scripting
- **JSP/JSTL**: Server-side templating

### Libraries
- **Jackson**: JSON processing
- **MySQL Connector**: Database driver
- **Servlet API**: Web application framework
- **JSTL**: JSP Standard Tag Library

## Development Features

### Hot Reload
The Jetty server supports hot reload for:
- JSP page changes (automatic)
- Static resources (CSS, JS, images)
- Java class changes (requires restart)

### Debugging
- **Server Logs**: Detailed logging with java.util.logging
- **Database Logs**: Connection and query logging
- **Error Pages**: Custom 404 and 500 error handling

### Testing
- **Unit Tests**: Existing JUnit and jqwik tests still work
- **Integration Tests**: Can test servlets with embedded Jetty
- **Manual Testing**: Web interface for end-to-end testing

## Future Enhancements

### Planned Features
1. **Transaction Management**: Web interface for supply chain transactions
2. **Blockchain Visualization**: Interactive blockchain explorer
3. **Real-time Updates**: WebSocket integration for live updates
4. **Advanced Search**: Product and transaction filtering
5. **Reporting**: PDF and Excel export capabilities
6. **Mobile App**: REST API ready for mobile development

### Technical Improvements
1. **Spring Framework**: Migration to Spring Boot
2. **REST API**: Full RESTful service implementation
3. **Database Migration**: Flyway or Liquibase integration
4. **Caching**: Redis integration for performance
5. **Security**: OAuth2 and JWT token authentication

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check MySQL server is running
   - Verify database credentials in `database.properties`
   - Ensure database `supply_chain_db` exists

2. **Port Already in Use**
   - Change port in `pom.xml` jetty configuration
   - Kill existing processes on port 8080

3. **Compilation Errors**
   - Run `mvn clean compile` to rebuild
   - Check Java version compatibility (requires Java 11+)

4. **Session Timeout**
   - Default timeout is 30 minutes
   - Adjust in `web.xml` session-config

### Log Locations
- **Application Logs**: Console output during Maven execution
- **Database Logs**: MySQL server logs
- **Jetty Logs**: Embedded in Maven output

## Conclusion

The blockchain supply chain management system has been successfully transformed into a modern web application while maintaining all core functionality. The new web interface provides:

- **Better Accessibility**: Access from any device with a web browser
- **Improved User Experience**: Modern, responsive design
- **Enhanced Security**: Session-based authentication and authorization
- **Scalability**: Ready for multi-user concurrent access
- **Maintainability**: Clean separation of concerns with MVC pattern

The application is now ready for production deployment and can be easily extended with additional features and integrations.