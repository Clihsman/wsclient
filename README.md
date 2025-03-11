# WhatsApp Client

## Overview
WhatsApp Client is a Java-based library designed to facilitate communication with the WhatsApp API. It provides validation mechanisms and message-sending functionalities, ensuring that all interactions comply with WhatsApp's standards.

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
### 1. Validate a message input

```java
String recipient = "1234567890";
Text text = new Text("Hello, this is a test message.");
WhatsAppException exception = WhatsAppClientValidator.validateMessageInput(recipient, text);

if (exception != null) {
    throw exception;
}
```

### 2. Validate an interactive message

```java
String recipient = "1234567890";
Interactive interactiveMessage = new Interactive(InteractiveType.BUTTON, action);
WhatsAppException exception = WhatsAppClientValidator.validateInteractiveInput(recipient, interactiveMessage);

if (exception != null) {
    throw exception;
}
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
MIT License. See `LICENSE` file for details.

## Contributing
Feel free to open issues or submit pull requests for improvements.
