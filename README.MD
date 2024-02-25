# Docker Compose Microservices Application

This repository contains a microservices application built using Docker Compose. It consists of multiple services including MySQL databases, Keycloak, Kafka, Zipkin, Prometheus, Grafana, and several microservices.

## How to Run the Application Using Docker

1. Clone this repository to your local machine:

   ```bash
   git clone <repository-url>
   ```

2. Navigate to the root directory of the cloned repository:

   ```bash
   cd <repository-directory>
   ```

3. Ensure Docker is installed on your machine.

4. Build the applications and create Docker images locally:

   ```bash
   mvn clean package jib:build -DskipTests
   ```

5. Start the applications using Docker Compose:

   ```bash
   docker-compose up -d
   ```

6. Access the application through the exposed ports specified in the `docker-compose.yml` file.

## How to Run the Application Without Docker

To run the application without Docker:

1. Clone this repository to your local machine (if not already done).

2. Navigate to the root directory of the cloned repository:

   ```bash
   cd <repository-directory>
   ```

3. Build the applications in each service's directory:

   ```bash
   mvn clean verify -DskipTests
   ```

4. Start each application in each service's directory:

   ```bash
   mvn spring-boot:run
   ```

5. Access the application through the respective ports defined in each service.

## Additional Notes

- The `docker-compose.yml` file contains configurations for each service, including volumes, ports, environment variables, and dependencies.

- The application includes various microservices (`product-service`, `order-service`, `inventory-service`, `notification-service`, etc.) each serving specific functionalities.

- Services such as Keycloak, Kafka, Zipkin, Prometheus, and Grafana are used for authentication, messaging, distributed tracing, monitoring, and visualization, respectively.

- Ensure that you have sufficient system resources (CPU, memory, disk space) available to run all the services effectively.

For more detailed setup instructions and configurations, please refer to the documentation of each individual service and the respective Dockerfiles if provided.

