"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import * as z from "zod";
import { ApiError, apiFetch } from "@/lib/api";

const NoteSchema = z.object({
  title: z.string().trim().min(1, { error: "Title is required." }).max(200),
  content: z.string().max(10_000).optional(),
});

export type NoteFormState =
  | { errors?: Record<string, string[] | undefined>; message?: string; ok?: boolean }
  | undefined;

function toState(error: unknown): NoteFormState {
  if (error instanceof ApiError) {
    const errors = Object.fromEntries(
      Object.entries(error.problem.errors ?? {}).map(([k, v]) => [k, [v]]),
    );
    return { errors, message: error.message };
  }
  throw error;
}

export async function createNote(_state: NoteFormState, formData: FormData): Promise<NoteFormState> {
  const parsed = NoteSchema.safeParse(Object.fromEntries(formData));
  if (!parsed.success) return { errors: z.flattenError(parsed.error).fieldErrors };

  try {
    await apiFetch("/api/notes", { method: "POST", body: JSON.stringify(parsed.data) });
  } catch (error) {
    return toState(error);
  }
  revalidatePath("/notes");
  return { ok: true };
}

export async function updateNote(
  id: number,
  _state: NoteFormState,
  formData: FormData,
): Promise<NoteFormState> {
  const parsed = NoteSchema.safeParse(Object.fromEntries(formData));
  if (!parsed.success) return { errors: z.flattenError(parsed.error).fieldErrors };

  try {
    await apiFetch(`/api/notes/${id}`, { method: "PUT", body: JSON.stringify(parsed.data) });
  } catch (error) {
    return toState(error);
  }
  revalidatePath("/notes");
  redirect("/notes");
}

export async function deleteNote(id: number): Promise<void> {
  await apiFetch(`/api/notes/${id}`, { method: "DELETE" });
  revalidatePath("/notes");
}
