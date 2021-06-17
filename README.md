
# Mock AI
This repository is a test service and can be run instead of the an Address Index service.

## Running

When running successfully version information can be obtained from the info endpoint
    
* localhost:8162/info
* localhost:8162/addresses/info
* localhost:8162/capture/info

## Manual testing

### Addresses endpoints 

These endpoints return AI captured responses which are held within the data resources directory. 
If no data is held for a particular search query then a default 'notFound' response is returned. This mirrors the
behaviour of AI if it is also asked for data which it doesn't hold.

    curl -s localhost:8162/addresses/rh/postcode/SO93a
    
    curl -s localhost:8162/addresses/partial?input=High%20Street
    
    curl -s localhost:8162/addresses/postcode/SO155NF
    
    curl -s localhost:8162/addresses/rh/uprn/3244
    
    curl -s localhost:8162/addresses/info

### Capture endpoints

These are the same as the addresses endpoints except that they start with '/capture'. This causes the mock-ai
to make a call to AI and store the result in a data file. Subsequent calls to the addresses endpoints will 
return the captured data.

    curl -s localhost:8162/capture/addresses/rh/postcode/DN370AA
    
    curl -s localhost:8162/capture/addresses/partial?input=High%20Street
    
    curl -s localhost:8162/capture/addresses/postcode/DN370AA
    
    curl -s localhost:8162/capture/addresses/rh/uprn/3244
    
    curl -s localhost:8162/capture/info
    
## Copyright
Copyright (C) 2021 Crown Copyright (Office for National Statistics)
