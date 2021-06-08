#!/bin/bash

set -e


#------ /addresses/rh/postcode/{postcode} ------

URL="http://localhost:8161/addresses/rh/postcode/DN370AA"

curl -k -s -H "Content-Type: application/json" $URL
curl -k -s -H "Content-Type: application/json" $URL | jq .


#------ /addresses/partial ------
URL="http://localhost:8161/addresses/partial?input=Shirley"

curl -k -s -H "Content-Type: application/json" $URL
curl -k -s -H "Content-Type: application/json" $URL | jq .


#------ /addresses/postcode/{postcode} ------

URL="http://localhost:8161/addresses/rh/postcode/DN370AA"

curl -k -s -H "Content-Type: application/json" $URL
curl -k -s -H "Content-Type: application/json" $URL | jq .


#------ /addresses/rh/uprn ------

URL="http://localhost:8161/addresses/rh/postcode/DN370AA"

curl -k -s -H "Content-Type: application/json" $URL
curl -k -s -H "Content-Type: application/json" $URL | jq .

# EOF
