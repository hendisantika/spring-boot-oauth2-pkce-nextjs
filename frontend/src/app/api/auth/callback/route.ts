import { createRemoteJWKSet, jwtVerify } from "jose";
import { NextResponse, type NextRequest } from "next/server";
import { config, endpoints } from "@/lib/config";
import { consumeAuthTransaction, createSession } from "@/lib/session";

const jwks = createRemoteJWKSet(new URL(endpoints.jwks));

type TokenResponse = {
  access_token: string;
  id_token: string;
  scope: string;
  token_type: string;
  expires_in: number;
};

function fail(reason: string) {
  const url = new URL("/", config.appUrl);
  url.searchParams.set("error", reason);
  return NextResponse.redirect(url);
}

/**
 * Handles the redirect back from the Authorization Server: validates state,
 * exchanges the code together with the code_verifier, verifies the ID token
 * and stores the tokens in an encrypted session cookie.
 */
export async function GET(request: NextRequest) {
  const params = request.nextUrl.searchParams;
  const tx = await consumeAuthTransaction();

  if (params.get("error")) {
    return fail(params.get("error_description") ?? params.get("error")!);
  }
  if (!tx) {
    return fail("Login session expired, please try again");
  }
  if (params.get("state") !== tx.state) {
    return fail("Invalid state parameter");
  }
  const code = params.get("code");
  if (!code) {
    return fail("Missing authorization code");
  }

  const tokenRes = await fetch(endpoints.token, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({
      grant_type: "authorization_code",
      client_id: config.clientId,
      code,
      redirect_uri: config.redirectUri,
      code_verifier: tx.codeVerifier,
    }),
    cache: "no-store",
  });

  if (!tokenRes.ok) {
    const body = await tokenRes.json().catch(() => ({}));
    return fail(body.error_description ?? body.error ?? "Token exchange failed");
  }

  const tokens = (await tokenRes.json()) as TokenResponse;

  let idClaims;
  try {
    ({ payload: idClaims } = await jwtVerify(tokens.id_token, jwks, {
      issuer: config.issuer,
      audience: config.clientId,
    }));
  } catch {
    return fail("Invalid ID token");
  }
  if (idClaims.nonce !== tx.nonce) {
    return fail("Invalid nonce");
  }

  const { payload: accessClaims } = await jwtVerify(tokens.access_token, jwks, {
    issuer: config.issuer,
  });

  await createSession({
    accessToken: tokens.access_token,
    idToken: tokens.id_token,
    scope: tokens.scope,
    expiresAt: Math.floor(Date.now() / 1000) + tokens.expires_in,
    user: {
      sub: String(idClaims.sub),
      name: idClaims.name as string | undefined,
      email: idClaims.email as string | undefined,
      preferredUsername: idClaims.preferred_username as string | undefined,
      roles: (accessClaims.roles as string[] | undefined) ?? [],
    },
  });

  return NextResponse.redirect(new URL(tx.returnTo, config.appUrl));
}
