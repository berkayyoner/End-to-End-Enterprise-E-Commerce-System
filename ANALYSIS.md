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

## Phase 2 — Category & Catalog Management
Goal: 3-level category hierarchy and admin/moderator category management with change-request approval flow.

2.1. `product-service` (or new `catalog-service`): Main Category / Sub Type / Inner Type entities with multi-language name/description translation tables.
2.2. Category "Change Requests" workflow: moderator edits create a pending change request; admin (P0/appropriate permission) approves or denies before it takes effect.
2.3. `berkay-personnel`: "Main Categories", "Sub Categories", "Inner Categories" management pages + "Change Requests" review page.
2.4. `berkay-public`: hover-driven "Categories" mega-menu (Main > Sub > Inner, 5 inner types + "Show More"), top-nav horizontal main category shortcuts linking into search/filter.

## Phase 3 — Product Catalog & Elasticsearch Search
Goal: product CRUD, Elasticsearch indexing, and the full search/browse experience.

3.1. `product-service`: Product entity (price, stock, description short/long, photos max 10, key features, translations for seller-provided names/descriptions) with Oracle persistence + soft delete.
3.2. `product-service`: Elasticsearch index + sync pipeline (on create/update/delete) for product search documents.
3.3. `product-service`: paginated/infinite-scroll search API (20 items per page, fetch-more trigger around item 16), sort options (Suggested Ranking, Most Expensive, Cheapest, Newest, Most Selling, Most Favorited, Most Rated).
3.4. `product-service`: detailed filter API — category checkboxes (Main/Sub/Inner), price range, and other facets backed by Elasticsearch aggregations.
3.5. `product-service`: product detail aggregation endpoint — rating summary, Q&A, campaigns, similar/recommended/"bought together" product lists, seller info snapshot, estimated delivery calculation (customer vs seller location).
3.6. `berkay-public`: Search Results page — 4-column product grid, sort dropdown, left vertical filter panel, infinite scroll.
3.7. `berkay-public`: Product Detail page — photo slider (max 10), price/stock/rating, Show More description expansion, Q&A section, campaigns section, Buy Now / Add to Basket (ID-verification gate), follow seller, key features boxes, all horizontal slider sections (similar / recommended / bought-together), "these might also interest you" text list, popular brands/stores, popular pages section.
3.8. `berkay-personnel`: "Products" management page (edit/soft-delete seller products).

## Phase 4 — Seller Experience
Goal: seller-side storefront management.

4.1. `product-service` / `auth-service`: Seller profile (store name, earned money ledger, followers).
4.2. `berkay-public`: "My Store" hover menu (earned money info, "My Products", "Add New Product").
4.3. `berkay-public`: "Add New Product" page — up to 10 photo uploads + all product fields from Phase 3.1, both language variants.
4.4. `berkay-public`: "My Products" page — list + edit all product details.
4.5. `berkay-public`: Seller public profile page ("go to market") — all seller products, follow button, seller Q&A list.

## Phase 5 — Basket, Orders & Payment
Goal: cart, checkout, dummy payment, and order history.

5.1. New `order-service`: Basket entity/API (add/remove/update quantity), persisted per signed-in user.
5.2. New `payment-service` (or module in `order-service`): dummy payment processing — accepts any card info, "save card for future purchases" (encrypted-at-rest storage), auto-fill saved card on checkout.
5.3. `order-service`: Order entity, order status lifecycle, order history ("All My Orders").
5.4. `berkay-public`: Basket page, Checkout/Payment page (saved-card autofill), Buy Now direct-to-payment flow, ID-verification gate enforcement.
5.5. `berkay-public`: "My Account" dropdown pages — All My Orders, My Reviews, My Discount Coupons, Seller Messages, My User Information, Log Out.

## Phase 6 — Reviews, Q&A, Favorites & Campaigns
Goal: social/engagement features layered on top of catalog and orders.

