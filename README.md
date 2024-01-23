# spring-idempotency-filter

Idempotent Request Filter for Spring Boot is a library that helps solve the idempotency problem in distributed systems by utilizing the idempotency request header and a distributed caching mechanism (e.g., Hazelcast) through Spring Caching Abstraction.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)

### Problem Statement

POST and PATCH HTTP methods are not inherently idempotent, meaning that repeating the same request can lead to unintended consequences, especially in distributed systems.
This library addresses this issue by introducing a filter that leverages the idempotency request header to prevent duplicate requests within a specified time frame.

## Features

- **Idempotency Support:** Prevent duplicate records and unintended consequences in non-idempotent operations.
- **Distributed Caching:** Utilize a distributed caching mechanism (e.g., Hazelcast) for storing and checking idempotency information.
- **Spring Boot Integration:** Seamlessly integrate the library into your Spring Boot application using Spring Caching Abstraction.

## Getting Started

### Prerequisites

- Java 21 or higher
- Spring Boot 3.x
- Hazelcast or another compatible distributed caching solution

### Installation

Clone this repository and install the project into your local repository using the following command:

```bash
./mvnw clean install
```

Add the following dependency to your Spring Boot project:

```xml
<dependency>
    <groupId>codes.erbilcan.springboot.idempotency</groupId>
    <artifactId>spring-idempotency-filter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Usage

### 1. Enable caching in your Spring Boot Application:

Ensure that caching is enabled in your Spring Boot application.

```java
@SpringBootApplication
@EnableCaching
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

}
```

For example, you can use Hazelcast as a caching provider by creating a **hazelcast.yaml** file like the one below:

```yaml
hazelcast:
  map:
    idempotency:
      time-to-live-seconds: 120
```

Here we have created a map called __idempotency__, which we will also need to specify in the application properties.
The entries we put into this map will be stored for 2 minutes.

### 2. Enable configurations in your Spring Boot Application:

```java
@SpringBootApplication
@EnableCaching
@EnableIdempotency
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

}
```

The **@EnableIdempotency** annotation ensures that the IdempotencyFilter is created and registered as a bean in your Spring application context.

### 3. Define cache name and header in your application

Specify the cache name and header name in your application.properties or application.yml file.
Customize these values based on your preferences.

```yaml
# application.yaml
idempotency:
  cachename: idempotency
  header: X-Idempotency-Key
```

### 4. Use Idempotency Headers in Your Requests:

Include the **X-Idempotency-Key** header in your requests to enable idempotency checking.

```http
POST /your-endpoint
Content-Type: application/json
X-Idempotency-Key: abcdefg1234xyz
```

If a request with the same Idempotency-Key is received within the specified time frame, a 429 Too Many Requests response will be returned.

## Contribution 

I appreciate contributions! Feel free to contribute to the project by opening issues or submitting pull requests.

Thank you for considering contributing to this project!