<div align="center">
  <img src="./FrontInvernaderoAmigochas/src/recursos/logo.png" alt="Logo Invernaderos" width="200"/>
  <h1>🌿 Smart Greenhouse IoT System</h1>
  <h3>Distributed Microservices Architecture for Real-Time Monitoring</h3>
  
  <p>
    <b>Status:</b> 🚀 MVP Completed | <b>License:</b> MIT
  </p>

  <p>
    <a href="#-architecture">Architecture</a> •
    <a href="#-tech-stack">Tech Stack</a> •
    <a href="#-services">Microservices</a> •
    <a href="#-getting-started">Getting Started</a>
  </p>
</div>

---

## 📖 Overview

**Smart Greenhouse ("Invernadero Amigochas")** is a robust distributed IoT system designed to monitor and manage environmental conditions (temperature, humidity, etc.) in real-time. 

Unlike traditional monoliths, this project implements a **Event-Driven Microservices Architecture**, ensuring high scalability, fault tolerance, and secure data processing using **RabbitMQ** for internal messaging and **MQTT** for sensor communication.

### ✨ Key Features
* **📡 Real-Time Telemetry:** Ingestion of sensor data via MQTT protocol.
* **🔐 High Security:** Custom implementation of **RSA Encryption** for inter-service communication and **JWT** for user session management.
* **🐳 Fully Containerized:** Orchestrated entirely with **Docker & Docker Compose** for consistent deployment.
* **⚡ Reactive Frontend:** A modern dashboard built with **React & Vite** to visualize anomalies and alerts instantly.

---

## 🛠 Tech Stack

| Domain | Technologies |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot, Spring Cloud, Hibernate |
| **Frontend** | React.js, Vite, Tailwind CSS, Axios |
| **Messaging** | RabbitMQ (AMQP), Mosquitto (MQTT) |
| **Database** | MySQL, MongoDB (for Anomaly logs) |
| **DevOps** | Docker, Docker Compose, Git |
| **Security** | Spring Security, JWT, RSA Key-Pair Exchange |

---

## 🧩 Microservices Breakdown

The system is composed of several autonomous services:

| Service | Description | Port |
| :--- | :--- | :--- |
| **🔌 API Gateway** | Central entry point that routes traffic and handles load balancing. | `8080` |
| **🛡️ Auth Service** | Manages user registration and JWT token issuance. | `8081` |
| **🌡️ Sensor Management** | CRUD operations for greenhouses and sensor registration. | `8082` |
| **📡 Data Ingestion** | Listens to MQTT queues and processes raw sensor data. | `8083` |
| **🚨 Alarmator** | Evaluates rules and triggers alerts (Email/SMS) when thresholds are breached. | `8084` |
| **🧠 Anomalyzer** | AI/Logic component that detects irregular patterns in data streams. | `8085` |
| **💻 Frontend** | User interface for administrators and monitoring. | `5173` |

---

## 🚀 Getting Started

This project includes a `docker-compose.yml` file to spin up the entire infrastructure with one command.

### Prerequisites
* Docker & Docker Compose installed.
* Java 17 JDK (for local development).
* Node.js & npm.

### Installation

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/tu-usuario/invernaderos-amigochas.git](https://github.com/tu-usuario/invernaderos-amigochas.git)
    cd invernaderos-amigochas
    ```

2.  **Infrastructure Setup (The easy way)**
    Run the complete environment (Databases, RabbitMQ, Services):
    ```bash
    docker-compose up --build -d
    ```

3.  **Access the Dashboard**
    Open your browser and navigate to:
    `http://localhost:5173`

---

<div align="center">
  <sub>Built with ❤️ by <a href="https://github.com/victoriaaavega">Victoria Vega</a>, Diego Valenzuela, Ángel Huerta y Ricardo Gutiérrez as a Capstone Project @ ITSON</sub>
</div>
