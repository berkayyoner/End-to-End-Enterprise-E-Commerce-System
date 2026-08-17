# Berkay — Project Roadmap & Phase Analysis

This document is the single source of truth for project phases. It is derived from `RULES.md`.
Each phase is broken into discrete, independently-completable tasks. When a task is completed,
append a one-line summary to `DONE.md` before starting the next task. Do not skip ahead — always
complete tasks within a phase in order unless a task has no dependency on earlier ones.

Existing scaffolding found in repo root: `api/` (Spring Boot base), `auth-service/` (Spring Boot base),
`product-service/` (Spring Boot base), `berkay-public/` (React/Vite base), `berkay-personnel/` (React/Vite base),
`example-bases/` (multi-language i18n reference: components, hooks, locales, utils).

---

## Phase 0 — Foundation & Shared Infrastructure
Goal: establish the cross-cutting infrastructure every microservice and front-end depends on.

0.1. Define repository/folder conventions per service (no generic "backend"/"frontend" names) and document module boundaries.
0.2. Set up Docker Compose for local orchestration: Oracle DB (XEPDB1, user `berkay`, port 1521), Redis, Elasticsearch, and service containers.
0.3. Create shared environment configuration strategy: `application-local.yml`, `application-development.yml`, `application-production.yml` per service using localhost values for local/dev.
0.4. Establish shared database conventions: soft-delete columns (`is_deleted`, `deleted_at`), audit columns (`created_at`, `updated_at`, `created_by`, `updated_by`) applied via a common JPA `AuditingEntityListener` base entity.
0.5. Design multi-language (i18n) strategy for backend: `translations` support tables (e.g. `product_translation`, `category_translation`) keyed by locale code, default locale Turkish (`tr`), fallback logic, and adapt patterns from `example-bases/locales` for a scalable pattern.
0.6. Design multi-language strategy for frontend: adapt `example-bases/hooks` + `example-bases/locales` into a shared i18n module usable by both `berkay-public` and `berkay-personnel`, default language Turkish, easy addition of new languages.
0.7. Set up API Gateway service (Spring Cloud Gateway) for routing to all microservices, CORS rules (public open, personnel restricted to localhost/specified IPs).
0.8. Set up Service Discovery (Eureka or equivalent) and centralized config server for Spring Boot services.
0.9. Set up centralized logging infrastructure (user activity logs + personnel activity logs) with a shared `Log` schema/service.
0.10. Define OAuth 2.0 authentication/authorization architecture (Authorization Server in `auth-service`, resource server config for each microservice, JWT claims for permission codes).
0.11. Set up CI skeleton (Jenkins pipeline definitions) and Kubernetes manifests skeleton (namespaces per environment: local, development, production).

## Phase 1 — Identity, Auth & Permissions
Goal: users, personnel, sign up/login, OAuth2, and the P0–P5 permission group system.

1.1. `auth-service`: user entity (Customer/Seller/User base), registration, login, OAuth2 password/token flows, refresh tokens.
1.2. `auth-service`: personnel entity, personnel login (separate from public users), personnel permission groups (P0–P5 + A/E/D suffix codes) data model and evaluation logic.
1.3. `auth-service`: "Permissions" management endpoints (P0-only: create/edit/delete permission groups and codes).
1.4. `auth-service`: ID verification dummy flow (accepts any ID number + 2 photos), submits to moderator "ID Applications" queue.
1.5. `auth-service`: Seller application dummy flow (dummy company info), submits to moderator "Seller Applications" queue.
1.6. `auth-service`: soft-delete + ban flow — banning a user copies id numbers, phone numbers, IP addresses, emails into a `banned_users` table.
1.7. `auth-service`: user & personnel activity logging hooks (login, logout, key actions) writing to the Phase 0.9 log store.
1.8. `berkay-public`: sign up / login pages, ID verification dummy page, "apply to become Seller" flow from profile section.
1.9. `berkay-personnel`: login page (separate app entry, no public nav), permission-aware route/menu rendering based on personnel's P-codes.

---

## ⚠️ SCOPE PIVOT (2026-08-17) — read this before continuing past Phase 1

This was meant to be a **prototype that demonstrates enterprise architecture**, not a production-grade system. Phase 0 and Phase 1 already went far deeper than intended (real OAuth2 Authorization Server, Eureka, config server, full permission-code engine) and burned most of the available budget. Remaining budget is small (~$20), so Phases 2–10 below are rewritten **thin on purpose**: keep every feature from RULES.md visibly present and clickable, but implement the cheapest correct version of each, not the enterprise-grade one. Cost/effort now outranks completeness-of-realism for every remaining task.

