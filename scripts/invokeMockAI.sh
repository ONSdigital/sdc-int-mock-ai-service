#!/bin/bash

set -e


#------ /addresses/rh/postcode/{postcode} ------

URL="http://localhost:8162/addresses/rh/postcode/SO155NF"

curl -k -s -H "Content-Type: application/json" $URL | jq . | tee /tmp/1.rhPostcode.after.json


#------ /addresses/partial ------
URL="http://localhost:8162/addresses/partial?input=Shirley"

curl -k -s -H "Content-Type: application/json" $URL | jq . | tee /tmp/2.partial.after.json


#------ /addresses/postcode/{postcode} ------

URL="http://localhost:8162/addresses/postcode/EX41EH"

curl -k -s -H "Content-Type: application/json" $URL | jq . | tee /tmp/3.postcode.after.json


#------ /addresses/rh/uprn ------

URL="http://localhost:8162/addresses/rh/uprn/3244"

curl -k -s -H "Content-Type: application/json" $URL | jq . | tee /tmp/4.uprn.after.json


echo "3.postcode"
diff /tmp/3.postcode.before.json /tmp/3.postcode.after.json

echo "4.uprn"
diff /tmp/4.uprn.before.json /tmp/4.uprn.after.json

# EOF
