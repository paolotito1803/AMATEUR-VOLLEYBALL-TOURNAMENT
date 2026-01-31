# Amateur Volleyball Tournament (Modular Monolith)

## Prerequisites
1. **Docker Desktop** (Running)
2. **Java 21 JDK** (Required to compile the backend)
3. **Ports Available:** 8080 (FrontEnd), 5432 (DB)

## Configuration (First Run)
This project uses a `.env` file to manage secrets (Database credentials, JWT keys) securely.
The `.env.example` file in the root directory shows the structure and secrets that should be present in the .env file to add in the root folder.
**Note:** The project will not start if the `.env` file is missing.

## How to Run
1. Click the `run button` in IntelIJ IDEA
2. Open the browser and search the web page http://localhost:8080

## Access
- Frontend: http://localhost:8080

## Development vs. Production Build
Currently, this project uses a Modular Build approach for development speed:
1. Java compiles the machine (Requires JDK 21).
2. Docker packages the pre-built JARs.
