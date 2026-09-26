import { NextResponse, type NextRequest } from "next/server";

const SESSION_COOKIE = "app_session";

/**
 * Optimistic auth check: send visitors without a session cookie straight to login.
 * The session itself is fully validated on the server when pages load.
 */
export function proxy(request: NextRequest) {
  if (!request.cookies.has(SESSION_COOKIE)) {
    const login = new URL("/api/auth/login", request.url);
    login.searchParams.set(
      "returnTo",
      request.nextUrl.pathname + request.nextUrl.search,
    );
    return NextResponse.redirect(login);
  }
  return NextResponse.next();
}

export const config = {
  matcher: ["/notes/:path*", "/profile/:path*", "/admin/:path*"],
};
