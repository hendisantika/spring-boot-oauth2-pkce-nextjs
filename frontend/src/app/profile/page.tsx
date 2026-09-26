import type { Metadata } from "next";
import { decodeJwt } from "jose";
import { apiFetch, type User } from "@/lib/api";
import { getSession } from "@/lib/session";

export const metadata: Metadata = { title: "Profile · OAuth2 PKCE Notes" };

export default async function ProfilePage() {
  const [user, session] = await Promise.all([apiFetch<User>("/api/users/me"), getSession()]);
  const claims = session ? decodeJwt(session.accessToken) : {};

  const rows: [string, string][] = [
    ["Username", user.username],
    ["Email", user.email],
    ["Full name", user.fullName ?? "—"],
    ["Roles", user.roles.join(", ")],
    ["Member since", new Date(user.createdAt).toLocaleDateString("en")],
  ];

  return (
    <div className="space-y-8">
      <h1 className="text-2xl font-semibold tracking-tight">Profile</h1>

      <section className="card">
        <h2 className="mb-3 font-medium">Account (GET /api/users/me)</h2>
        <dl className="grid grid-cols-[max-content_1fr] gap-x-6 gap-y-2 text-sm">
          {rows.map(([label, value]) => (
            <div key={label} className="contents">
              <dt className="text-black/60 dark:text-white/60">{label}</dt>
              <dd className="break-words">{value}</dd>
            </div>
          ))}
        </dl>
      </section>

      <section className="card">
        <h2 className="mb-1 font-medium">Access token claims</h2>
        <p className="mb-3 text-xs text-black/60 dark:text-white/60">
          Decoded on the server. The raw token never reaches the browser.
        </p>
        <pre className="overflow-x-auto rounded-md bg-black/5 p-3 font-mono text-xs dark:bg-white/5">
          {JSON.stringify(claims, null, 2)}
        </pre>
      </section>
    </div>
  );
}