Ground rules for everything below:
* **Fewer services.** Do not create a new microservice per feature. Fold Phase 3–6 features into `product-service` (catalog/search/reviews/Q&A/favorites/campaigns) and a single new `order-service` (basket/order/payment/coupons). No `catalog-service`, `payment-service`, `favorites-service`, or `campaign-service`.
* **Skip real Elasticsearch aggregations.** A single ES index for keyword + sort is enough; implement category/price filtering with plain JPA queries against Oracle, not ES facet aggregations. If ES is more trouble than it's worth for a given task, a well-indexed Oracle query satisfying the same API contract is an acceptable substitute — note the substitution in DONE.md.
* **No real recommendation/ranking logic.** "Similar/recommended/bought-together/suggested more" lists = same-category or random-N queries. Label them as such in code comments only if genuinely non-obvious; otherwise they're just simple queries.
* **No approval workflows unless RULES.md explicitly names one** (category change-requests is the one exception already scoped down in 2.2).
* **UI polish is inline, not a separate pass.** Build the red-accent theme token and light/dark toggle once in Phase 2, reuse everywhere after. Don't schedule a later "polish phase."
* **Testing is a thin safety net, not a suite.** A handful of unit tests per service and one or two Selenium happy-path scripts, not per-flow coverage.
* **Deployment is already done at skeleton level** (Phase 0.11: Dockerfiles, k8s namespaces, Jenkinsfile). Don't expand it unless a task specifically needs a new Dockerfile.
* Every task below should be completable as a single DONE.md entry each — if a task is trending large mid-implementation, cut scope further rather than expanding it.

---

## Phase 2 — Category & Catalog Management (thin)
Goal: 3-level category hierarchy, enough admin management to demo it, and the shared UI theme.

2.1. `product-service`: Main Category / Sub Type / Inner Type entities with translation tables (tr/en). Plain CRUD, no versioning.
2.2. Category "Change Requests": single simple workflow — moderator edit creates a pending row, P0 admin approves/denies via one endpoint. Keep the entity and the two endpoints minimal.
2.3. `berkay-personnel`: one combined "Categories" page (all 3 levels in one screen, tabs or nested lists) + one "Change Requests" list. Also establish here: the shared red-accent CSS variable and light/dark toggle, reused by every later page instead of a dedicated polish phase.
2.4. `berkay-public`: hover-driven "Categories" mega-menu (Main > Sub > Inner, 5 + Show More) and horizontal top-nav category shortcuts.

## Phase 3 — Product Catalog & Search (thin)
Goal: product CRUD and a working, but simplified, search/browse/detail experience.

3.1. `product-service`: Product entity (price, stock, short/long description, up to 10 photo URLs, key features, tr/en translations) with soft delete.
3.2. `product-service`: single Elasticsearch index synced on write; search API does keyword + the 7 required sort options; category/price filtering via JPA query params, not ES aggregations. Pagination: 20/page, infinite scroll trigger ~item 16.
3.3. `product-service`: product detail endpoint — rating average/count and Q&A are zero/empty placeholders in the response shape for now (real Review/Q&A entities are built in Phase 6.1/6.2 and will populate these same fields, no reshaping needed later); similar/recommended/bought-together/"might also interest you"/popular-brands/popular-pages are all simple same-category-or-random queries. Delivery estimate is a fixed dummy calculation (e.g. flat 3–7 days, no real distance logic).
3.4. `berkay-public`: Search Results page (grid, sort dropdown, left filter panel, infinite scroll).
3.5. `berkay-public`: Product Detail page — all sections from RULES.md present (slider, Q&A, campaigns, buy now/add to basket with ID-verification gate, follow seller, key features, the horizontal slider sections, text-only "might also interest you", popular brands/pages) — each wired to the simplified data above.
3.6. `berkay-personnel`: "Products" page (list/edit/soft-delete).

## Phase 4 — Seller Experience (thin)
Goal: enough seller tooling to demo the seller side of the marketplace.

