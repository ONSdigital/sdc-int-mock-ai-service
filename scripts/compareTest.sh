#!/bin/bash

#
# This script acts as a unit test for the mock-ai.
#
# It invokes the real and mock Address Index for a number of queries.
# The script then does a diff between the real vs. mocked results.
# 
# NOTE: This script requires an AI token to run. This must be set in 
# the 'AI_TOKEN' environment variable.
# By default the script points at a WL based AI service.
# 
# The mock-ai must be running locally.
#

MOCK_AI="http://localhost:8162"
WL_AI="https://whitelodge-eq-ai-api.census-gcp.onsdigital.uk"

# Run the comparison of the real vs. mock-ai responses
function compare {
  ENDPOINT=$1

  # Get the response status codes from genuine and mock AI
  curl -s -H "Authorization: Bearer $AI_TOKEN" -I $WL_AI/$ENDPOINT | grep HTTP | grep -Eo "[0-9]{3}" > /tmp/wl-ai.status.txt
  curl -s -I $MOCK_AI/$ENDPOINT | grep HTTP | grep -Eo "[0-9]{3}" > /tmp/mock-ai.status.txt

  # Compare real & mock AI status codes
  diff "/tmp/wl-ai.status.txt" "/tmp/mock-ai.status.txt" > /dev/null
  if [ $? -ne 0 ]
  then
    echo "*FAIL STATUS*: $ENDPOINT"
    echo -n "  Genuine: "
    cat /tmp/wl-ai.status.txt
    echo -n "  Mock   : "
    cat /tmp/mock-ai.status.txt
  fi

  # Get responses from genuine and mock AI
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
compare "addresses/rh/postcode/CF3%202TW"

# PARTIAL
compare "addresses/partial?input=Shirley"
compare "addresses/partial?input=Shirley&offset=611"
compare "addresses/partial?input=Shirley&limit=18"
compare "addresses/partial?input=Shirley&offset=14&limit=77"
compare "addresses/partial?input=Shirley&offset=800&limit=177"

# POSTCODE
compare "addresses/postcode/EX24LU"
compare "addresses/postcode/EX24LU?offset=6"
compare "addresses/postcode/EX24LU?limit=1"
compare "addresses/postcode/EX24LU?offset=2&limit=90"

# UPRN
compare "addresses/rh/uprn/3244"
compare "addresses/rh/uprn/100040239948"

# TYPE AHEAD"
compare "addresses/eq?input=Pineapple"
compare "addresses/eq?input=Pinea"
compare "addresses/eq?input=SO1"
compare "addresses/eq?input=B1"
compare "addresses/eq?input=B15"
compare "addresses/eq?input=46%20West%20B153Q"

# Check responses for data with no results in real or mock AI
compare "addresses/rh/postcode/SO996AB"
compare "addresses/rh/postcode/SO31%209UP"
compare "addresses/partial?input=rtoeutheuohh"
compare "addresses/postcode/SO996AB"
compare "addresses/postcode/SO31%209UP"
compare "addresses/rh/uprn/11"
compare "addresses/eq?input=tcexdeupydhp"

# EOF
