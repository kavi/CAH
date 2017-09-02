#!/bin/bash
# Will create the database if it doesn't exist.

ROOT_USER=root
ROOT_PASS=
DB_NAME=cah
DB_USER=cah
DB_PASS=cah_rw_123


SQL_DIR=$1

# Check credentials
mysqlshow -u$DB_USER -p$DB_PASS "${DB_NAME}" > /dev/null && { echo "Db exists." ; exit 0 ; }
mysqlshow -u$ROOT_USER -p$ROOT_PASS "information_schema" > /dev/null && { CRED_OK=1 ; } || { echo "Invalid credentials for mysql." ; echo "Invalid user/pass for mysql." ; exit 1 ; }

# if CRED_OK==1 then:
# Check if 'cah' database exists:
mysqlshow -u$ROOT_USER -p$ROOT_PASS "${DB_NAME}" > /dev/null && { echo "Db exists." ; exit 0 ; }

MYSQL="mysql -u${ROOT_USER} -p${ROOT_PASS} -h127.0.0.1 -P 3306"


# Create user
echo "Creating MySQL user cah"
$MYSQL < "${SQL_DIR}/00_cah_user.sql"

MYSQL="mysql -u${DB_USER} -p${DB_PASS} -h127.0.0.1 -P 3306"

# Create schema
echo "Creating MySQL Schema cah..."
$MYSQL $DB_NAME < "${SQL_DIR}/01_cah_schema.sql"
$MYSQL $DB_NAME < "${SQL_DIR}/02_cah_data.sql"
$MYSQL $DB_NAME < "${SQL_DIR}/03_version_schema.sql"
echo "Done"

