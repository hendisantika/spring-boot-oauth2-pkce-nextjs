import type { Metadata } from "next";
import Link from "next/link";
import { apiFetch, type Note, type Page } from "@/lib/api";
import { createNote, deleteNote } from "./actions";
import { NoteForm } from "./note-form";

export const metadata: Metadata = { title: "My notes · OAuth2 PKCE Notes" };

const dateFormat = new Intl.DateTimeFormat("en", { dateStyle: "medium", timeStyle: "short" });

export default async function NotesPage() {
  const notes = await apiFetch<Page<Note>>("/api/notes?size=50");

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">My notes</h1>
        <p className="text-sm text-black/60 dark:text-white/60">
          {notes.page.totalElements} note{notes.page.totalElements === 1 ? "" : "s"} · served by
          the Spring Boot resource server with your access token
        </p>
      </div>

      <section className="card">
        <h2 className="mb-3 font-medium">New note</h2>
        <NoteForm action={createNote} submitLabel="Add note" />
      </section>

      <section className="space-y-3">
        {notes.content.length === 0 && (
          <p className="text-sm text-black/60 dark:text-white/60">No notes yet.</p>
        )}
        {notes.content.map((note) => (
          <article key={note.id} className="card">
            <div className="flex items-start justify-between gap-4">
              <div className="min-w-0">
                <h3 className="font-medium break-words">{note.title}</h3>
                <p className="text-xs text-black/50 dark:text-white/50">
                  Updated {dateFormat.format(new Date(note.updatedAt))}
                </p>
              </div>
              <div className="flex shrink-0 gap-1">
                <Link href={`/notes/${note.id}`} className="btn-secondary">Edit</Link>
                <form action={deleteNote.bind(null, note.id)}>
                  <button type="submit" className="btn-danger">Delete</button>
                </form>
              </div>
            </div>
            {note.content && (
              <p className="mt-2 text-sm whitespace-pre-wrap break-words text-black/80 dark:text-white/80">
                {note.content}
              </p>
            )}
          </article>
        ))}
      </section>
    </div>
  );
}
