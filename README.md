# Tran Interpreter Run Instructions

This guide will help you set up and run the application after cloning the repository. The project consists of a Spring Boot backend which runs the interpreter and a React (Vite) frontend which serves as a code editor.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java JDK 17 or higher** - [Download here](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.6+** - [Download here](https://maven.apache.org/download.cgi)
- **Node.js 18+ and npm** - [Download here](https://nodejs.org/)
- **Git** - [Download here](https://git-scm.com/)

Verify installations by running:
```bash
java -version
mvn -version
node -version
npm -version
```

## Project Structure

```
project-root/
├── backend/          # Spring Boot application
│   ├── src/
│   ├── pom.xml
│   └── ...
├── frontend/         # React + Vite application
│   ├── src/
│   ├── package.json
│   └── ...
└── README.md
```

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd <project-directory>
```

### 2. Backend Setup (Spring Boot)

Navigate to the backend directory:

```bash
cd backend
```

Install dependencies and build the project:

```bash
mvn clean install
```

Run the Spring Boot application:

```bash
mvn spring-boot:run
```

The backend server will start on **http://localhost:8080** (or the port specified in `application.properties`).

**Alternative:** Run using the generated JAR file:
```bash
java -jar target/<application-name>.jar
```

### 3. Frontend Setup (React + Vite)

Open a new terminal and navigate to the frontend directory:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

The frontend will start on **http://localhost:5173** (default Vite port).

## Running Both Applications

You need to run both the backend and frontend simultaneously in separate terminal windows:

**Terminal 1 (Backend):**
```bash
cd backend
mvn spring-boot:run
```

**Terminal 2 (Frontend):**
```bash
cd frontend
npm run dev
```

## Building for Production

### Backend

Create a production-ready JAR file:

```bash
cd backend
mvn clean package
```

The JAR file will be in the `target/` directory.

### Frontend

Build the frontend for production:

```bash
cd frontend
npm run build
```
Run the frontend locally
```bash
npm run preview
```

Which you should then be able to access it on `http://localhost:4173/`

The production files will be in the `dist/` directory.

## Common Issues and Solutions

### Port Already in Use

If port 8081 or 5173 is already in use:

- **Backend:** Change the port in `backend/src/main/resources/application.properties`:
  ```properties
  server.port=8081
  ```

- **Frontend:** The Vite dev server will automatically try the next available port, or you can specify one in `vite.config.js` by adding this:
  ```javascript
  preview: {
    host: "0.0.0.0",
    port: 5173, // Change to desired port number
  },
  ```

### CORS Issues

If you encounter CORS errors, ensure your Spring Boot application has CORS configured properly with its mappings in the WebConfig.java file

```java
@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow all endpoints
                .allowedOrigins("https://traninterpreter.com", "http://localhost:5173") // Replace with your frontend's origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // HTTP methods allowed
                .allowedHeaders("*") // Allow all headers
                .exposedHeaders("Access-Control-Allow-Origin", "Cache-Control", "Content-Type") // Headers exposed to the client
                .allowCredentials(true); // Allow cookies or credentials

    }
```

After running the frontend and backend you should be able to write and execute code in the code editor provided and click run to see your output.

The backend takes requests to run code, spinning up a new thread for each request and sends output using SSE (Server sent events) which gives real time code execution and output.

