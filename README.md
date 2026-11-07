# Student Master

## Setup

1. Install **JDK 17+**, **Maven 3.8+** and **MySQL 8**.
2. Create the database and sample data:
   ```bash
   mysql -u root -p < sql/schema.sql
   ```
3. Create a file named `db.properties` in the project root
   ```properties
   db.url=jdbc:mysql://localhost:3306/university?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.user=root
   db.password=YOUR_PASSWORD
   ```
   
4. Run:
   ```bash
   mvn compile exec:java
   ```