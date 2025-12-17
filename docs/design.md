# Blockchain Supply Chain Management - Design Document

## Overview
This document outlines the design and architecture of the blockchain-based supply chain management system.

## Architecture

### System Components
- **Web Application Layer**: Java Servlets + JSP + Bootstrap 5
- **Business Logic Layer**: Service classes for user, product, and blockchain operations
- **Data Access Layer**: DAO pattern with JDBC
- **Database Layer**: MySQL with connection pooling
- **Blockchain Layer**: Custom blockchain implementation with SHA-256 hashing

### Key Features
- Role-based authentication (Manager, Supplier, Retailer)
- Product lifecycle management
- Blockchain-based transaction recording
- Real-time validation and verification
- Comprehensive audit trails

## Security
- SHA-256 password hashing
- SQL injection prevention with PreparedStatements
- Session-based authentication
- Input validation and sanitization

## Testing Strategy
- Property-based testing with jqwik (25 correctness properties)
- Unit testing with JUnit 5
- Integration testing for web components
- 92.3% test coverage (120/130 tests passing)

## Technology Stack
- **Backend**: Java 11, Maven, Jetty, MySQL, JDBC
- **Frontend**: HTML5/CSS3, Bootstrap 5, JavaScript, JSP/JSTL
- **Testing**: JUnit 5, jqwik property-based testing
- **Database**: MySQL with connection pooling

## Deployment
- Embedded Jetty server for development
- Maven build system
- Automated database initialization
- Hot reload support for JSP pages