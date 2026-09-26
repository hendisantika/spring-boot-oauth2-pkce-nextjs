import Link from "next/link";
import { notFound } from "next/navigation";
import { ApiError, apiFetch, type Note } from "@/lib/api";
import { updateNote } from "../actions";
import { NoteForm } from "../note-form";

export default async function EditNotePage({ params }: PageProps<"/notes/[id]">) {
  const { id } = await params;
  const noteId = Number(id);
  if (!Number.isInteger(noteId)) notFound();

  let note: Note;
  try {
    note = await apiFetch<Note>(`/api/notes/${noteId}`);
  } catch (error) {
    if (error instanceof ApiError && error.problem.status === 404) notFound();
    throw error;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold tracking-tight">Edit note</h1>
        <Link href="/notes" className="text-sm hover:underline">Back to notes</Link>
      </div>
      <div className="card">
        <NoteForm action={updateNote.bind(null, note.id)} defaultValues={note} submitLabel="Save changes" />
      </div>
    </div>
  );
}
