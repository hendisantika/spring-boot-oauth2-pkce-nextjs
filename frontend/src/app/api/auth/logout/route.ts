import { NextResponse } from "next/server";
import { config, endpoints } from "@/lib/config";
import { deleteSession, getSession } from "@/lib/session";

/**
 * Clears the local session and performs OIDC RP-initiated logout so the
 * Authorization Server session is terminated as well.
 */
export async function POST() {
  const session = await getSession();
  await deleteSession();

  if (!session) {
    return NextResponse.redirect(new URL("/", config.appUrl), 303);
  }

  const url = new URL(endpoints.logout);
  url.search = new URLSearchParams({
    id_token_hint: session.idToken,
    client_id: config.clientId,
    post_logout_redirect_uri: config.appUrl,
  }).toString();

  return NextResponse.redirect(url, 303);
}
