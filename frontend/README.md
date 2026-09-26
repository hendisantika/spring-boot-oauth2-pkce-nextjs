# OAuth2 PKCE Notes: Next.js frontend

A Next.js 16 client for the Spring Boot OAuth2 Authorization Server. It runs the Authorization Code +
PKCE flow on the server (Backend-for-Frontend pattern), so access tokens are never exposed to browser JavaScript.

## Setup

```bash
cp .env.example .env.local
# set SESSION_SECRET, e.g. openssl rand -base64 32
bun install
bun dev
```

The backend must be running on http://localhost:8080 (see the root README).

## How it works

| File                                   | Responsibility                                                     |
|----------------------------------------|--------------------------------------------------------------------|
| `src/app/api/auth/login/route.ts`      | Creates `code_verifier`/`state`/`nonce`, redirects with S256 challenge |
| `src/app/api/auth/callback/route.ts`   | Validates state, exchanges code + verifier, verifies ID token      |
| `src/app/api/auth/logout/route.ts`     | Clears session and performs OIDC RP-initiated logout               |
| `src/lib/session.ts`                   | Encrypted (JWE A256GCM) httpOnly session and auth transaction cookies |
| `src/lib/api.ts`                       | Server-only API client that attaches the bearer token              |
| `src/proxy.ts`                         | Redirects unauthenticated visitors of protected routes to login    |

## Scripts

[Bun](https://bun.sh) is the package manager and script runner (version pinned in `packageManager`).

| Command             | Description                       |
|---------------------|-----------------------------------|
| `bun dev`           | Start development server          |
| `bun run build`     | Production build                  |
| `bun run start`     | Start production server           |
| `bun run lint`      | Run ESLint                        |
| `bun run typecheck` | Generate route types and run tsc  |
