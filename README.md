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
2. Note that the api tests have been added to the Postman collection. Normally I would also add junit tests covering the
   java side of the implementation, but with the combination of having to set up my entire development environment,
   while also simultaneously working on the assignment, I have run short on time.
3. The same counts for pagination for the GET endpoint of the orders. I would have made the endpoint use Pageable, but
   since everything works now and I don't want to risk breaking things again, I have left this out.
4. Most JavaDoc comments were generated using IntelliJ’s AI Assistant. This approach saves time while providing a clear
   and consistent level of documentation quality.
5. The Reqres.in api does not have any way of retrieving a single user by e-mail. For that reason, when retrieving a
   user, I had to run through all users using the pagination. This is not ideal, but it is the only way to retrieve a
   user by e-mail. In a real world scenario, it would be nice to have a more robust api to call.