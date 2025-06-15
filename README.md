# spring-batch-data-processor
## Spring Batch - NYC Taxi Data ETL Project

## 📌 Project Overview

This project demonstrates an ETL (Extract, Transform, Load) pipeline built with **Spring Batch**.

- **Input**: A **Parquet** file containing New York City Taxi trip data.
- **Transformation**: The Parquet file is **converted to CSV** (outside this Spring Batch app).
- **Load**: Spring Batch **reads the CSV file (3GB+, ~20 million records)** and loads the data into a **PostgreSQL database**.
- **Execution**: This is a **fully local setup**, with no Docker or containerization involved.

---

## 🛠️ Technologies Used

- Java 17+
- Spring Boot 3.x
- Spring Batch
- PostgreSQL
- Maven
- Lombok

---

## 📂 Project Structure
    spring-batch-data-processor/
    ├── src/
    │ ├── main/
    │ │ ├── java/com/yourpackage/
    │ │ │ ├── config/
    │ │ │ ├── entity/
    │ │ │ ├── repository
    │ │ │ └── SpringBatchDataProcessorApplication.java
    │ ├── resources/
    │ │ ├── application.yml # DB and Batch Config
    └── pom.xml

---

## 🚀 How to Run Locally

### ✅ Prerequisites

- Java 17+
- Maven
- PostgreSQL installed and running locally
- CSV file: NYC taxi data (~3GB+, 20M records)

---

### 📥 Step-by-Step Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/muralimittireddy/spring-batch-data-processor.git
   cd spring-batch-data-processor
2. **Configure PostgreSQL**

   Create a database:

       CREATE DATABASE tripsdb;
   Update your application.yml with correct DB credentials:
   
        spring:
          datasource:
            url: jdbc:postgresql://localhost:5432/tripsdb
            username: your_username
            password: your_password

 3. **Place the CSV File**

    Put your large CSV file (taxi_trip_data.csv) in the specified location or update the path in FlatFileItemReader.

 4. Build and Run the App

        mvn clean install
        java -jar target/spring-batch-data-processor-0.0.1-SNAPSHOT.jar

📊 Output
   
   Once the job completes, your PostgreSQL database (tripsdb) will have the entire 20M+ records from the CSV file loaded efficiently via Spring Batch.

🧾 Notes
  No Docker/Cloud used — purely local setup.
  
  You can monitor job completion through logs and use a simple listener.
  
  If your machine has limited memory, consider chunking and tuning reader/writer performance in batch config.

