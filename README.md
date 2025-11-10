# Paymeter Backend Software Engineer Assessment

## Deliverable

There is quite some flexibility in what can be delivered, just ensure the service:

* is a web service
* can run on Mac OS X or Linux (x86-64)
* is written in Java
* uses Spring Boot WebFlux, Spring Data R2DBC, MySQL
* can make use of any existing open source libraries that don't directly address the problem statement (use your best judgement)

Send us:

* The full source code, including any code written which is not part of the normal program run (scripts, tests)
* Clear instructions on how to obtain and run the program
* Please provide any deliverables and instructions using a public Github (or similar) Repository as several people will need to inspect the solution

## Evaluation
The point of the exercise is for us to see some of the code you wrote (and should be proud of).
We will especially consider:

* Code organisation
* Quality
* Readability
* Actually solving the problem

## Requirements
* Java 17 or higher
* Spring Boot 3.x
* MySQL database (with R2DBC support)
* Docker and Docker Compose (to run the database)
* Gradle
* 
## Instructions

To run the application, run the following command in a terminal window:
```shell
OPCION # 1
# java 17 in host
1. Have a MySQL database running locally.
2. Create a database named parking_db.
3. Change the connection URL, username, and password in the application.ymal file.
   r2dbc:
     url: r2dbc:mysql://localhost:3306/parking_db

./gradlew bootRun

OPCION # 2
# using docker
docker compose up --build

The service includes a docker-compose.yml file with the necessary configuration to run a MySQL database image and launch the application in a Docker container.

When the application starts, it creates a table called parking in the database and inserts two records as initial configuration. This information is contained in the following files: schema.sql y data.sql.

El servicio espera que la base de datos haya iniciado para poder crear la tabla e insertar los registros iniciales, esta configuración está en el docker-compose.yml.
  app:
    build: .
    container_name: parking-api
    depends_on:
      mysql:
        condition: service_healthy #WAIT UNTIL THE DATABASE IS UP TO START THE APP

```

Console view

![ejemplodockercompose.png](/images/ejemplodockercompose.png)

Docker Desktop
Waiting for the database to be OK to start
![dcoker2.png](/images/docker2.png)

Database and service OK
![dcoker.png](/images/docker.png)


Check service is running:
```shell
curl http://localhost:8080
```

Execute the following command to test the application:
```shell
# java 17 in host
./gradlew test

# using docker
docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:8-jdk17 gradle test
```

```shell
# java 17 in host jacoco report
./gradlew test jacocoTestReport
```

Go to the project root and in the /build/jacocoHtml/index.html folder, you will find the jacoco report file.

![jacocoReport.png](/images/jacocoReport.png)

## Challenge

Our customers want to be sure they're properly charging the correct amount on their parkings. 
For this reason, we plan to create a new pricing calculation feature so they can test multiple scenarios.
We have two customers with one parking each:

* Customer 1:
  * Parking id: `P000123`
  * Hourly price: 2€
  * Discounts
    * Max price per day: 15€

* Customer 2 
  * Parking id: `P000456`
  * Hourly price: 3€
  * Discounts
    * Max price every 12 hours: 20€
    * First hour is free

Note:
  * The price of a fraction of an hour is the same as a full hour
  * If duration of the stay is less than one minute, parking is free
  * There's no max time for a stay
  * There's no limit of times that max price discount can be applied
  * Max price discount starts counting when entering the parking 

Requirements:
* Endpoint: POST `/tickets/calculate`
* Request:
  * Content type: JSON
  * Fields:
    * `parkingId`: string (required)
    * `from`: ISO 8601 timestamp string (required)
    * `to`: ISO 8601 timestamp string (optional, defaults to current time)
* Response:
  * Content type: JSON
  * Fields:
    * `parkingId`: string (required)
    * `from`: ISO 8601 timestamp string (required)
    * `to`: ISO 8601 timestamp string (required)
    * `duration`: integer (minutes)
    * `price`: string (integer amount + currency code, e.g. 2.35€ would be `"235EUR"`)
  * Status codes:
    * 200 ok
    * 400 invalid request
    * 404 parking not found
    * 500 server error
    * (feel free to return any status codes needed)

Example usage:
```shell
curl -X POST http://localhost:8080/tickets/calculate \
  -H "Content-Type: application/json" \
  -d '{"parkingId":"P000123","from":"2024-02-27T09:00:00"}'
```
## Solution

### Database Model (Conceptual Schema)
Since the pricing parameters are fixed for each customer and are the basis of the calculation logic, it is crucial to store this configuration data in a table.

