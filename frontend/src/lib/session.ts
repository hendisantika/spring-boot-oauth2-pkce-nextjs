import "server-only";

import { cookies } from "next/headers";
import { EncryptJWT, jwtDecrypt, type JWTPayload } from "jose";
import { config } from "@/lib/config";

export const SESSION_COOKIE = "app_session";
export const AUTH_TX_COOKIE = "app_auth_tx";

export type SessionUser = {
  sub: string;
  name?: string;
  email?: string;
  preferredUsername?: string;
  roles: string[];
};

export type Session = {
  accessToken: string;
  idToken: string;
  scope: string;
  expiresAt: number; // epoch seconds
  user: SessionUser;
};

/** Transient state kept between /api/auth/login and /api/auth/callback. */
export type AuthTransaction = {
  state: string;
  nonce: string;
  codeVerifier: string;
  returnTo: string;
};

async function encryptionKey(): Promise<Uint8Array> {
  // Derive a 256-bit key from the configured secret
  const digest = await crypto.subtle.digest(
    "SHA-256",
    new TextEncoder().encode(config.sessionSecret),
  );
  return new Uint8Array(digest);
}

async function seal(payload: JWTPayload, expiresAt: number): Promise<string> {
  return new EncryptJWT(payload)
    .setProtectedHeader({ alg: "dir", enc: "A256GCM" })
    .setIssuedAt()
    .setExpirationTime(expiresAt)
    .encrypt(await encryptionKey());
}

async function unseal<T>(token: string | undefined): Promise<T | null> {
  if (!token) return null;
  try {
    const { payload } = await jwtDecrypt(token, await encryptionKey());
    return payload as T;
  } catch {
    return null;
  }
}

const baseCookie = {
  httpOnly: true,
  secure: process.env.NODE_ENV === "production",
  sameSite: "lax" as const,
  path: "/",
};

export async function createSession(session: Session): Promise<void> {
  const cookieStore = await cookies();
  cookieStore.set(SESSION_COOKIE, await seal(session, session.expiresAt), {
    ...baseCookie,
    expires: new Date(session.expiresAt * 1000),
  });
}

export async function getSession(): Promise<Session | null> {
  const cookieStore = await cookies();
  const session = await unseal<Session>(cookieStore.get(SESSION_COOKIE)?.value);
  if (!session || session.expiresAt * 1000 <= Date.now()) return null;
  return session;
}

export async function deleteSession(): Promise<void> {
  const cookieStore = await cookies();
  cookieStore.delete(SESSION_COOKIE);
}

export async function saveAuthTransaction(tx: AuthTransaction): Promise<void> {
  const expiresAt = Math.floor(Date.now() / 1000) + 10 * 60;
  const cookieStore = await cookies();
  cookieStore.set(AUTH_TX_COOKIE, await seal(tx, expiresAt), {
    ...baseCookie,
    path: "/api/auth",
    expires: new Date(expiresAt * 1000),
  });
}

export async function consumeAuthTransaction(): Promise<AuthTransaction | null> {
  const cookieStore = await cookies();
  const tx = await unseal<AuthTransaction>(cookieStore.get(AUTH_TX_COOKIE)?.value);
  cookieStore.delete({ name: AUTH_TX_COOKIE, path: "/api/auth" });
  return tx;
}
