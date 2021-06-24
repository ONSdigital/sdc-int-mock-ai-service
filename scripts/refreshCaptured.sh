#!/bin/bash

# This script 


set -e

TMP_FILE="/tmp/found"

# Move to the captured data directory
cd "$(dirname "$0")"
cd ../src/main/resources/data

# Find captured files that need to be refetched
find . -name "*json" | grep -v notFound > $TMP_FILE

# Convert the list of captured files into curl commands
sed -i ".bak" 's|-|%20|g' $TMP_FILE
sed -i ".bak" 's|/partial/|/partial?input=|g' $TMP_FILE
sed -i ".bak" 's|^.|curl -s localhost:8162/capture|g' $TMP_FILE
sed -i ".bak" 's|.json$||g' $TMP_FILE

# Invoke capture endpoint to refresh existing data
cat $TMP_FILE | while read line 
do
  echo "Refreshing: $line"
  
  $line > /tmp/refreshCapture.result.json
  if [ $? -ne 0 ]
  then
    echo "*FAIL*"
  fi
done
