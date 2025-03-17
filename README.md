# WhatsApp Client

## Overview
WhatsApp Client is a Java-based library designed to facilitate communication with the WhatsApp API. It provides validation mechanisms and message-sending functionalities, ensuring that all interactions comply with WhatsApp's standards.

## Project Structure
````bash
├── src/  
│   ├── main/  
│   │   ├── java/com/wsclient/  
│   │   │   ├── cloud/api/  
│   │   │   │   ├── constants/        # Constant definitions
│   │   │   │   ├── messages/         # Message models
│   │   │   │   │   ├── request/      # Request structures
│   │   │   │   │   ├── response/     # WhatsApp responses
│   │   │   │   ├── services/         # Business logic and API calls
│   │   │   │   ├── validators/       # Data validation
│   │   │   │   ├── webhook/          # Handling incoming WhatsApp events
│   │   │   ├── common/utils/         # Utility functions
│   │   │   ├── core/exceptions/      # Exception handling
│   ├── test/                         # Unit tests
├── pom.xml                           # Maven project configuration
└── README.md                         # Project documentation
````

## Features
- Input validation for WhatsApp messages
- Interactive message validation
- Exception handling for incorrect inputs
- Easy integration with existing Java applications

## Requirements
- Java 21 or later
- Maven

## Installation
Clone the repository and build the project using Maven:

```sh
git https://github.com/Clihsman/wsclient.git
cd wsclient
mvn clean install
```

## Usage
### 1. Send Message

```java
 WhatsAppClient client = new WhatsAppClient();
 Text textMessage = new Text("Hello, this is a test message!");
 client.sendMessage("1234567890", textMessage);
```

## Building the JAR
To generate the JAR file, use the following command:

```sh
mvn package
```

The generated JAR can be found in the `target/` directory.

## Dependencies
This project uses the following dependencies:
- [Jackson Databind](https://github.com/FasterXML/jackson-databind)
- [Lombok](https://projectlombok.org/)
- [JUnit 5](https://junit.org/junit5/)
- [Mockito](https://site.mockito.org/)

## License
All rights reserved.

## Contributing
Feel free to open issues or submit pull requests for improvements.
