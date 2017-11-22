#!/bin/bash

# Drops the Propr database, creates a new one and inserts the dummydata in it.

psql -U postgres -w -c "DROP DATABASE IF EXISTS propr" 

psql -U postgres -w -c "CREATE DATABASE propr OWNER propr" 

echo "Migrating database.."

mvn -Dflyway.configFile=src/main/resources/flyway.properties flyway:migrate

echo "Inserting dummy data into database"

psql -d propr -U postgres -w -f src/main/resources/db/dummy/DummyData.sql 
