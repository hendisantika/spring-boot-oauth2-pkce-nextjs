"use client";

import { useActionState } from "react";
import { register } from "./actions";

const fields = [
  { name: "username", label: "Username", type: "text", autoComplete: "username" },
  { name: "email", label: "Email", type: "email", autoComplete: "email" },
  { name: "fullName", label: "Full name (optional)", type: "text", autoComplete: "name" },
  { name: "password", label: "Password", type: "password", autoComplete: "new-password" },
] as const;

export function RegisterForm() {
  const [state, action, pending] = useActionState(register, undefined);

  return (
    <form action={action} className="space-y-4">
      {state?.message && Object.keys(state.errors ?? {}).length === 0 && (
        <p className="field-error text-sm">{state.message}</p>
      )}
      {fields.map((field) => (
        <div key={field.name}>
          <label htmlFor={field.name} className="mb-1 block text-sm font-medium">
            {field.label}
          </label>
          <input id={field.name} name={field.name} type={field.type}
                 autoComplete={field.autoComplete} className="input" />
          {state?.errors?.[field.name]?.map((error) => (
            <p key={error} className="field-error">{error}</p>
          ))}
        </div>
      ))}
      <button type="submit" disabled={pending} className="btn-primary w-full">
        {pending ? "Creating account..." : "Create account"}
      </button>
    </form>
  );
}
