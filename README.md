This repository contains a **microservice SpringBoot RESTful web application** simulating user-task-notification system:
* Entities are created via **RestAPI** calls and stored in a **PostgreSQL** database. **Flyway** is used to control database changes.
* Microservice communications are implemented using both **Kafka** (notifications are published when a new task is created) and HTTP calls (taskService requests information about existing users from userService).
* TaskService uses **Redis** for caching.
* **Prometheus&Grafana** are implemented for monitoring and health checks.
* **Docker-compose** is provided for building and running all microservices in one command.
