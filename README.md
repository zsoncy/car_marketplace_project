# Recipes Web Application

A full-stack car listing management application where users can create, store, and browse car advertisments. Built as a university project by  Kulcsár Gerzson.

---

## Purpose of the Application

The Car Marketplace App allows users to:

- Add and store their own car listings  
- Browse listings uploaded by others  
- View detailed descriptions and car specifications  
- Manage their uploaded content  
- Access admin-level management functions (for admin users)

---

# Backend (Java + Spring Boot)

## Tech Stack
- Java 21  
- Spring Boot  
- PostgreSQL  
- Spring Security + JWT  
- Checkstyle  
- Jacoco (83% test coverage)

---

## Models

### User
- Stores user details  
- A user can have multiple recipes (1-N relationship)

### Car
- Depends on User  
- Contains specifications and description

---

## Services

### Car Service
- Repository  
- JWT service  
- Model–DTO converter  

### User Service
- User-related business logic  

---

## Controllers

### Car Controller
- Handles car listing endpoints  
- Method-level authorization applied  

### User Controller
- Registration  
- Login  
- User operations  

---

## Security (JWT)

- Endpoints secured with role-based access  
- JWT token validation via custom filters  
- Token validity ~30 minutes  

---

# Testing

- Unit tests implemented  
- Test coverage via Jacoco  
- Total: 83%

---

# Frontend (React + TypeScript)

## Tech Stack
- React  
- TypeScript  
- Tailwind CSS  
- Zustand  
- TanStack Query  
- React-Toastify  

## Features
- Form validation on login and registration  
- Logout invalidates token  
- Dynamic welcome message showing logged-in user  

---

# Main Pages

## Homepage
- General information  
- Logout button invalidates token  

## My Cars
- Shows car listings uploaded by the logged-in user  
- Add, edit, delete listings  

## All Cars
- All cars from every user    
- Admins can edit or delete any recipe  
- Users can also add new recipes here  

## Admin Panel
- View all users  
- Delete users  
- Grant admin roles  
- Admin cannot delete themselves  
- Admin can edit or delete any recipe  

---

# Running the App (Docker)

```bash
docker-compose up --build
+
run backend: CarMarketplaceApp.java
+
```bash frontend:
npm run dev

