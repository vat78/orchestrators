# Examples of orchestrating backend logic

This repository contains examples of implementation of the simple financial system.

## app-camunda8

This is an example application orchestrating backend logic using Camunda 8.

## app-temporal

This is an example application with orchestrating backend logic using Temporal.

## business-core

This is common module containing the business logic. This module is reused in example applications above

## Usage

To run the application, you need to have Java 21.
You can run tests in app modules that shows working with orchestrators.

```bash
gradlew clean :app-camunda8:test
```

## License

This project is licensed under the GPL-3.0 license - see the [LICENSE](LICENSE) file for details.