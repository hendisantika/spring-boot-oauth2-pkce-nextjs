# OAuth2 PKCE Notes: Next.js frontend

A Next.js 16 client for the Spring Boot OAuth2 Authorization Server. It runs the Authorization Code +
PKCE flow on the server (Backend-for-Frontend pattern), so access tokens are never exposed to browser JavaScript.

## Setup

```bash
cp .env.example .env.local
# set SESSION_SECRET, e.g. openssl rand -base64 32
npm install
npm run dev
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

| Command         | Description              |
|-----------------|--------------------------|
| `npm run dev`   | Start development server |
| `npm run build` | Production build         |
| `npm run start` | Start production server  |
| `npm run lint`  | Run ESLint               |
