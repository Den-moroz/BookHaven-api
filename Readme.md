# Book Store API

Welcome to the Book Store API! This project was inspired by the need for a robust and efficient solution to manage the operations of a book store. Whether you're running a physical bookstore or an online platform, our API provides the tools you need to streamline your book inventory, manage customer data, and handle sales transactions seamlessly.

## Table of Contents

- [Technologies and Tools Used](#technologies-and-tools-used)
- [Controller Functionalities](#controller-functionalities)
- [Installation and Usage](#installation-and-usage)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
- [Postman Collection](#postman-collection)
- [Challenges Faced](#challenges-faced)
- [Conclusion](#conclusion)

## Technologies and Tools Used

This API is built using modern technologies and tools to ensure reliability, security, and performance:

- **Spring Boot**: We've leveraged the power of Spring Boot to create a highly scalable and easy-to-maintain application.
- **Hibernate**: Hibernate, an Object-Relational Mapping (ORM) framework, streamlines database interaction by mapping Java objects to database tables, making it feel like second nature.
- **Spring Security**: Security is a top priority. We've implemented Spring Security to protect your data and ensure only authorized access.
- **JWT (JSON Web Tokens)**: Fortify our application with JWT-based authentication and authorization, as if you're locking it down with a secure shield.
- **Spring Data JPA**: With Spring Data JPA, managing our book data in the database is a breeze.
- **MapStruct**: MapStruct simplifies the transformation between different data models.
- **Liquibase**: Effortlessly manage database schema changes with Liquibase, making it seem like a walk in the park.
- **Swagger**: Explore and interact with our API easily through the Swagger documentation.
- **Postman**: I've provided a collection of Postman requests to help you test and use the API effectively.

## Controller Functionalities

### Our API consists of several controllers, each serving a specific purpose:

- **AuthenticationController**: Handles user authentication and registration.

- **BookController**: Handles book-related operations, including creation, retrieval, update, and deletion of books.

- **CategoryController**: Manages categories, enabling admins to create, update, delete categories, and retrieve books by category.

- **ShoppingCartController**: Manages shopping cart information, allowing users to create, retrieve, update, and delete cart items.

- **OrderController**: Handles order management, including creating orders, updating order status, and retrieving order history.

# Installation and Usage

You can interact with the Book Store API in two ways:

1. **Using Swagger (No Installation Required)**

   To test the API without any installation, you can utilize Swagger, which provides a user-friendly interface for interacting with your API.

    - Open your web browser and navigate to [Swagger UI](http://ec2-13-48-249-170.eu-north-1.compute.amazonaws.com/api/swagger-ui/index.html#/).
    - Explore and test the various API endpoints directly through the Swagger interface.

**Authentication for Swagger UI:**

- **User Access**: You can use the following credentials to access the API as a user:
    - **Username**: test@example.com
    - **Password**: zxcvbnmas

- **Admin Access**: If you want to access admin-specific functionalities, use these credentials:
    - **Username**: admin@example.com
    - **Password**: zxcvbnmas

2. **Local Installation**

   If you prefer to run the Book Store API on your local machine, follow these steps:

   ### Prerequisites

   Before you begin, ensure you have the following prerequisites installed on your system:

    - [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
    - [MySQL database](https://www.mysql.com/downloads/)
    - [Docker](https://docs.docker.com/get-docker/)

   ### Installation

   Follow these steps to set up and run the Book Store API on your local machine:

    1. **Clone the Repository:**

       ```shell
       git clone https://github.com/Den-moroz/BookStore-api.git
       cd BookStore-api
       ```

    2. **Build with Maven:**

       ```shell
       mvn clean install
       ```

    3. **Docker Image Creation:**

       ```shell
       docker build -t your-image-name .
       ```

    4. **Docker Compose:**

       ```shell
       docker-compose build
       docker-compose up
       ```

   Make sure you have Docker and Docker Compose installed and properly configured on your machine before running the last two commands.

   You've now successfully set up and launched the Book Store API locally.

## Postman Collection
For your convenience, i've created a Postman collection that includes sample requests for various API endpoints. You can download it [here](BookStore-api.postman_collection.json) and import it into your Postman workspace to get started quickly.

## Challenges Faced
During the development of this project, i encountered a few challenges, such as data modeling complexities and security implementations. However, through collaboration and extensive testing, i successfully overcame these challenges to deliver a reliable and secure API.

## Conclusion
The Bookstore API provides a solid foundation for building a book-selling platform. Whether you're starting a new online bookstore or enhancing an existing one, this API can help you manage books, orders, and customers efficiently. Feel free to reach out if you have any questions or need assistance.

### Happy coding!
