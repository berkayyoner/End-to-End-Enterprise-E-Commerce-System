# Berkay — Repository & Module Conventions

Reference document for task **0.1** in `ANALYSIS.md`. Defines how applications are named,
folder-organized, and how module/database boundaries are drawn between them, per the
"no generic backend/frontend folder names" and "each application in its own folder" rules
in `RULES.md`.

## 1. Top-level folder = one deployable application

Every folder at repo root is exactly one independently deployable, independently versioned
application. Nothing generic (`backend/`, `frontend/`, `common/`, `shared/`) lives at root.

| Folder              | Type              | Responsibility                                                                 |
|---------------------|-------------------|---------------------------------------------------------------------------------|
| `api`               | Spring Boot (gateway) | Single public entry point (Spring Cloud Gateway): routing, CORS enforcement, request-level rate limiting. Never holds business/domain logic. |
| `discovery-service` | Spring Boot (infra, Phase 0.8+) | Eureka service registry. Every other Spring Boot service registers here and discovers peers by name instead of hardcoded host:port. |
| `config-server`     | Spring Boot (infra, Phase 0.8+) | Spring Cloud Config Server (native/classpath-backed `config-repo`). Supplies centrally-managed property overrides layered on top of each service's own `application-*.yml`; never the only source of a property a service needs to boot. |
| `log-service`       | Spring Boot (infra, Phase 0.9+) | Centralized, immutable user/personnel activity log store (RULES.md's "Log every user and personnel activity"). Every other service POSTs activity events to it over REST instead of writing its own log table — the one deliberate exception to per-service table ownership below. |
| `auth-service`      | Spring Boot       | Identity: users, personnel, permission groups, OAuth2 authorization server, ID verification, seller applications, bans. |
| `product-service`   | Spring Boot       | Catalog: products, categories, Elasticsearch indexing/search, Q&A, ratings/reviews. |
| `order-service`     | Spring Boot (Phase 5) | Basket, orders, dummy payment/card vault. |
| `campaign-service`  | Spring Boot (Phase 6) | Campaigns, discount coupons. |
| `berkay-public`     | React (Vite)      | Public storefront: customers, sellers, anonymous visitors. |
| `berkay-personnel`  | React (Vite)      | Internal admin/moderator panel. Never bundled with or routed through `berkay-public`. |
| `common-lib`        | Java library (Phase 0.4+) | Cross-cutting Java code shared by Spring Boot services only (base auditable/soft-delete entity, translation base classes, standard error response DTOs). Published to the local Maven repo and declared as a normal `<dependency>` — never a source copy-paste. Contains no business logic, no controllers, no service-specific entities. |
| `example-bases`     | Reference only    | Not shipped. Source patterns (locales/hooks/components/utils) that `berkay-public` and `berkay-personnel` adapt into their own `src/i18n` module. |

Future microservices follow the `<domain>-service` naming pattern (e.g. `order-service`,
`campaign-service`, `notification-service`). Future front-ends stay prefixed `berkay-<audience>`.
No new service is ever named `api2`, `backend`, `core`, or `common` — names must describe the
business domain they own.

## 2. Per-service internal package structure (Spring Boot)

Each service's `src/main/java/com/berkay/<service_name>/` is organized by technical layer,
grouped under a domain package per bounded context it owns:

```
com.berkay.<service_name>/
  config/          # Spring configuration (security, CORS, OpenAPI, Redis, Elasticsearch, etc.)
  <domain>/
    entity/        # JPA entities, extend common-lib's AuditableEntity
    dto/           # request/response payloads — never expose entities directly
    repository/    # Spring Data repositories
    service/       # interfaces + impl, business logic, SOLID single-responsibility
    controller/     # REST controllers, thin — delegate to service layer
    mapper/        # entity <-> DTO mapping
  exception/       # service-specific exception types + @ControllerAdvice handler
```

Multiple `<domain>` packages are expected inside one service only when the domains are too
tightly coupled to split into separate services yet remain independently testable modules
inside it (e.g. `product-service` will hold both `catalog` and `search` domain packages).

## 3. Database & module boundaries

* Every service connects to the same Oracle instance/service name (`XEPDB1`, user `berkay`,
  port 1521) for local/dev, per `RULES.md` — but each service owns an exclusive set of tables
  and is the only writer/reader of them. Cross-service data needs go through that service's
  REST API (via the `api` gateway or direct service-to-service call), never a shared table or
  cross-schema JOIN.
* Table ownership by service:
  * `log-service`: `activity_log` (written via `POST /logs` by every other service; the only
    table any service other than its owner effectively "writes to", and only through the
    owner's own REST API, never directly).
  * `auth-service`: `app_user`, `personnel`, `permission_group`, `id_verification`,
    `seller_application`, `banned_user`.
  * `product-service`: `product`, `product_translation`, `category` (+ translations),
    `category_change_request`, `review`, `qna`.
  * `order-service` (Phase 5): `basket`, `basket_item`, `order`, `order_item`, `saved_card`.
  * `campaign-service` (Phase 6): `campaign`, `coupon`.
* All tables include soft-delete (`is_deleted`, `deleted_at`) and audit (`created_at`,
  `updated_at`, `created_by`, `updated_by`) columns via the shared `common-lib` base entity —
  see task 0.4. No table ever performs a hard `DELETE`.
* Environments: each service ships `application-local.yml`, `application-development.yml`,
  `application-production.yml` (task 0.3); `local` uses `localhost` values for Oracle/Redis/
  Elasticsearch, matching the `RULES.md` requirement that development environments default to
  localhost.

## 4. Front-end module boundaries

* `berkay-public` and `berkay-personnel` are fully separate Vite apps with independent
  `package.json`, build pipelines, and deployments — never a shared monorepo package unless a
  future `common-lib`-equivalent (e.g. `ui-kit`) is introduced explicitly, named for what it
  contains, not "shared" or "frontend-common".
* Both apps consume the backend exclusively through the `api` gateway's public base URL — no
  front-end talks to `auth-service`/`product-service`/etc. directly.

## 5. Rationale

This structure keeps every folder self-describing (task boundary is obvious from the name),
keeps the database boundary aligned with the service boundary even while sharing one physical
Oracle instance (a deliberate compromise for local/dev simplicity per `RULES.md`'s Oracle
connection info, without violating microservice data ownership), and gives the personnel and
public applications hard separation for the CORS/security rules `RULES.md` requires.
