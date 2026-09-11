# Smart Agricultural Procurement & Queue Management Platform (SIH_HACKATHON)

> **Smart India Hackathon (SIH) Project**  
> An end-to-end intelligent procurement, real-time queue balancing, DBT payment tracking, dynamic route estimation, and district analytics system for Indian agricultural mandis.

---

## 🌾 Key Features & Modules

- **Unified Authentication & RBAC**: Single login system for Farmers, Mandi Owners, and District Admins with automatic role identification and route guarding.
- **Smart Centre Recommendation Engine**: Real-time load balancing that detects overloaded procurement centres and recommends optimal alternatives based on distance and wait times.
- **Digital Token & Live Queue Tracker**: Real-time token generation, queue position tracking, and dynamic departure advisories based on traffic conditions.
- **4-Step Mandi Owner Stepper**: Streamlined token verification, quality inspection & weighment, gate pass issuance, and direct DBT payment triggering.
- **DBT Payment Tracker**: Real-time payment status tracking for farmers with direct bank credit validation.
- **District Admin Console**: Comprehensive district-wide analytics, emergency mandi closure management, load balancing triggers, and AI demand predictions.
- **Multilingual Support**: Instant switching between **Telugu (తెలుగు)**, **English**, and **Hindi (हिन्दी)**.

---

## 🚀 Quick Start Guide

### Prerequisites
- **Java 17+**
- **Maven 3.8+**
- **Node.js 18+ & npm**

### 1. Run Backend (Spring Boot)
```bash
# In project root
mvn spring-boot:run
```
- REST API running at: `http://localhost:8080`
- H2 Console at: `http://localhost:8080/h2-console`

### 2. Run Frontend (React + Vite)
```bash
# In frontend directory
cd frontend
npm install
npm run dev
```
- Web Application running at: `http://localhost:5173`

---

## 🔑 Demo Access Credentials

Log in via the **Common Login Page** at `http://localhost:5173/login`:

| Role | Mobile Number | Password | Target Dashboard |
| :--- | :--- | :--- | :--- |
| **Farmer** | `9876543210` | `123456` | Farmer Portal (`/`) |
| **Mandi Owner** | `9876543211` | `123456` | Owner Dashboard (`/owner/dashboard`) |
| **District Admin** | `9999999999` | `admin123` | District Admin Console (`/admin/dashboard`) |

---

## 🏗️ Technology Stack

- **Backend**: Spring Boot 3.3, Spring Security, Spring Data JPA, H2/MySQL Database, Netty Socket.IO, JWT.
- **Frontend**: React 18, Vite, React Router, TailwindCSS, Lucide Icons, Axios.

---

## 📝 License
Developed for Smart India Hackathon (SIH). All rights reserved.
# Smart_Procurement_center
