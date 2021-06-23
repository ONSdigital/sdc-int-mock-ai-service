#!/bin/bash

MOCK_AI="http://localhost:8162"
WL_AI="https://whitelodge-eq-ai-api.census-gcp.onsdigital.uk"

function compare {
  ENDPOINT=$1

  curl -s -H "Authorization: Bearer $AI_TOKEN" $WL_AI/$ENDPOINT | jq . > /tmp/wl-ai.json

  curl -s $MOCK_AI/$ENDPOINT | jq . > /tmp/mock-ai.json

  # Compare real & mock AI results
  diff "/tmp/wl-ai.json" "/tmp/mock-ai.json" > /dev/null
  if [ $? -eq 0 ]
  then
    echo "Pass: $ENDPOINT"
  else
    echo "*FAIL*: $ENDPOINT"
  fi
}


# RH POSTCODE
compare "addresses/rh/postcode/CF32TW"
compare "addresses/rh/postcode/CF32TW?offset=1"
compare "addresses/rh/postcode/CF32TW?limit=3"
compare "addresses/rh/postcode/CF32TW?offset=101&limit=8"
# postcode with no results
compare "addresses/rh/postcode/SO996AB"


# PARTIAL - standard dataset
compare "addresses/partial?input=Shirley"
compare "addresses/partial?input=Shirley&offset=611"
compare "addresses/partial?input=Shirley&limit=18"
compare "addresses/partial?input=Shirley&offset=14&limit=77"
compare "addresses/partial?input=Shirley&offset=800&limit=177"
# no results 
compare "addresses/partial?input=rtoeutheuohh"


# POSTCODE
compare "addresses/postcode/EX24LU"
compare "addresses/postcode/EX24LU?offset=6"
compare "addresses/postcode/EX24LU?limit=1"
compare "addresses/postcode/EX24LU?offset=2&limit=90"
# no results
compare "addresses/postcode/SO996AB"


# UPRN
compare "addresses/rh/uprn/3244"
compare "addresses/rh/uprn/100040239948"
# not found
compare "addresses/rh/uprn/11"


# EOF
