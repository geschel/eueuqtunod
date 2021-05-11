# Eueuq Tunod - Microservice REST-API

How to build and start the Main application
---
Requirements: *`git, maven, jre8+`*

1. TODO git checkout
1. enter project folder `cd {project_folder}`
1. Run `mvn clean package` to build your application
1. Start application with `java -jar target/donut-queue-1.0.0.jar server config.yml`


Technologies used:
---
1. Dropwizard (Jetty wrapper library)
1. H2 (In-Memory SQL database for queue)
1. RestAssured (End-to-End testing)
1. git
1. maven
1. Swagger (generates documentation)
1. Postman


the endpoints and their purpose:
---

#### client submits an order
Endpoint: **`/orders`**

```
curl --location --request POST 'http://localhost:8080/orders' --header 'Content-Type: application/json' --data-raw '{ "client_id": 1, "quantity": 1 }'
```


#### manager inspects the queue
Endpoint: **`/orders`**

```
curl --location --request GET 'http://localhost:8080/orders'
```

#### client checks his order's status
Endpoint: **`/orders/{client_id}`**

```
curl --location --request GET 'http://localhost:8080/orders/1'
```

#### client cancels his order
Endpoint: **`/orders/{client_id}`**

```
curl --location --request DELETE 'http://localhost:8080/orders/1'
```

#### jim removes donuts from queue in order to fill his delivery cart
Endpoint: **`/delivery_cart`**

```
curl --location --request POST 'http://localhost:8080/delivery_cart' --header 'Content-Type: application/json' --data-raw '{}'
```

#### to generate swagger API documentation from code
Endpoint: **`/swagger.json`**

```
curl --location --request GET 'http://localhost:8080/swagger.json'
```

#### to check server health
Endpoint: **`/healthcheck`**

```
curl --location --request GET 'http://localhost:8081/healthcheck'
```

