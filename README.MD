# Gym App Backend - Spring Boot

Welcome to the Gym App Backend! This Spring Boot application serves as the backend for the Gym Management System. It handles bookings, memberships, classes, and appointments, allowing users to make payments through the Stripe platform.
Connected to Front-end: https://github.com/corgan0746/gym-frontend-angular

## Overview

This backend is designed to work seamlessly with the Gym Management System Angular Frontend. It provides essential functionalities for authentication, registration, purchasing, searching, and performing CRUD operations as an administrator. Additionally, media uploads are supported using an S3 bucket, and data is stored in a MySQL Database using JPA as an ORM.

## Features

- **Firebase Authentication**: Users can register and authenticate using Firebase tokens for secure access.

- **CRUD Operations**: Admins can view and manage Classes, Instructors, Memberships, and Membership Classes through a dedicated interface.

- **Media Uploads**: Photos and media are uploaded to an S3 bucket, enhancing the user experience with dynamic content.

- **Scheduled Tasks**: Automated tasks, such as deleting expired bookings and memberships, ensure system efficiency.

- **Payment Integration**: Create payments and subscriptions seamlessly using the Stripe API.

## Technologies Used

- Spring Boot
- Firebase
- MySQL Database with JPA
- S3 Bucket for Media Storage
- Stripe API

## Getting Started

To run the Gym App Backend locally, follow these steps:

1. Clone the repository:
2.  Configure Firebase:

    -   Set up a Firebase project and obtain the configuration.
    -   Locate the Firebase Json configuration in the `src/main/resources/` directory and rename it accordignly in FirebaseConfig Class.
3.  Configure AWS S3:

    -   Obtain your AWS S3 credentials (Access Key, Secret Key) and update the `src/main/resources/application.properties` file.
4.  Configure MySQL Database:

    -   Set up a MySQL Database and update the `src/main/resources/application.properties` file with the database URL and credentials.
5. Environment variables:
- spring.datasource.url - ( url to the Mysql Database, also include credentials in the properties )

- allowed.origins - ( incoming origin accepted on the server )

- aws.s3.access.key - ( Aws S3 Bucket Access key )

- aws.s3.secret.key - ( Aws S3 Bucket Secret key )

- aws.s3.bucket.name - ( Aws S3 Bucket Name )
6. Open your browser and navigate to `http://localhost:8080/` to ensure the application is running.

## Notes

-   Ensure all necessary environment variables are correctly configured.

## License

This project is dual-licensed under the following licenses:

-   [MIT License](https://opensource.org/licenses/MIT): For general use.
-   [Creative Commons Attribution-NonCommercial License (CC BY-NC)](https://creativecommons.org/licenses/by-nc/4.0/): For non-commercial use. Please review the specific terms and conditions of each license before using or contributing to this project.