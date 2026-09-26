import Link from "next/link";
import { getSession } from "@/lib/session";

const steps = [
  "Next.js generates a random code_verifier and sends only its SHA-256 code_challenge.",
  "You sign in on the Spring Authorization Server login page.",
  "The server redirects back with a one-time authorization code.",
  "Next.js exchanges the code plus the code_verifier for tokens, server-side.",
  "Tokens live in an encrypted httpOnly cookie and are used to call the Notes API.",
];

export default async function Home({ searchParams }: PageProps<"/">) {
  const { error } = await searchParams;
  const session = await getSession();

  return (
    <div className="space-y-8">
      {error && (
        <p className="rounded-md border border-red-600/30 bg-red-600/10 px-4 py-3 text-sm text-red-700 dark:text-red-300">
          Sign-in failed: {String(error)}
        </p>
      )}

      <section className="space-y-3">
        <h1 className="text-3xl font-semibold tracking-tight">
          Spring Boot OAuth2 + PKCE
        </h1>
        <p className="max-w-2xl text-black/70 dark:text-white/70">
          A Next.js public client that authenticates against a Spring Authorization Server
          backed by MySQL using the Authorization Code flow with Proof Key for Code Exchange.
        </p>
        <div className="flex gap-3 pt-2">
          {session ? (
            <Link href="/notes" className="btn-primary">Go to my notes</Link>
          ) : (
            <>
              <a href="/api/auth/login" className="btn-primary">Sign in</a>
              <Link href="/register" className="btn-secondary">Create an account</Link>
            </>
          )}
        </div>
      </section>

      <section className="card">
        <h2 className="mb-3 font-medium">How the flow works</h2>
        <ol className="list-decimal space-y-1.5 pl-5 text-sm text-black/70 dark:text-white/70">
          {steps.map((step) => (
            <li key={step}>{step}</li>
          ))}
        </ol>
      </section>
    </div>
  );
}
