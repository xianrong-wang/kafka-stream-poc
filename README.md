# kafka-stream-poc
- build
./gradlew build
- run from local
  - install kafka
    - run kafka
  - install redis
    - run redis
  - run this service: `./gradlew bootRun`
 
 
- rest endpoints
  - create message: 
  ```curl --location --request POST 'http://localhost:8080/reports/' \
  --header 'Content-Type: application/json' \
  --data-raw '{
      "key":"rpt25",
      "name":"report test 01", "reportDate":"20221022", "status": ""
  }'
  ```
  - view result:
  ```
  curl --location --request GET 'http://localhost:8080/reports/rpt25'
  ```