4.1. `product-service`: seller profile fields on top of existing Seller account (store name, follower count) + a simple earned-money ledger entity (just a running total updated on order completion, no payout logic).
4.2. `berkay-public`: "My Store" hover menu, "Add New Product" page (up to 10 photos, both languages), "My Products" list/edit page, and seller public profile page (products + follow button + seller's Q&A list) — one PR-sized task covering all four, since each is a thin CRUD screen over Phase 3's Product entity.

## Phase 5 — Basket, Orders & Payment (thin)
Goal: cart → dummy payment → order history, in one new service.

5.1. New `order-service`: Basket entity/API (add/remove/update qty) + Order entity/status lifecycle + dummy Payment (accepts any card, optional encrypted "save card" field, autofills on next checkout) — build as one cohesive service, not staged sub-tasks.
5.2. `berkay-public`: Basket page, Checkout/Payment page (saved-card autofill), Buy Now flow, ID-verification gate; "My Account" dropdown pages (All My Orders, My Reviews, My Discount Coupons, Seller Messages placeholder, My User Information, Log Out) — Seller Messages can be a static "coming soon" placeholder, it's not in RULES.md's required feature list beyond being a menu entry.

## Phase 6 — Reviews, Q&A, Favorites & Campaigns (thin)
Goal: the remaining social/engagement features, folded into existing services.

6.1. `product-service`: Review entity (tied to completed orders via `order-service` lookup), Q&A entity — already partially consumed by Phase 3; this task is whatever wasn't needed yet (e.g. the ask/answer write endpoints if Phase 3 only built read).
6.2. `product-service`: Favorite + Follow-seller entities; "suggested more" boost for followed sellers = simple SQL boost (e.g. `ORDER BY followed DESC`), not a ranking model.
6.3. `order-service`: flat-percentage discount coupon entity + apply-at-checkout logic.
6.4. `product-service`: Campaign entity + product-campaign association, surfaced on product detail (already stubbed in 3.5) and footer.
6.5. `berkay-public`: "My Favorites" page, follow/unfollow buttons, review submission UI, Q&A ask/answer UI, coupons page — one combined task.

## Phase 7 — Personnel/Admin Panel Completion (thin)
Goal: every admin page RULES.md names exists and works; no extra polish.

7.1. `berkay-personnel`: "Personnel" page (create accounts, assign permission groups).
7.2. `berkay-personnel`: "Users" page (view/edit/soft-delete, trigger existing ban flow from 1.6).
7.3. `berkay-personnel`: "ID Applications" and "Seller Applications" review pages (these mostly just need a UI in front of 1.4/1.5's existing endpoints — one combined task).
7.4. `berkay-personnel`: "User Logs" and "Personnel Logs" viewer pages (read-only tables over log-service's existing GET /logs).
7.5. `berkay-personnel`: "Campaigns" management page (CRUD over 6.4's Campaign entity) + the shared shell (top nav with language dropdown/logout, permission-driven collapsible left menu) if not already produced as a side effect of earlier personnel pages.

## Phase 8 — Testing & Quality (thin safety net)
Goal: a baseline of automated checks, not full coverage. Skip if budget runs out before this phase — it is the lowest-priority remaining phase.

8.1. A handful of backend unit tests for the highest-risk logic added in Phases 2–7 (payment, ban, permissions already covered in Phase 1).
8.2. One Postman collection covering the core happy path (browse → basket → checkout → order).
8.3. One or two Selenium scripts for the single most important flow (sign up → search → buy) — not per-feature suites.

## Phase 9 — Final polish & wrap-up (thin)
Goal: make sure what exists is coherent, not add anything new.

9.1. Verify responsive layout works at a basic mobile/tablet breakpoint on the pages built in Phases 2–7 (fix only actual breakage, don't do a dedicated redesign pass).
9.2. Footer component (Who Are We/Contact/Security/Campaigns/Sell on Berkay/Live Support/How May I Return links, payment network icons, social icons, copyright/legal links) and final top-nav assembly — build once, reuse; this was deferred from Phase 2 only because it needs pages from later phases to link to.
9.3. Skip Kubernetes/Jenkins/observability expansion entirely — Phase 0.11's skeleton is the deliverable for deployment. Only touch it if something in Phases 2–8 broke it (e.g. a new service needs a Dockerfile).

---

## How to use this file with DONE.md
* Before starting work, diff `DONE.md` against this file to find the next uncompleted task (tasks are numbered `Phase.Task`, e.g. `3.4`).
* Complete tasks strictly in order within a phase; cross-phase reordering is only allowed when a later phase's task has no unmet dependency on an earlier one.
* After a task is fully implemented (no TODOs, no placeholders, compiling/running), append exactly one line to `DONE.md` describing what was built, referencing its task number.
