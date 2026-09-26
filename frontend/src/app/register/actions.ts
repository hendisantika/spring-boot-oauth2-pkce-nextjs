"use server";

import { redirect } from "next/navigation";
import * as z from "zod";
import { ApiError, apiFetch } from "@/lib/api";

const RegisterSchema = z.object({
  username: z
    .string()
    .trim()
    .min(3, { error: "Username must be at least 3 characters." })
    .max(50)
    .regex(/^[a-zA-Z0-9._-]+$/, { error: "Only letters, digits, '.', '_' and '-'." }),
  email: z.email({ error: "Please enter a valid email." }).trim(),
  password: z.string().min(8, { error: "Password must be at least 8 characters." }).max(100),
  fullName: z.string().trim().max(150).optional(),
});

export type RegisterState =
  | { errors?: Record<string, string[] | undefined>; message?: string }
  | undefined;

export async function register(_state: RegisterState, formData: FormData): Promise<RegisterState> {
  const parsed = RegisterSchema.safeParse(Object.fromEntries(formData));
  if (!parsed.success) {
    return { errors: z.flattenError(parsed.error).fieldErrors };
  }

  try {
    await apiFetch("/api/users/register", {
      method: "POST",
      auth: false,
      body: JSON.stringify(parsed.data),
    });
  } catch (error) {
    if (error instanceof ApiError) {
      const fieldErrors = Object.fromEntries(
        Object.entries(error.problem.errors ?? {}).map(([k, v]) => [k, [v]]),
      );
      return { errors: fieldErrors, message: error.message };
    }
    throw error;
  }

  redirect("/api/auth/login?returnTo=/notes");
}
