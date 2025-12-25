
## 💸 SplitEase – Smart Expense Sharing Application

SplitEase is a **Splitwise-like expense sharing web application** designed to simplify group expense management.
It helps users **track shared expenses, split bills fairly, and settle balances seamlessly**, with a focus on **backend robustness, cloud deployment, and scalability**.

> ⚠️ **Note:** SplitEase is an **independently built project**, not copied or forked.
> Some platforms may share similar names, but this application is **originally designed and implemented from scratch**.

---

## 🚀 Features

* 🔐 **Secure Authentication**

  * JWT-based authentication
  * Email verification with one-tap confirmation
* 👥 **User & Group Management**

  * Create groups
  * Add participants
* 💰 **Expense Management**

  * Add expenses
  * Automatic expense splitting
  * Track who owes whom
* 📊 **Balance Calculation**

  * Real-time balance settlement logic
* 📧 **Email Notifications**

  * Signup verification
  * Important user actions
* 🌐 **Fully Deployed & Live**

  * Frontend + backend hosted separately
* ⚙️ **Cloud-Ready Architecture**

---

## 🏗️ Tech Stack

### 🔹 Backend

* Java
* Spring Boot
* Spring Security (JWT)
* Hibernate / JPA
* PostgreSQL (Neon DB)

### 🔹 Frontend

* React
* Tailwind CSS
* Deployed on Vercel

### 🔹 Cloud & DevOps

* AWS EC2
* Docker
* Reverse Proxy (Caddy / Nginx)
* Custom Domain
* HTTPS enabled
* CI/CD (planned / in progress)
* Monitoring (Grafana & Prometheus – planned)

---

## 🧱 Architecture Overview

```
Frontend (Vercel)
   |
   | HTTPS API Requests
   ↓
Spring Boot Backend (AWS EC2 - Dockerized)
   |
   ↓
PostgreSQL Database (Neon Cloud)
```

This separation ensures:

* Scalability
* Security
* Independent deployments
* Better performance

---

## 📂 Project Structure (Backend)

```
src/
 ├── controller/
 ├── service/
 ├── repository/
 ├── model/
 ├── security/
 ├── config/
 ├── utils/
 └── SplitEaseApplication.java
```

---

## 🔐 Authentication Flow

1. User signs up
2. Verification email is sent
3. User verifies email with one tap
4. JWT token generated on login
5. Token used for secured API access

---

## 🌍 Deployment Details

### Frontend

* Hosted on **Vercel**
* Connected via custom domain

### Backend

* Hosted on **AWS EC2**
* Dockerized Spring Boot application
* Reverse proxy configured for HTTPS
* Environment variables used for secrets

---

## 🧪 API Testing

* APIs tested using Postman
* JWT-protected routes validated
* Email verification flow tested

---

## 📈 Scalability & Future Enhancements

* 🔄 CI/CD pipeline using GitHub Actions
* 📊 Monitoring with Grafana & Prometheus
* ☁️ Infrastructure as Code using Terraform
* 🤖 AI-assisted expense insights (future scope)
* 📱 Mobile-friendly enhancements

---

## 🎯 Why SplitEase?

SplitEase was built to:

* Solve a **real-world problem**
* Gain hands-on experience with **Spring Boot + Cloud + DevOps**
* Understand **production-grade backend deployment**
* Work with **real authentication & security**

---

## 👨‍💻 Author

**Atul Kumar Rana**
Backend Developer | DevOps Enthusiast | Cloud Learner

* GitHub:https://github.com/Atul-Kumar-Rana
* Project Repository:https://github.com/Atul-Kumar-Rana/splitease-v1
* Live Application: spliteaseapp.atul.codes

---

## ⭐ Acknowledgment

This project is built purely for **learning, experimentation, and skill development**, focusing on real deployment challenges rather than just local execution.

