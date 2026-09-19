# Kadal Vazhi (கடல் வழி) — Microservices Architecture & V1 Blueprint

An enterprise-grade, low-latency, polyglot microservices platform tailored for deep-sea fishing communities, fleet owners, crew exchange, and direct-to-consumer maritime commerce.

---

## 1. System Architecture Overview

```mermaid
flowchart TD
    subgraph Clients["Clients (Mobile App & Web App)"]
        Mobile["Flutter / React Native Mobile"]
        Web["Next.js Web Portal (Admin & Fleet Dashboard)"]
    end

    subgraph Edge["Edge / API Gateway"]
        Gateway["API Gateway (Spring Cloud Gateway / Envoy)\n- Rate Limiting (Token Bucket)\n- JWT Auth Verification\n- mTLS & SSL Termination\n- i18n Header Routing ('Accept-Language')"]
    end

    subgraph CoreJava["Core Business Services (Java 25 / Spring Boot)"]
        UserService["User & Social Service\n(Profile, Follows, Port Location)"]
        FleetService["Fleet & Voyage Service\n(10-Day Trips, Diesel, Food, P&L, Fleet Leaderboard)"]
        MarketService["Marketplace & Pre-Order Service\n(At-Sea Catch Pre-booking, Zero-Middlemen)"]
        CrewService["Crew Exchange Service\n(Emergency Manpower, Job Availability)"]
    end

    subgraph PythonAI["AI & Voice Engine (Python / FastAPI)"]
        VoiceAgent["AI Voice & Advisory Service\n- Whisper/Faster-Whisper (Tamil/Malayalam STT)\n- Llama/Gemini Agent (Fisheries Knowledge)\n- TTS Engine"]
    end

    subgraph EventStream["Distributed Event Backbone"]
        Kafka["Apache Kafka\n- trip-events (Start, End, Log)\n- catch-reported-events\n- crew-emergency-events"]
    end

    subgraph Persistence["Polyglot Data Tier"]
        Postgres[("PostgreSQL\n(Relational ACID, JSONB for i18n)")]
        Redis[("Redis Cluster\n(Leaderboard ZSETs, Token Blacklist, Cache-Aside)")]
        VectorDB[("Vector DB (pgvector / Qdrant)\n(Fish Species, Voice Embeddings)")]
        S3[("Object Storage (S3 / MinIO)\n(Catch Photos, Trip Receipts, Voice Notes)")]
    end

    Clients -->|HTTPS / WSS / REST / GraphQL| Gateway
    Gateway -->|Internal REST / gRPC| UserService
    Gateway -->|Internal REST / gRPC| FleetService
    Gateway -->|Internal REST / gRPC| MarketService
    Gateway -->|Internal REST / gRPC| CrewService
    Gateway -->|REST / gRPC| VoiceAgent

    FleetService -->|Publish Events| Kafka
    MarketService -->|Publish Events| Kafka
    CrewService -->|Publish Events| Kafka

    Kafka -->|Consume Events| FleetService
    Kafka -->|Consume Events| MarketService
    Kafka -->|Consume Events| VoiceAgent

    UserService --> Postgres
    FleetService --> Postgres
    MarketService --> Postgres
    CrewService --> Postgres

    UserService --> Redis
    FleetService --> Redis
    VoiceAgent --> VectorDB
    UserService --> S3
```

---

### 2. Vessel Classification: All Boat Types Supported

Kadal Vazhi serves the entire spectrum of maritime vessels, not just large trawlers:

| Vessel Category | Tamil Common Name | Trip Duration | Crew Size | Engine / Fuel Type | Unique Needs |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Traditional / Country Craft** | நாட்டுப் படகு / கட்டுமரம் | 4 – 12 hours | 1 – 3 | Manual oars or small OBM (Kerosene/Petrol) | Quick daily catch, immediate shore landing. |
| **Motorized Fiber Boat (FRP)** | ஃபைபர் படகு | 1 day (Early morning to evening) | 2 – 5 | Outboard Motor (OBM) — Petrol/Kerosene | Daily fuel tracking, ice box catch preservation. |
| **Mechanized Gillnetter / Trawler** | விசைப்படகு (ட்வாலிங்) | 3 – 15 days | 8 – 15 | Inboard Diesel Engine (Heavy Diesel) | Bulk diesel, ration supplies, cold storage, multi-tier crew roster. |
| **Deep-Sea Longliner / Tuna Craft** | ஆழ்கடல் தூண்டில் படகு | 15 – 30 days | 10 – 18 | Heavy Diesel + Generator | Tuna export grade tracking, blast freezing, satellite communication. |

