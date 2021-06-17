# SDC Mock AI (Address Index) Service
TODO: Add summary of service


## Set Up
Do the following steps to set up the code to run locally:
* Install Java 15+ locally
* Make sure that you have a suitable settings.xml file in your local .m2 directory
* Clone the mock-ai repository locally


## Running

There are two ways of running this service

* The first way is from the command line after moving into the same directory as the pom.xml:
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
* The second way requires that you first create a JAR file using the following mvn command (after moving into the same directory as the pom.xml):
    ```bash
    mvn clean package
    ```
This will create the JAR file in the Target directory. You can then right-click on the JAR file (in Intellij) and choose 'Run'.


## End Point

TODO: Add list of supported endpoints.


## Manual testing

TODO: Add curl commands to show local running


## Docker image build
Is switched off by default for clean deploy. Switch on with;

* mvn dockerfile:build -Dskip.dockerfile=false


## Copyright
Copyright (C) 2021 Crown Copyright (Office for National Statistics)