Table: Parking
This table stores the price and discount settings for each parking lot.

![database.png](/images/database.png)

### Diagram 1:

![/images/diagrama1.png](/images/diagrama1.png)


### Alternative database solution

An alternative solution for the database is to separate the rate configuration into a different table.

This allows multiple parking facilities to share the same pricing configuration without duplicating data.

Here are two database tables that meet this requirement: Parkings and Parking_Rates.

Parking Rates:

![/images/altDatabase1.png](/images/altDatabase1.png)

Parkings:

![/images/altDatabase2.png](/images/altDatabase2.png)

### Diagram 2:

![/images/diagrama2.png](/images/diagrama2.png)


## Important
To simplify the exercise, we will work with a single Parking table, which is the first proposed solution.



### Common scenarios


The price of a fraction of an hour is the same as a full hour
```shell
Request:

curl --location 'http://localhost:8080/tickets/calculate' \
--header 'Content-Type: application/json' \
--data '{
    "parkingId": "P000123",
    "from": "2025-11-10T08:25:00.123",
    "to":   "2025-11-10T08:45:50.123"
}'

Response:
{
        "parkingId": "P000123",
        "from": "2025-11-10T08:25:00.123",
        "to": "2025-11-10T08:45:50.123",
        "duration": 20,
        "price": "200EUR"
}
```

If duration of the stay is less than one minute, parking is free

```shell
Request:

curl --location 'http://localhost:8080/tickets/calculate' \
--header 'Content-Type: application/json' \
--data '{
    "parkingId": "P000456",
    "from": "2025-11-10T08:45:00.123",
    "to":   "2025-11-10T08:45:50.123"
}'

Response:
{
        "parkingId": "P000456",
        "from": "2025-11-10T08:45:00.123",
        "to": "2025-11-10T08:45:50.123",
        "duration": 0,
        "price": "0EUR"
}
```

### specific scenarios by client


### Client 1:

```shell
Hourly price: 2€
Request:

curl --location 'http://localhost:8080/tickets/calculate' \
--header 'Content-Type: application/json' \
--data '{
    "parkingId": "P000123",
    "from": "2025-11-10T08:45:00.123",
    "to":   "2025-11-10T09:43:00.123"
}'

Response:
{
  "parkingId": "P000123",
  "from": "2025-11-10T08:45:00.123",
  "to": "2025-11-10T09:43:00.123",
  "duration": 58,
  "price": "200EUR"
}
```

```shell
Max price per day: 15€
Request:

curl --location 'http://localhost:8080/tickets/calculate' \
--header 'Content-Type: application/json' \
--data '{
    "parkingId": "P000123",
    "from": "2025-11-10T08:45:00.123",
    "to":   "2025-11-11T07:43:00.123"
}'

Response:
{
 "parkingId": "P000123",
 "from": "2025-11-10T08:45:00.123",
 "to": "2025-11-11T07:43:00.123",
 "duration": 1378,
 "price": "1500EUR"
}
```


```shell
Max price per day: 15€
Request:

curl --location 'http://localhost:8080/tickets/calculate' \
--header 'Content-Type: application/json' \
--data '{
    "parkingId": "P000123",
    "from": "2025-11-10T08:45:00.123",
    "to":   "2025-11-11T07:43:00.123"
}'

Response:
{
 "parkingId": "P000123",
 "from": "2025-11-10T08:45:00.123",
 "to": "2025-11-11T07:43:00.123",
 "duration": 1378,
 "price": "1500EUR"
}
```


### Client 2:

```shell
Hourly price: 3€ and First hour is free
Request:

curl --location 'http://localhost:8080/tickets/calculate' \
--header 'Content-Type: application/json' \
--data '{
    "parkingId": "P000456",
    "from": "2025-11-10T08:45:00.123",
    "to":   "2025-11-10T10:43:00.123"
}'

Response:
{
  "parkingId": "P000456",
  "from": "2025-11-10T08:45:00.123",
  "to": "2025-11-10T10:43:00.123",
  "duration": 118,
  "price": "300EUR"
}
```



```shell
Max price every 12 hours: 20€

Request:
curl --location 'http://localhost:8080/tickets/calculate' \
--header 'Content-Type: application/json' \
--data '{
    "parkingId": "P000456",
    "from": "2025-11-10T08:45:00.123",
    "to":   "2025-11-10T20:43:00.123"
}'

Response:
{
        "parkingId": "P000456",
        "from": "2025-11-10T08:45:00.123",
        "to": "2025-11-10T20:43:00.123",
        "duration": 718,
        "price": "2000EUR"
}
```