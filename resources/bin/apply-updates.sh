#!/bin/bash
DBNAME=cah
TMP_DIR=/tmp
APPLIED_TMP=${TMP_DIR}/applied.tmp
AVAILABLE_TMP=${TMP_DIR}/available.tmp
SQL_DIR=$1

DB_USER=cah
DB_PASS=cah_rw_123

pushd $SQL_DIR

MYSQL="mysql -u${DB_USER} -p${DB_PASS} -h127.0.0.1 -P 3306"

$MYSQL -e "select version, filename from version_history" $DBNAME | sed -e "s:\t:,:g" > ${APPLIED_TMP}

ls -1 updates > ${AVAILABLE_TMP}

cat ${AVAILABLE_TMP} | while read line ; do
  version=`echo $line | sed -e "s:\([0-9]\+\)-.*:\1:" -e "s/^0*//"`
  filename=$line
  scriptname=updates/$filename
  grep -q "^${version}," ${APPLIED_TMP} || { 
      echo "Applying update script: $filename"
      $MYSQL $DBNAME < $scriptname && $MYSQL $DBNAME -e "insert into version_history(version, filename) values ('${version}','${filename}')" && echo "Done."
  }
done

popd
