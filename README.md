# Getting Started

### Reference Documentation

Stablecoin Wallet & Payments API For on-ramp/off-ramp cross-border payment

A backend Web API for managing stablecoin wallets, on-ramp and off-ramp transactions, wallet-to-wallet transactions,
ledger accounting, fund reservations, and provider integrations (e.g. Circle), built with strong guarantees around consistency, idempotency, and concurrency safety.

This system is designed for fintech-grade reliability, ensuring balances are always correct even under concurrent requests and asynchronous webhooks.

Features

### Wallet Management

Create and manage user wallets

Accurate balance tracking via ledger projection

Support for multiple assets (e.g. USDC, MATIC, NGN)

Double-Entry Ledger System

Credit and debit entries for every financial operation

Immutable ledger records

Rebuild wallet balances from ledger at any time

### Transaction Management

On-ramp (fiat → stablecoin)

Off-ramp (stablecoin → fiat / blockchain transfer)

Transaction lifecycle tracking (INITIATED, PENDING, SUCCESS, FAILED)

### Reservation (Fund Locking)

Lock wallet funds before external transfers

Prevent double-spend and race conditions

Release or settle reservations based on outcome

### Concurrency & Locking

Row-level pessimistic locking on wallet rows

Safe handling of concurrent user actions and webhooks

Idempotent operations

### Provider Integration

External payment/blockchain providers (e.g. Circle)

Webhook handling for async status updates

Failure-safe reconciliation logic

Audit & Observability

Event-based audit logging

Webhook and provider response logging

Full transaction traceability

### Core Domain Models
User
Represents Users information and onboarding data

Wallet

Represents a user’s balance per asset.

Ledger

Immutable record of all financial movements.

Source of truth — wallet balances are projections from ledger entries.

Reservation

Locks funds during in-flight operations.

Transaction

Tracks business intent and external state.

### High-Level Flow

Off-Ramp Flow (Stablecoin → Fiat)

On-Ramp Flow (Fiat > stablecoin)

Stablecoin wallet-to-wallet(Inbound & outbound)

### Locking & Consistency Strategy

Row-level pessimistic locks on wallet rows during balance-critical operations

Reservation table prevents double-spend

Idempotency keys for webhook and external callbacks

Transactional boundaries ensure atomicity

### Money Handling

All monetary values stored using BigDecimal / DECIMAL

No floats or doubles

Explicit rounding strategy

Asset-aware precision

### Audit Logging(In the pipeline)

Every critical event is logged:

Wallet debits & credits

Reservation lifecycle changes

Transaction state changes

Webhook payloads

Provider API responses

Logs are structured and queryable for:

Compliance

Dispute resolution

Debugging

Reconciliation

### Tech Stack

Language: Java

Framework: Spring Boot

Database: PostgreSQL

ORM: JPA / Hibernate

Transactions: Spring @Transactional

Validation: Bean Validation / Custom Guards

Integrations: REST APIs (e.g. Circle)

### Reliability Guarantees

Exactly-2 ledger entries per Transaction

No negative balances

Safe under concurrent requests

Recoverable from partial failures

Rebuildable state from ledger

### Design Principles

Ledger is the source of truth

Side-effects only after state is safely recorded

Fail fast, recover safely

External systems are unreliable — design defensively