---

## 3. Critical Real-World Maritime Needs You Must Not Miss

1. **Offline-First Data Sync (Zero Network at Deep Sea)**:
   - *The Reality*: 5 to 10 nautical miles shore-la irundhu thalli pona **mobile signal ZERO**.
   - *Architecture*: Mobile app-la local SQLite / Room DB. Captain sea-la daily diesel, catch, food expenses record pannuvanga. Boat shore-ku varum podhu 4G signal kedaicha odane backend-kku **Idempotent Background Sync** aagum.
2. **Fuel Quota & Subsidized Fuel Tracking**:
   - Indian coastal states (Tamil Nadu, Kerala) grant subsidized diesel & kerosene for registered fishermen.
   - Kadal Vazhi should track monthly government quota balance.
3. **Harbor & Fish Landing Center (FLC) Registry**:
   - Catch pre-order valid aaga irukanumna, customer-kku boat entha harbor-la (e.g. Kasimedu, Tuticorin, Rameswaram, Munambam) ethana manikku (ETA) land aagum nu accurate-ah theriya vendum.
4. **Weather, Cyclone & Marine Safety Alerts (IMD / INCOIS Integration)**:
   - High wave alerts, cyclone warnings, ocean current data, and Potential Fishing Zones (PFZ).
   - Instant SMS / Push notification when coast guard flags bad weather.
5. **Government Annual Fishing Ban Period (மீன்பிடி தடைக்காலம்)**:
   - 61-day annual ban compliance check (East Coast: April 15 - June 14; West Coast: June 1 - July 31).

---

## 4. Recommended Microservices & Repository Plan

| Repository Name | Tech Stack | Primary Responsibility |
| :--- | :--- | :--- |
| **`nn-home-service`** *(Current)* | Java 25, Spring Boot | **Home Aggregator & User Social Service**: User profiles, roles, boat follow relations, home feed aggregator. |
| **`kadalvazhi-fleet-service`** | Java 25, Spring Boot | **Vessel & Voyage Engine**: 1-day & 15-day trip lifecycle, fuel ledger (Diesel/Petrol/Kerosene), ice & supplies, crew roster, P&L analytics, Redis leaderboards. |
| **`kadalvazhi-crew-exchange-service`** | Java 25, Spring Boot | **Emergency Crew Exchange**: Quick hiring board ("3 crew urgently needed at Port X"), real-time worker availability. |
| **`kadalvazhi-marketplace-service`** | Java 25, Spring Boot | **Direct Catch Pre-order**: At-sea catch declaration, dockside pre-booking, eliminating exploitative middlemen. |
| **`kadalvazhi-gateway`** | Spring Cloud Gateway | Auth JWT verification, rate limiting, SSL, routing. |
| **`kadalvazhi-ai-service`** | Python 3.12, FastAPI | AI Voice assistant (Tamil/Malayalam STT/TTS), seasonal fish advisory, smart crew matching. |
| **`kadalvazhi-weather-service`** | Java or Python | INCOIS/IMD weather scraping, ocean alerts, harbor advisories. |
| **`kadalvazhi-common-lib`** | Java (Shared library) | Shared DTOs, Kafka event contracts, security filters, error handlers. |
| **`kadalvazhi-frontend`** | Flutter (Mobile) + Next.js (Web) | Offline-enabled mobile app + fleet management web dashboard. |

---

## 5. AWS Cloud Architecture & Infrastructure

