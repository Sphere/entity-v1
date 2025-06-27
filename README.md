# Entity-v1 Setup Guide

Follow the steps below to set up and run the `entity-v1` project locally.

---

## 1. Clone the Repository

```bash
git clone git@github.com:Sphere/entity-v1.git
```

---

## 2. Navigate to the Project Directory

```bash
cd entity-v1
```

---

## 3. Install Dependencies with Maven

Use **Java 8**.

```bash
mvn clean install -DskipTests
```

---

## 4. Set Up PostgreSQL Database

- Create a new database in your local PostgreSQL instance (e.g., `entity-v1`).

---

## 5. Execute SQL to Create Required Tables

Run the following SQL commands **in the given order** in your PostgreSQL database:

```sql
CREATE TABLE data_node (
  "id" SERIAL NOT NULL,
  "type" varchar(45) DEFAULT NULL,
  "name" TEXT,
  "description" TEXT,
  "additional_properties" json NULL,
  "status" varchar(45) DEFAULT NULL,
  "source" varchar(45) DEFAULT NULL,
  "level" varchar(45) DEFAULT NULL,
  "level_id" integer DEFAULT NULL,
  "created_date" TIMESTAMP DEFAULT NULL,
  "created_by" varchar(250) DEFAULT NULL,
  "updated_date" TIMESTAMP DEFAULT NULL,
  "updated_by" varchar(250) DEFAULT NULL,
  "reviewed_date" TIMESTAMP DEFAULT NULL,
  "reviewed_by" varchar(250) DEFAULT NULL,
  "is_active" BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY ("id")
);

CREATE TABLE node_mapping_parent (
  "id" SERIAL NOT NULL,
  "type" varchar(45) DEFAULT NULL,
  "parent_id" INTEGER,
  "child" varchar(45) DEFAULT NULL,
  "status" varchar(45) DEFAULT NULL,
  PRIMARY KEY ("id"),
  UNIQUE(parent_id, child),
  CONSTRAINT fk_datanode FOREIGN KEY(parent_id)
  REFERENCES data_node(id)
);

CREATE TABLE node_mapping_child (
  "id" SERIAL NOT NULL,
  "parent_map_id" INTEGER NOT NULL,
  "child_id" INTEGER NOT NULL,
  UNIQUE(parent_map_id, child_id),
  CONSTRAINT fk_child FOREIGN KEY(child_id)
  REFERENCES data_node(id),
  CONSTRAINT fk_parent FOREIGN KEY(parent_map_id)
  REFERENCES node_mapping_parent(id)
);

CREATE TABLE bookmarks (
  "id" SERIAL NOT NULL,
  "node_id" INTEGER NOT NULL,
  "user_id" varchar(250) NOT NULL,
  UNIQUE(node_id, user_id),
  CONSTRAINT fk_datanode FOREIGN KEY(node_id)
  REFERENCES data_node(id)
);
```

---

## 6. Verify Database Tables

- Ensure that the following tables are created:
  - `data_node`
  - `node_mapping_parent`
  - `node_mapping_child`
  - `bookmarks`

---

## 7. Configure `application.properties`

Update `sunbird-entity/src/main/resources/application.properties` with your PostgreSQL credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/entity-v1
spring.datasource.username=postgres
spring.datasource.password=test123
```

---

## 8. Run the Application

- Open the project in IntelliJ.
- Run `EntityApplication.java`.

---
