import Link from "next/link";
import { getSession } from "@/lib/session";

export async function Header() {
  const session = await getSession();
  const isAdmin = session?.user.roles.includes("ADMIN");

  return (
    <header className="border-b border-black/10 dark:border-white/10">
      <nav className="mx-auto flex max-w-4xl flex-wrap items-center gap-x-6 gap-y-2 px-4 py-4">
        <Link href="/" className="font-semibold tracking-tight">
          OAuth2 PKCE Notes
        </Link>
        {session && (
          <>
            <Link href="/notes" className="text-sm hover:underline">Notes</Link>
            <Link href="/profile" className="text-sm hover:underline">Profile</Link>
            {isAdmin && <Link href="/admin" className="text-sm hover:underline">Admin</Link>}
          </>
        )}
        <div className="ml-auto flex items-center gap-3">
          {session ? (
            <>
              <span className="text-sm text-black/60 dark:text-white/60">
                {session.user.name ?? session.user.sub}
              </span>
              <form action="/api/auth/logout" method="post">
                <button className="btn-secondary" type="submit">Sign out</button>
              </form>
            </>
          ) : (
            <>
              <Link href="/register" className="text-sm hover:underline">Register</Link>
              <a href="/api/auth/login" className="btn-primary">Sign in</a>
            </>
          )}
        </div>
      </nav>
    </header>
  );
}
