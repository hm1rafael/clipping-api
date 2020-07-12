Requirements
===========

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0b19d54782cd415b9e3487da813caba7)](https://app.codacy.com/manual/hm1rafael/clipping-api?utm_source=github.com&utm_medium=referral&utm_content=hm1rafael/clipping-api&utm_campaign=Badge_Grade_Dashboard)

- Java 11
- Maven 3.6
- Google cloud SDK
- Google cloud datastore emulator (testing)

Configuration
======

Requires configuring google credentials to access your DATASTORE.
You can do this through:
 - the ```GOOGLE_APPLICATION_CREDENTIALS``` environment variable;
 - Spring boot properties ```spring.cloud.gcp.datastore.credentials.location``` or ```spring.cloud.gcp.datastore.credentials.encoded-key```
 
 You can check documentation for them on
 - https://cloud.google.com/docs/authentication/getting-started
 - https://cloud.spring.io/spring-cloud-static/Greenwich.RC1/multi/multi__spring_data_cloud_datastore.html

Endpoints
========
  *GET ```/api/clipping/{id}?```* 
  --
  Use to load all clipping information store in the datastore.
  This search is paginated, by default, only 20 are show in each page.
  
  You can change the page size through the query param: ```size```.
  
  You can change the page through the query param: ```page```. 
  
  If an id is passed through the query param, a specific Clipping is loaded
  
  test:
  ```
    curl -X GET http://localhost:8080/api/clipping/
  ```
  *GET ```/api/user/alerts```*
  -
  Search all the alerts created. This search is paginated. by default, only 20 are show in each page.
  
  You can change the page size through the query param: ```size```.
    
  You can change the page through the query param: ```page```.
  test:
    ```
      curl -X GET http://localhost:8080/api/user/alerts
    ```
    
  *GET ```/api/user/alerts```*
  -
  Search all the hearings created. This search is paginated. by default, only 20 are show in each page.
  
  You can change the page size through the query param: ```size```.
    
  You can change the page through the query param: ```page```.
  test:
    ```
      curl -X GET http://localhost:8080/api/user/hearings
    ```  
    
  *POST ```/api/clipping```* 
  - 
  Used to send clippings to be saved.
  
  If clippings are marked as ```important=true``` a new ```Alert``` is created for each that is marked.
  
  If clipping have ```ClassificationType``` as ```HEARING``` a new ```HearingAppointment``` is created 
  
  test:
  ```
    curl -H "Content-Type: application/json" \
        -X POST http://localhost:8080/api/clipping/ \
        -d \
        '[ 
          { 
            "clippingMatter": "string", 
            "classificationType": "HEARING", 
            "clippingDate": "2020-07-12",
            "classifiedDate": "2020-07-12",
            "classifiedTime": "string",
            "important": true
          }
        ]'  
  ```  
 
 *DELETE ```/api/clipping/{id}?```*
 -
 
 Delete a specific clipping on the datastore, or all clippings if no id is informed.
 
 test:
 ```
    curl -X DELETE http://localhost:8080/api/clipping/  
```

*PATCH ```/api/clipping/{id}```*
-

Update specific clipping to confirmed by the user.
test:
 ```
    curl -X PATCH http://localhost:8080/api/clipping/  
```
 
Swagger documentation
--
You can take a look on to check schemas and endpoints
http://localhost:8080/swagger-ui 
 
Demo application
======
https://sunlit-amulet-282621.ew.r.appspot.com/api/clipping/

TODO
===
Tenant support