```mermaid
flowchart TD
    subgraph AWSCloud["AWS Production Cloud (ap-south-1 / Mumbai)"]
        subgraph EdgeLayer["Edge & Security"]
            Route53["Amazon Route 53 (DNS)"]
            CloudFront["Amazon CloudFront (CDN)"]
            WAF["AWS WAF (DDoS / OWASP Protection)"]
            ALB["Application Load Balancer (ALB)"]
        end

        subgraph Compute["EKS Kubernetes Cluster"]
            Ingress["Nginx / AWS Load Balancer Controller"]
            PodGW["kadalvazhi-gateway pods"]
            PodHome["nn-home-service pods"]
            PodFleet["fleet-service pods"]
            PodMarket["marketplace-service pods"]
            PodAI["ai-service pods (Python)"]
        end

        subgraph ManagedData["AWS Managed Data Tier"]
            RDS[("Amazon RDS PostgreSQL (Multi-AZ)")]
            ElastiCache[("Amazon ElastiCache Redis (Cluster Mode)")]
            MSK[("Amazon MSK (Managed Apache Kafka)")]
            S3[("Amazon S3 (Catch Photos, Voice, Backups)")]
            Secrets["AWS Secrets Manager & KMS"]
        end
    end

    Route53 --> CloudFront
    CloudFront --> WAF
    WAF --> ALB
    ALB --> Ingress
    Ingress --> PodGW
    PodGW --> PodHome & PodFleet & PodMarket & PodAI

    PodFleet --> MSK
    PodMarket --> MSK
    PodHome & PodFleet & PodMarket --> RDS
    PodFleet & PodMarket --> ElastiCache
    PodHome --> S3
    Compute -.-> Secrets
```

---

## 6. Jenkins CI/CD Pipeline Flow

```mermaid
flowchart LR
    Dev["Git Push (Feature Branch)"] --> Webhook["GitHub Webhook"]
    Webhook --> Jenkins["Jenkins CI Master"]
    
    subgraph Pipeline["Jenkins Automated Pipeline"]
        Checkout["1. Git Checkout"] --> Lint["2. Compile & Unit Tests (JUnit 5)"]
        Lint --> Sonar["3. SonarQube (Quality & Security Scan)"]
        Sonar --> DockerBuild["4. Docker Multi-Stage Build"]
        DockerBuild --> ECR["5. Push Image to AWS ECR"]
        ECR --> HelmDeploy["6. Helm / Kubectl Deploy to AWS EKS"]
        HelmDeploy --> SmokeTest["7. Healthcheck & Canary Rollout"]
    end

    Jenkins --> Pipeline
```

---

## 7. Communication Protocols & i18n Strategy

1. **REST (OpenAPI 3.0)**: Mobile/Web $\to$ Gateway $\to$ Microservices for standard CRUD.
2. **GraphQL**: Fleet Analytics Dashboard for complex aggregated queries (12 boats + fuel + P&L in one roundtrip).
3. **gRPC (HTTP/2 Protobuf)**: Low-latency synchronous service-to-service communication.
4. **Apache Kafka (Amazon MSK)**: Asynchronous event streaming (`TripEndedEvent`, `CatchDeclaredEvent`, `UrgentCrewNeededEvent`).
5. **i18n**: 
   - Static UI strings: Client local JSON files (`en.json`, `ta.json`, `ml.json`) $\to$ zero network latency.
   - Master data (Fish, ports): PostgreSQL `JSONB` with GIN indexing.
   - Dynamic user posts: Native language storage with on-demand AI translation.

---

## 8. Version Scope Boundary

### Version 1 (V1)
- Auth (Phone OTP + JWT) & Role-Based Access (Fisherman, Captain, Fleet Owner, Crew, Buyer, Admin).
- User Profiles, Follow system, and Home Feed Aggregator (`nn-home-service`).
- All-Boat Voyage & Fuel Expense Tracker (1-day country/fiber boat to 15-day trawler; Diesel/Petrol/Kerosene).
- Fleet Dashboard & P&L Analysis (Profit vs Loss comparison across 12+ boats).
- Emergency Crew Replacement Board (urgent hiring for missing deckhands/drivers).
- Direct-From-Sea Pre-Order Marketplace (Harbor arrival ETA + catch booking).
- Multi-Language (English, Tamil, Malayalam).
- Dockerized microservices on AWS EKS with Jenkins CI/CD automation.

### Version 2 (V2)
- Fishing Reels & Video Streaming Pipeline (HLS/DASH + CloudFront).
- Real-Time Duplex Voice AI Agent via WebRTC.
- Automated Live Harbor Auction with WebSocket bidding.
- Satellite / IoT GPS Vessel Transponder Integration.
