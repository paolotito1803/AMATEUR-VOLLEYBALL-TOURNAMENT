# Amateur Volleyball Tournament (Modular Monolith)

## Prerequisites
1. **Docker Desktop** (Running)
2. **Ports Available:** 8080 (Application & Frontend), 5433 (PostgreSQL Database)

## Installation & Setup
1.  **Clone the repository:**

2.  **Configure Environment:**
    No need to copy and populate the .env file, as we have committed it to this repository for demonstration purposes, so that the project runs out of the box.
    In a proper production environment, this file would be excluded from version control.

3.  **Run with Docker:**
    Execute the following command in the root directory:
       ```bash
       docker compose up -d --build
       ```

4.  **Wait a bit for the app to run and the seeding process to finish**

## Access
- Frontend Application: http://localhost:8080

Testing the Application:
The application automatically seeds the database with demo users and data on startup, and provides a quick login option to avoid having to write credentials every time.
