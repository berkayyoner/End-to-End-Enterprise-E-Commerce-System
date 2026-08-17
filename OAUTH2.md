# OAuth 2.0 Authentication/Authorization Architecture

Reference document for task **0.10** in `ANALYSIS.md`. Defines the topology, token contents,
and per-service integration pattern; `auth-service` wires the actual Authorization Server once
real user/personnel/permission-group entities exist (task 1.1–1.3), and each resource server
gets its `SecurityFilterChain` when it has real endpoints to protect (starting task 1.8+). No
service should add unused OAuth2 config for endpoints that don't exist yet — see
`CONVENTIONS.md`'s "no dead wiring" discipline.

## 1. Topology

* **Authorization Server**: `auth-service`, using Spring Authorization Server
  (`spring-security-oauth2-authorization-server`). It is the single issuer for the whole
  platform (`iss` claim = the gateway's public base URL, not auth-service's internal one — see
  §4).
* **Resource Servers**: every other Spring Boot service that exposes protected endpoints
  (`product-service`, `order-service` (Phase 5), `campaign-service` (Phase 6), etc.) validates
  JWTs locally via `spring-boot-starter-oauth2-resource-server` + a JWK Set URI pointing at
  `auth-service`. No service other than `auth-service` ever sees a password.
* **Clients**: the two SPAs (`berkay-public`, `berkay-personnel`) are registered as OAuth2
  **public clients** using `authorization_code` + PKCE (no client secret — a browser SPA can't
  keep one). There is no `client_credentials` client yet; add one only when a real
  service-to-service call needs to authenticate as itself rather than forward a user's token
  (none does as of task 0.10).
* **No refresh tokens for the public client, by design**: Spring Authorization Server never
  issues a refresh token for the `authorization_code` grant when the client's authentication
  method is `NONE` (verified against `OAuth2RefreshTokenGenerator` in task 1.1 - it's an
  intentional, hardcoded security policy, not a config knob: a long-lived refresh token sitting
  in browser storage with no way to prove possession is exactly what OAuth 2.1 tells public
  clients to avoid). The SPA instead renews its 15-minute access token by silently repeating the
  `/oauth2/authorize` redirect (hidden iframe, no visible UI) while its `auth-service` session
  cookie is still valid - functionally the same "stay logged in without re-entering a password"
  outcome as a refresh token, without one ever leaving the server.
* **api gateway**: not a client and not (yet) a resource server. It proxies the
  `/api/auth/**` routes (including the AS's `/oauth2/*` and `/login` endpoints) straight through
  to `auth-service` per task 0.7, so both front-ends only ever talk to the gateway's public
  origin — the browser never calls `auth-service` directly. The gateway can add
  coarse-grained JWT validation later (Phase 1.8+) if a "reject unauthenticated requests before
  they reach a downstream service" optimization is wanted; not required for correctness since
  every resource server validates independently regardless.

## 2. Grant flow (per client)

1. SPA redirects the browser to `GET /api/auth/oauth2/authorize?...&code_challenge=...` (PKCE,
   `S256`) through the gateway.
2. `auth-service` renders/handles login (task 1.1's user entity + Spring Security
   `UserDetailsService`), issues an authorization code, redirects back to the SPA's
   `redirect_uri`.
3. SPA exchanges the code (+ `code_verifier`) at `POST /api/auth/oauth2/token` for an access
   token (JWT, short-lived, e.g. 15 min) and a refresh token (longer-lived, rotated on use).
4. SPA calls any resource server through the gateway with `Authorization: Bearer <jwt>`. The
   resource server validates the signature/issuer/expiry itself — no call back to
   `auth-service` per request.

## 3. JWT claims

| Claim | Meaning | Populated by |
|---|---|---|
| `sub` | `app_user.id` or `personnel.id` (as a string) | Spring Authorization Server default |
| `iss` | the platform's public issuer URL (gateway origin) | `issuer-uri` config, §4 |
| `aud` | list of resource server audience identifiers (`product-service`, ...) | custom `OAuth2TokenCustomizer` |
| `account_type` | `CUSTOMER`, `SELLER`, or `PERSONNEL` — which principal table `sub` refers to | custom `OAuth2TokenCustomizer` |
| `permissions` | for personnel tokens only: array of permission codes exactly as stored by the "Permissions" page (e.g. `["P0AED","P2ED"]`, RULES.md's P0–P5 + A/E/D scheme) | custom `OAuth2TokenCustomizer`, populated once task 1.2/1.3 build the permission-group data model |
| `id_verified` | boolean — customer/seller has passed ID verification (RULES.md gates Buy Now/Add to Basket/Sell on this) | custom `OAuth2TokenCustomizer` |

The customizer bean (`OAuth2TokenCustomizer<JwtEncodingContext>`) is implemented in
`auth-service` in task 1.1–1.3 once there is a real user/permission-group table to read these
values from — building it against fake data now would just be rewritten there.

## 4. Issuer URL behind the gateway

Because browsers and resource servers only ever see the gateway's public origin (never
`auth-service`'s internal `host:port`), `auth-service`'s
`spring.security.oauth2.authorizationserver.issuer` must be set to the gateway's externally
visible base URL per environment (e.g. `http://localhost:8080` locally, the real domain in
production) — **not** `http://localhost:8081`/`http://auth-service:8081`. Every resource
server's `spring.security.oauth2.resourceserver.jwt.issuer-uri` must match exactly, since token
validation checks the `iss` claim against it. `auth-service` additionally needs
`server.forward-headers-strategy: framework` so it correctly builds AS metadata/endpoint URLs
when reached through the gateway's reverse proxy.

## 5. Turning a permission-code JWT claim into Spring Security authorities

This part has no dependency on real user data existing yet, so it ships now in `common-lib`:
`com.berkay.common.security.PermissionAuthoritiesConverter` implements
`Converter<Jwt, Collection<GrantedAuthority>>`, reading the `permissions` claim (§3) and decomposing
each compact code into individual authorities matching RULES.md's capability scheme:
- Each code (e.g. `"P0AED"`) is parsed as (P\d+)([AED]*) — page code + optional capability suffix
- Always emits `PERM_<pageCode>_VIEW` (presence of the code implies view access)
- Conditionally emits `PERM_<pageCode>_ADD/EDIT/DELETE` if those letters appear in the suffix
- Example: `"P0AED"` → `PERM_P0_VIEW`, `PERM_P0_ADD`, `PERM_P0_EDIT`, `PERM_P0_DELETE` (4 authorities)
- Example: `"P2ED"` → `PERM_P2_VIEW`, `PERM_P2_EDIT`, `PERM_P2_DELETE` (3 authorities, no ADD)
- Example: `"P3"` → `PERM_P3_VIEW` (1 authority, view-only)

Plus maps `account_type` to `SimpleGrantedAuthority("ACCOUNT_" + accountType)`. Every resource server
wires it once via `JwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(...)` when it
adds `spring-boot-starter-oauth2-resource-server` (task 1.8+) — this keeps the claim-to-authority
mapping identical across every service instead of each one reimplementing it.
