# Personnel Page Codes Registry

Reference document for RULES.md's permission scheme (task 1.2/1.3): every admin-panel page has
one fixed code baked in when it's built - the "Permissions" page (P0-only) then lets admins
grant/revoke which personnel/groups can reach each fixed code, it does not let admins invent new
codes for existing pages. **Whenever a task adds a new protected personnel/admin page or
endpoint, add its code here first** so two tasks never pick the same number.

Each code grants view access to the page by existing at all on a `PermissionGroup`; the A/E/D
suffix flags add capabilities. Resource servers other than auth-service check the JWT
`permissions` claim (OAUTH2.md) via `PERM_<code>_VIEW`/`_ADD`/`_EDIT`/`_DELETE` authorities
(`common-lib`'s `PermissionAuthoritiesConverter` decodes the compact `permissions` claim
strings into per-capability authorities the same way `PermissionGroup#toAuthorities` does on the
session side); auth-service's own personnel endpoints check the session authorities directly.

| Code | Page | Owning service | Added in |
|---|---|---|---|
| P0 | Permissions (create/edit/delete permission groups) | auth-service | task 1.2/1.3 |
| P1 | ID Applications (accept/deny ID verification submissions) | auth-service | task 1.4 |
| P2 | Seller Applications (accept/deny seller applications) | auth-service | task 1.5 |
| P3 | Users (ban action now; full list/view/edit/soft-delete added in Phase 7.2) | auth-service | task 1.6 |
| P4 | Categories (Main/Sub/Inner category management) | product-service | task 2.1 |
| P5 | Products (list/edit/soft-delete seller products) | product-service | task 3.6 |
| P6 | Campaigns (create/edit/delete campaigns, manage product associations) | product-service | task 6.4 |

Not yet assigned (RULES.md names these pages; codes are assigned when each is actually built):
Personnel, User Logs, Personnel Logs (Phase 7).
