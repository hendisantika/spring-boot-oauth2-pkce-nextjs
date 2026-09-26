"use client";

import { useActionState, useEffect, useRef } from "react";
import type { NoteFormState } from "./actions";

type Props = {
  action: (state: NoteFormState, formData: FormData) => Promise<NoteFormState>;
  defaultValues?: { title: string; content: string | null };
  submitLabel: string;
};

export function NoteForm({ action, defaultValues, submitLabel }: Props) {
  const [state, formAction, pending] = useActionState(action, undefined);
  const formRef = useRef<HTMLFormElement>(null);

  useEffect(() => {
    if (state?.ok && !defaultValues) formRef.current?.reset();
  }, [state, defaultValues]);

  return (
    <form ref={formRef} action={formAction} className="space-y-3">
      <div>
        <input name="title" placeholder="Title" defaultValue={defaultValues?.title}
               aria-label="Title" className="input" />
        {state?.errors?.title?.map((e) => <p key={e} className="field-error">{e}</p>)}
      </div>
      <div>
        <textarea name="content" placeholder="Write something..." rows={4}
                  defaultValue={defaultValues?.content ?? ""} aria-label="Content" className="input" />
        {state?.errors?.content?.map((e) => <p key={e} className="field-error">{e}</p>)}
      </div>
      {state?.message && !state.errors && <p className="field-error">{state.message}</p>}
      <button type="submit" disabled={pending} className="btn-primary">
        {pending ? "Saving..." : submitLabel}
      </button>
    </form>
  );
}
