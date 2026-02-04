# Orders API - VodafoneZiggo

This application provides an API for managing orders, including creating, retrieving, updating, and deleting orders. It
is designed to use a contract first approach, where the API contract is defined before the implementation. This ensures
that the API is well-defined and easy to understand, making it easier to develop and maintain.

## Prerequisites

- Java 21 or later
- Maven
- Docker

## Tech Stack

- Java 21
- Spring Boot 4.0.1
- PostgreSQL Database
- springdoc-openapi (swagger ui)
- lombok
- Apache Camel
- IntelliJ IDEA with AI Assistant

## Usage

To run the application locally, follow these steps:

1. Clone the repository
2. Build the project using Maven
3. Run the application using Docker
4. Access the API using Swagger UI or import the Postman-OrdersAPI.json file in postman and run the tests.

### 1. Clone the repository

```bash
  git clone git@github.com:JasperVanAlderwegen-Business/VodafoneZiggo.git
```

### 2. Build the project using Maven

```bash
  mvn clean install
```

### 3. Run the application using Docker

```bash
  docker-compose up
```

### 4. Access the API using  or import the Postman-OrdersAPI.json file in postman and run the tests.

Swagger UI: http://localhost:8080/swagger-ui/index.html

Postman: Open the Postman application and import [Postman-OrdersAPI.json](Postman-OrdersAPI.json)

# Notes & Known Issues

1. While it was not directly specified, the api also provides a way to delete an order as well as update the user for a
   specific order. This was done to provide a more complete implementation of the api.
2. Since I only started at iO on Monday, I did not have a set up laptop yet. Because of this, I had to combine the
   setup of my new MacBook with the implementation of this assignment. While the implementation is feature complete, I
   have not had time implement a ful set of units test for all functionality. This is not an oversight. In a real world
   scenario, everything would be covered by unit tests and integration tests.
3. Most JavaDoc comments were generated using IntelliJ’s AI Assistant. This approach saves time while providing a clear
   and consistent level of documentation quality.
4. The Reqres.in api does not have any way of retrieving a single user by e-mail. For that reason, when retrieving a
   user, I had to run through all users using the pagination. This is not ideal, but it is the only way to retrieve a
   user by e-mail. In a real world scenario, it would be nice to have a more robust api to call.