6.1. `product-service`: Rating/Review entity (average rating, total count) tied to completed orders.
6.2. `product-service`: Q&A entity (customer asks, seller answers) surfaced on product detail and seller profile.
6.3. New `favorites` module/service: Favorite products, Follow seller relationships; personalized "suggested more" ranking boost for followed sellers.
6.4. New `campaign-service` (or module): Campaign entity, product-campaign association, "Campaigns" listing surfaced on product detail and footer.
6.5. Discount coupon entity + application at checkout.
6.6. `berkay-public`: "My Favorites" page, follow/unfollow UI, review submission UI, Q&A ask/answer UI, coupons page.

## Phase 7 — Personnel/Admin Panel Completion
Goal: fill out remaining admin/moderator management pages with unique page codes (e.g. "P2 - Product Requests").

7.1. `berkay-personnel`: "Personnel" page (create personnel accounts, assign permission groups).
7.2. `berkay-personnel`: "Users" page (view/edit/soft-delete public accounts, trigger ban flow).
7.3. `berkay-personnel`: "ID Applications" review page (accept/deny).
7.4. `berkay-personnel`: "Seller Applications" review page (accept/deny).
7.5. `berkay-personnel`: "User Logs" and "Personnel Logs" viewer pages.
7.6. `berkay-personnel`: "Campaigns" management page.
7.7. `berkay-personnel`: shared shell — top nav (language dropdown, logout only), collapsible left page menu driven by personnel's permission codes, multi-language support for the whole app.

## Phase 8 — Cross-Cutting UI/UX Polish
Goal: enterprise-grade shared UI system across both front-ends.

8.1. Design system: shared component library (buttons, inputs, sliders, modals) with a single configurable red accent variable (theme token, not hardcoded).
8.2. Light/dark mode toggle wired through the design system (`berkay-public` nav + `berkay-personnel` nav).
8.3. Responsive layout pass for `berkay-public` (mobile/tablet breakpoints) for nav, category mega-menu, search grid, product detail sliders, checkout.
8.4. Footer component: sub-sections (Who Are We, Contact, Security, Campaigns, Sell on Berkay, Live Support, How May I Return), payment network icons (MasterCard, Visa, Troy), social icons, "©2026 All Rights Reserved", Cookie Options / Terms of Use / Protection of Personal Data links.
8.5. Top navigation bar final assembly: logo→home, Elasticsearch-backed search bar, My Account/My Favorites/My Basket, light/dark toggle, language switcher, Categories button + horizontal main categories.

## Phase 9 — Testing & Quality
Goal: automated verification across the stack, continuously extended per feature (not a one-time pass).

9.1. Backend unit tests per service (JUnit) for services delivered in Phases 1–7.
9.2. Postman collections per service for manual/CI API verification.
9.3. Selenium end-to-end test suites for critical public flows (search, product detail, checkout, sign up) and personnel flows (login, approvals).
9.4. CI wiring: Jenkins pipeline runs unit tests + Postman (Newman) + Selenium suites per environment.

## Phase 10 — Deployment & Operations
Goal: containerize, orchestrate, and ship.

10.1. Dockerfiles for every service and front-end (multi-stage builds).
10.2. Kubernetes manifests (Deployments, Services, Ingress, ConfigMaps/Secrets) per environment (local/development/production).
10.3. Jenkins pipelines: build → test → containerize → deploy per environment, with promotion gates.
10.4. Observability: centralized log aggregation dashboard, health checks/readiness probes for all services.

---

## How to use this file with DONE.md
* Before starting work, diff `DONE.md` against this file to find the next uncompleted task (tasks are numbered `Phase.Task`, e.g. `3.4`).
* Complete tasks strictly in order within a phase; cross-phase reordering is only allowed when a later phase's task has no unmet dependency on an earlier one.
* After a task is fully implemented (no TODOs, no placeholders, compiling/running), append exactly one line to `DONE.md` describing what was built, referencing its task number.
