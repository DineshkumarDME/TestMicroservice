# Spring Boot OTP + JWT Validation Demo

## Description

This project demonstrates a simple OTP (One-Time Password) generation and validation mechanism implemented using Spring Boot. The OTP validation is coupled with JWT (JSON Web Token) validation, which is handled by Keycloak.

Key features:
*   In-memory storage for OTPs (suitable for demo purposes).
*   A mock SMS service that prints the OTP to the console instead of sending an actual SMS.
*   Integration with Keycloak for JWT validation on protected endpoints.

## Prerequisites

*   Java JDK 11 or newer
*   Apache Maven
*   A running Keycloak instance

## Setup & Running

1.  **Clone or Download the Project:**
    ```bash
    git clone <repository_url>
    cd <project_directory>
    ```

2.  **Configure Keycloak Integration:**
    This is a **crucial step**. Open the `src/main/resources/application.properties` file and update the following Keycloak properties with your actual Keycloak server details:

    *   `keycloak.realm`: The name of your Keycloak realm.
    *   `keycloak.auth-server-url`: The base URL of your Keycloak authentication server (e.g., `http://localhost:8080/auth` or `http://localhost:8080` if your Keycloak version is newer and doesn't use `/auth` by default).
    *   `keycloak.resource`: The Client ID of the Keycloak client you have configured for this application.
    *   `keycloak.public-client`: Set to `true` if your Keycloak client is public. If it's a confidential client, set this to `false` (or remove it) and add `keycloak.credentials.secret=your-client-secret` with the actual client secret.

    Example:
    ```properties
    keycloak.realm=my-spring-realm
    keycloak.auth-server-url=http://localhost:8181/auth
    keycloak.resource=my-otp-app-client
    keycloak.public-client=true
    # For confidential clients:
    # keycloak.credentials.secret=your_client_secret_here
    ```
    The default application port is `8080`. If you wish to change it, uncomment and modify `server.port=8090` in the same `application.properties` file.

3.  **Build and Run the Application:**
    Use Maven to build and run the Spring Boot application:
    ```bash
    mvn spring-boot:run
    ```
    The application will start, and you should see log output in your console.

## API Endpoints

The default base URL is `http://localhost:8080`. Adjust the port if you've changed `server.port`.

### 1. Request OTP

*   **Endpoint:** `POST /otp/request`
*   **Description:** Generates an OTP for the given mobile number and "sends" it via the mock SMS service (i.e., prints to the console). This endpoint is not protected by Keycloak.
*   **Request Body:**
    ```json
    {
        "mobileNumber": "your_mobile_number"
    }
    ```
*   **Success Response:**
    *   Code: `200 OK`
    *   Body: `"OTP sent successfully (check console)."`
*   **Example `curl` command:**
    ```bash
    curl -X POST \
      -H "Content-Type: application/json" \
      -d '{"mobileNumber":"1234567890"}' \
      http://localhost:8080/otp/request
    ```

### 2. Validate OTP and JWT

*   **Endpoint:** `POST /otp/validate`
*   **Description:** Validates the provided OTP for the given mobile number. This endpoint **requires a valid JWT** from Keycloak in the `Authorization` header.
*   **Request Header:**
    *   `Authorization: Bearer <your_keycloak_jwt>`
*   **Request Body:**
    ```json
    {
        "mobileNumber": "your_mobile_number",
        "otp": "received_otp_from_console"
    }
    ```
*   **Success Response:**
    *   Code: `200 OK`
    *   Body: `"OTP is valid. JWT was also validated by Keycloak/Spring Security."`
*   **Error Responses:**
    *   Code: `401 Unauthorized` (If OTP is invalid, expired, or the JWT is missing/invalid/expired).
    *   Body: `"Invalid or expired OTP."` (or Keycloak's error for JWT issues).
*   **Example `curl` command:**
    ```bash
    curl -X POST \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer YOUR_KEYCLOAK_JWT_HERE" \
      -d '{"mobileNumber":"1234567890", "otp":"123456"}' \
      http://localhost:8080/otp/validate
    ```
    Replace `YOUR_KEYCLOAK_JWT_HERE` with an actual JWT obtained from your Keycloak server for a user in the configured realm and client.

## Keycloak Configuration Notes

*   This application expects a client (identified by `keycloak.resource` in `application.properties`) to be configured within your Keycloak realm.
*   The `/otp/validate` endpoint is secured by Keycloak. Ensure your Keycloak client configuration allows users to authenticate and obtain tokens.
*   The example uses `keycloak.public-client=true` for simplicity. In production environments, it's generally recommended to use confidential clients (`keycloak.public-client=false` and providing `keycloak.credentials.secret`). Ensure your Keycloak client type matches this configuration.
