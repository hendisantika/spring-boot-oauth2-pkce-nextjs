import "server-only";

import { redirect } from "next/navigation";
import { config } from "@/lib/config";
import { getSession } from "@/lib/session";

export type ProblemDetail = {
  title?: string;
  detail?: string;
  status: number;
  errors?: Record<string, string>;
};

export class ApiError extends Error {
  constructor(public readonly problem: ProblemDetail) {
    super(problem.detail ?? problem.title ?? `Request failed (${problem.status})`);
  }
}

export type User = {
  id: number;
  username: string;
  email: string;
  fullName: string | null;
  enabled: boolean;
  roles: string[];
  createdAt: string;
};

export type Note = {
  id: number;
  title: string;
  content: string | null;
  createdAt: string;
  updatedAt: string;
};

export type Page<T> = {
  content: T[];
  page: { size: number; number: number; totalElements: number; totalPages: number };
};

type Options = RequestInit & { auth?: boolean };

/**
 * Calls the Spring Boot resource server. Authenticated calls attach the access token
 * from the encrypted session; an expired session sends the user back to login.
 */
export async function apiFetch<T>(path: string, { auth = true, ...init }: Options = {}): Promise<T> {
  const headers = new Headers(init.headers);
  headers.set("Accept", "application/json");
  if (init.body) headers.set("Content-Type", "application/json");

  if (auth) {
    const session = await getSession();
    if (!session) redirect("/api/auth/login");
    headers.set("Authorization", `Bearer ${session.accessToken}`);
  }

  const res = await fetch(`${config.apiBaseUrl}${path}`, { ...init, headers, cache: "no-store" });

  if (res.status === 401 && auth) redirect("/api/auth/login");
  if (!res.ok) {
    const problem = (await res.json().catch(() => ({}))) as Partial<ProblemDetail>;
    throw new ApiError({ ...problem, status: res.status });
  }
  if (res.status === 204) return undefined as T;
  return (await res.json()) as T;
}
