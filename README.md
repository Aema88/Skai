Polynomial API
Description

This project is a RESTful API for simplifying and evaluating polynomials using Spring Boot and PostgreSQL.
Installation

    Clone the repository:
    git clone https://github.com/Aema88/Skai
    Navigate to the project directory:
    cd your_repository
    Install dependencies:
    ./gradlew build
    Run the application:
    ./gradlew bootRun

Usage
Endpoints

    POST /api/polynomial/simplify
        Parameters: expression (string) - the polynomial to simplify.
        Example request:
        curl -X POST -H "Content-Type: application/x-www-form-urlencoded" -d "expression=3*x+x" http://localhost:8080/api/polynomial/simplify

    GET /api/polynomial/evaluate
        Parameters: expression (string) - the polynomial to evaluate, x (number) - the value to substitute.

Testing

To run the tests, execute the command:
./gradlew test