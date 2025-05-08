# signature-vs-jwt-demo

This project demonstrates two approaches for securing payloads exchanged between microservices:

1. **Digital Signature** (asymmetric cryptography using certificates)
2. **JWT (JSON Web Token)** using **HMAC (HS256)** (symmetric shared secret)

It includes three Spring Boot services:

- `channel-service`: Sends secure transaction requests
- `signature-middleware-service`: Validates payloads using digital signature
- `jwt-middleware-service`: Validates payloads using JWT

---

## Summary

In modern middleware architectures, ensuring that incoming requests are authentic, tamper-proof, and verifiable is critical. This project evaluates **two widely used mechanisms** for achieving that goal and outlines which is best suited for **high-throughput** environments.

- **Digital Signature** offers strong guarantees for authenticity, integrity, and non-repudiation without needing token issuance or state tracking. It's ideal for middleware systems that process sensitive transactional data.
  
- **JWT with HMAC (HS256)**, while stateless and convenient, requires shared secrets, introduces token parsing and expiry management, and lacks non-repudiation. It's best used for simple, user-facing or authentication scenarios.

This demo proves that **digital signature validation is the preferred approach** for backend middleware, with **JWT as a fallback** if signature integration is not feasible. Using **both (JWT over signature)** is considered bad design—offering no additional security and increasing complexity.

---

## Technologies Used

- Java 21
- Spring Boot 3
- Maven
- Apache HttpClient (with mTLS)
- JJWT 0.11.x (for JWT)
- PKCS12 keystores and truststores for certificate handling

---

## Service Overview

### 1. channel-service
- Generates and sends requests to the middleware.
- Supports:
  - Digital signatures via `.p12` keystore
  - JWT generation using a shared HMAC secret
- Communicates over HTTPS using mutual TLS

### 2. signature-middleware-service
- Verifies digital signatures using a trusted certificate.
- Extracts public keys from a truststore based on the channel identifier.
- Performs signature verification on a canonical "clear text" string.

### 3. jwt-middleware-service
- Verifies JWT tokens using a shared HMAC secret.
- Parses token claims and validates them against the request payload.
- Checks token expiry and ensures token integrity.

---

## Recommendation

Use the right tool for the job:

- For middleware services that route, verify, and respond to transactional requests:
  - **Prefer digital signatures.**
  - They are stateless, strong, and scalable.

- Only use **JWT**:
  - When digital signatures are not feasible
  - For systems where the channel cannot handle certificates
  - For lighter or user-based interactions

- **Do not combine JWT and digital signatures.**
  - Adding JWT inside a signed payload or vice versa introduces unnecessary redundancy and complexity.

---

## Future Extension

Currently, only **HMAC (HS256)** JWT implementation is included. RS256 (asymmetric JWT) can be implemented for comparison, though it offers fewer benefits in middleware use cases when digital signatures are already available.

---

## Running the Project

Each service can be started independently using Maven:

```bash
mvn spring-boot:run
