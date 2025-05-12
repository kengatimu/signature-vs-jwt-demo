# signature-vs-jwt-demo

This project demonstrates two approaches for securing payloads exchanged between microservices:

1. **Digital Signature** – Asymmetric cryptography using X.509 certificates
2. **JWT (JSON Web Token)** – Symmetric key signing using HMAC (HS256)

It includes three Spring Boot services:

- `channel-service`: Sends secure transaction requests
- `signature-middleware-service`: Validates payloads using digital signature
- `jwt-middleware-service`: Validates payloads using JWT

---

## Summary

In modern middleware architectures, ensuring that requests are **authentic**, **tamper-proof**, and **verifiable** is critical. This demo compares two widely-used mechanisms and highlights the best choice for **high-throughput transactional systems**.

### Digital Signature
- Ensures **authenticity, integrity, and non-repudiation**
- Stateless, scalable, and ideal for secure backend middleware

### JWT (HS256)
- Lightweight and stateless
- Requires shared secret, expiry management
- Lacks non-repudiation and can introduce token complexity
 
- This project demonstrates that **digital signature validation is the preferred approach** for backend middleware. **JWT is suitable as a fallback**, but combining both adds unnecessary complexity and offers no security advantage.

---

## Technologies Used

- Java 21
- Spring Boot 3
- Maven
- Apache HttpClient (with mTLS)
- JJWT 0.11.x
- PKCS12 keystores/truststores for certificate handling

---

##️ Service Overview

### 1. `channel-service`
- Generates and sends transaction requests
- Supports:
  - Digital signatures via `.p12` keystore
  - JWT generation via shared HMAC secret
- Communicates via HTTPS with mutual TLS

### 2. `signature-middleware-service`
- Verifies signatures using trusted X.509 certificates
- Retrieves public certificate by `channelId`
- Performs canonical string generation and signature validation

### 3. `jwt-middleware-service`
- Verifies JWT tokens via shared HMAC secret
- Parses claims, validates expiry and payload consistency

---

## Sample JSON Requests

### ➤ To `channel-service` (JWT or Signature)

```json
{
  "rrn": "TX99887700",
  "senderName": "John Doe",
  "receiverName": "Jane Smith",
  "amount": 1500.00,
  "feeAmount": "0.00",
  "currency": "KES",
  "narration": "Family support"
}

```
---
# How to run project locally
- git clone https://github.com/kengatimu/signature-vs-jwt-demo.git
- cd signature-vs-jwt-demo
- mvn clean install
- mvn spring-boot:run

---

# Related Links
- Articles by Ken Gatimu: https://medium.com/@kengatimu
- Linkedin: https://www.linkedin.com/in/kengatimu/

---
