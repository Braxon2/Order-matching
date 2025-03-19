# Stock Exchange Service

A simple backend service for stock exchange order processing and storing, implemented with Spring Boot and WebFlux.

## Features

- WebSocket and REST API interfaces for communication

- Order processing and matching

- Real-time order book updates

- Non-blocking/asynchronous implementation

## Technologies used

- Java 17

- Maven 3.9.9

- Spring Boot 3.4.3

## Build & Run

To build the project:

```shell

mvn clean package

```

To run the application:

```shell

java -jar target/ordermatching-0.0.1-SNAPSHOT.jar

```

Or use Maven directly:

```shell

mvn spring-boot:run

```

The service will start on port 8080 by default.

## API Usage

### REST API

#### Create a new order

You can test the REST API with Postman.

Where you will make an HTTP request type of POST with this path:

```

http://localhost:8080/api/orders

```

With body in JSON format that looks like this.

```json

{

"type":"NEW_ORDER",

"orderType":"BUY",

"price":100.5,

"amount":10.0

}

```

#### Get order book

You can get order book with GET HTTP request with this path.

```

http://localhost:8080/api/orderbook

```

### WebSocket API

You can connect to the WebSocket endpoint at `ws://localhost:8080/ws/orders` and send/receive JSON messages.

#### Send a new order

```json

{

"type": "NEW_ORDER",

"orderType": "BUY",

"price": 100.5,

"amount": 10.0

}

```

#### Request order book

```json

{

"type": "GET_ORDER_BOOK"

}

```

#### Response formats

This responses are the same for REST API calls. But when you send a new order through websocket the response from the server will be order response as below and updated order book response when the order was processed.

Order response:

```json

{

"type": "ORDER_RESPONSE",

"id": "606e9a3f-5e51-4f99-b5e1-5784ef5c7e3a",

"status": "ACCEPTED",

"message": "Order processed successfully"

}

```

Order book response:

```json

{

"type": "ORDER_BOOK",

"buyOrders": [

{

"id": "606e9a3f-5e51-4f99-b5e1-5784ef5c7e3a",

"price": 100.5,

"amount": 10.0

}

],

"sellOrders": [

{

"id": "71e8a42b-2d67-4221-b6a8-14f853fa7571",

"price": 101.5,

"amount": 5.0

}

]

}

```

## Testing with WebSocket

You can test WebSocket client also through Postman with this endpoint:

```

ws://localhost:8080/ws/orders

```

Send JSON messages to interact with the service.

## Design Decisions

1. **In-memory storage**: Used concurrent data structures to store orders rather than a database to keep the implementation simple.

2. **Non-blocking/asynchronous**: Implemented using Spring WebFlux and Project Reactor for non-blocking I/O.

3. **Order matching**: Orders are matched based on price-time priority.

4. **Real-time updates**: WebSocket clients receive order book updates whenever changes occur.
