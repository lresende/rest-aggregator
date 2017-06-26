# File Aggregation Service

This service provides a fast/easy CSV aggregation of csv data. The current service REST API accepts a csv, a column to group on, and a column to aggregate and returns the result as plain text.

The service implementation is done using Apache Tuscany, which in the scope of this REST Service, provides a thin wrapper to a JAX-RS framework and enable service composition.

## Sample Input Data

```
first_name,last_name,count
piers,smith,10
kristen,smith,17
john,lee,3
sam,eagle,15
john,eagle,19

```

## Sample aggregated output

```
smith,27
lee,3
eagle,34
```

## Building the service

```
mvn clean install
```

## Starting the service

```
mvn exec:java
```

## Service invocation example

* Process input.csv and group on column 'last_name' and aggregate column 'count'
```
curl -X POST -H "Content-Type: multipart/form-data" -F "body=@input.csv"  "http://localhost:8080/services/aggregator/csv?keyColumn=last_name&valueColumn=count"
```

* Process generated_input.csv and group on column 'last_name' and aggregate column 'count'
```
curl -X POST -H "Content-Type: multipart/form-data" -F "body=@generated_input.csv"  "http://localhost:8080/services/aggregator/csv?keyColumn=last_name&valueColumn=count"
```

* Process generated_input.csv and group on column 'last_name' and aggregate column 'count'
```
curl -X POST -H "Content-Type: multipart/form-data" -F "body=@generated_input.csv"  "http://localhost:8080/services/aggregator/csv?keyColumn=first_name&valueColumn=count"
```

Note: Make sure you are in the same folder as the available csv files (e.g. src/test/resources)