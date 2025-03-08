Delivery App

Description
This project is a minimal viable product (MVP) for a delivery management system.

Setup Instructions
Follow the steps below to set up the project on your local machine:

1. Clone the Repository
   Clone the repository using the following command:
   git clone https://gitlab.com/fr_kata_sf/c4-SF-0196-SQ06.git

2. Build the Project
   Navigate to the project directory and build the project using Maven:
   mvn clean install

3. Set Up the MySQL Database
   The MVP uses a MySQL database. You need to create a database manually called deliverydb:
   CREATE DATABASE deliverydb;

4. Data Initialization
   The project includes a data loader class that automatically populates the database with necessary test data when the application starts.

5. Run the Application
   Launch the project by running the main class DeliveryAppApplication. This will start the application on localhost:8080.

6. Authentication Token
   The application uses Spring Security for authentication. To obtain a token for further requests, send a POST request to the following endpoint:
   POST http://localhost:8080/api/auth/token
   Include the following JSON body:
   {
   "email": "salahedd.lahmam@gmail.com",
   "password": "sla21"
   }
   The response will contain a Bearer token that you can use in subsequent API requests.

7. Swagger Documentation
   The application includes Swagger documentation for easy API exploration. Access it at:
   http://localhost:8080/swagger-ui/index.html

Notes
- Ensure that your MySQL server is running and accessible.
- The application assumes the database schema is created manually before starting the project.
