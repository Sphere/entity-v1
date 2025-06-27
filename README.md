1) Clone the repo
   git clone git@github.com:Sphere/entity-v1.git

2) Navigate to the cloned repo
   cd entity-v1

3) Install dependencies with maven
   mvn clean install -DskipTests (use java 8)

4)Create a new database in local postgres

5) Run the following commands on postgres (in this order)
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
CONSTRAINT fk_datanode
FOREIGN KEY(parent_id)
REFERENCES data_node(id)
);

CREATE TABLE node_mapping_child (
"id" SERIAL NOT NULL,
"parent_map_id" INTEGER NOT NULL,
"child_id" INTEGER NOT NULL,
UNIQUE(parent_map_id, child_id),
CONSTRAINT fk_child
FOREIGN KEY(child_id)
REFERENCES data_node(id),
CONSTRAINT fk_parent
FOREIGN KEY(parent_map_id)
REFERENCES node_mapping_parent(id)
);


CREATE TABLE bookmarks (
"id" SERIAL NOT NULL,
"node_id" INTEGER NOT NULL,
"user_id" varchar(250) NOT NULL,
UNIQUE(node_id, user_id),
CONSTRAINT fk_datanode
FOREIGN KEY(node_id)
REFERENCES data_node(id)
);

6) Check if these tables are now present in the new database

7) Update application.properties in sunbird-entity with the db details
   spring.datasource.url=jdbc:postgresql://localhost:5432/entity-v1
   spring.datasource.username=postgres
   spring.datasource.password=test123

8) Run the EntityApplication from Intellij