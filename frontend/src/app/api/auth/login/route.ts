import { NextResponse, type NextRequest } from "next/server";
import { config, endpoints } from "@/lib/config";
import { createCodeChallenge, randomString } from "@/lib/pkce";
import { saveAuthTransaction } from "@/lib/session";
import { safeReturnTo } from "@/lib/safe-redirect";

/**
 * Starts the OAuth2 Authorization Code flow with PKCE.
 * A fresh code_verifier, state and nonce are generated and stored in an
 * encrypted, short-lived, httpOnly cookie; only the S256 challenge leaves the server.
 */
export async function GET(request: NextRequest) {
  const codeVerifier = randomString(64);
  const state = randomString();
  const nonce = randomString();
  const returnTo = safeReturnTo(request.nextUrl.searchParams.get("returnTo"));

  await saveAuthTransaction({ state, nonce, codeVerifier, returnTo });

  const url = new URL(endpoints.authorize);
  url.search = new URLSearchParams({
    response_type: "code",
    client_id: config.clientId,
    redirect_uri: config.redirectUri,
    scope: config.scopes,
    state,
    nonce,
    code_challenge: await createCodeChallenge(codeVerifier),
    code_challenge_method: "S256",
  }).toString();

  return NextResponse.redirect(url);
}
