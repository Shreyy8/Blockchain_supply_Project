# Blockchain-based Supply Chain Management System

A Java application demonstrating OOP principles through blockchain technology for supply chain transparency.

## Project Structure

```
blockchain-supply-chain/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── supplychain/
│   │   │           ├── blockchain/     # Blockchain implementation components
│   │   │           ├── dao/            # Data Access Objects (JDBC)
│   │   │           ├── exception/      # Custom exception hierarchy
│   │   │           ├── gui/            # Java Swing UI components
│   │   │           ├── model/          # Data models and entities
│   │   │           ├── service/        # Business logic services
│   │   │           └── util/           # Utility classes
│   │   └── resources/
│   │       └── database.properties     # Database configuration
│   └── test/
│       └── java/
│           └── com/
│               └── supplychain/        # Unit and property-based tests
├── pom.xml                             # Maven configuration
└── README.md
```

## Dependencies

- **JUnit 5** (5.10.1): Unit testing framework
- **jqwik** (1.8.2): Property-based testing library
- **MySQL Connector/J** (8.2.0): JDBC driver for MySQL
- **Java Swing**: GUI framework (included in JDK)

## Building the Project

```bash
mvn clean compile
```

## Running the Application

### Option 1: Using Maven
```bash
mvn clean package
java -jar target/blockchain-supply-chain-1.0-SNAPSHOT.jar
```

### Option 2: Using Maven exec plugin
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.supplychain.Main"
```

### Option 3: Direct execution
```bash
mvn clean compile
java -cp target/classes;target/dependency/* com.supplychain.Main
```

## Running Tests

```bash
mvn test
```

## Requirements

- Java 11 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

## OOP Principles Demonstrated

- **Inheritance**: User class hierarchy
- **Polymorphism**: Transaction interface implementations
- **Encapsulation**: Private fields with getters/setters
- **Abstraction**: Abstract classes and interfaces
- **Exception Handling**: Custom exception hierarchy
- **Collections & Generics**: Type-safe data structures
- **Multithreading**: Background blockchain mining
- **JDBC**: Database persistence

## Configuration

Edit `src/main/resources/database.properties` to configure database connection settings.

## Application Startup

When the application starts, it performs the following initialization steps:

1. **Database Connection**: Establishes a JDBC connection pool to the configured MySQL database
2. **Schema Initialization**: Creates database tables if they don't exist (users, products, transactions, blocks)
3. **Genesis Block Creation**: Initializes the blockchain with a genesis block (the first block in the chain)
4. **Login GUI Launch**: Displays the login screen for user authentication

### Default Users

The system comes with three pre-configured users for testing:

- **Manager**: username: `admin`, password: `admin123`
- **Supplier**: username: `supplier1`, password: `pass123`
- **Retailer**: username: `retailer1`, password: `pass123`

Each user type has access to different dashboards and functionality based on their role.
