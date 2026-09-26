import "server-only";

function required(name: string): string {
  const value = process.env[name];
  if (!value) {
    throw new Error(`Missing required environment variable: ${name}`);
  }
  return value;
}

export const config = {
  appUrl: process.env.APP_URL ?? "http://localhost:3000",
  issuer: process.env.AUTH_ISSUER ?? "http://localhost:8080",
  clientId: process.env.AUTH_CLIENT_ID ?? "pkce-client",
  redirectUri:
    process.env.AUTH_REDIRECT_URI ?? "http://localhost:3000/api/auth/callback",
  scopes:
    process.env.AUTH_SCOPES ?? "openid profile email notes.read notes.write",
  apiBaseUrl: process.env.API_BASE_URL ?? "http://localhost:8080",
  get sessionSecret() {
    return required("SESSION_SECRET");
  },
};

export const endpoints = {
  authorize: `${config.issuer}/oauth2/authorize`,
  token: `${config.issuer}/oauth2/token`,
  jwks: `${config.issuer}/oauth2/jwks`,
  logout: `${config.issuer}/connect/logout`,
};
