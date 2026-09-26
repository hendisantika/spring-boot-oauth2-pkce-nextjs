import type { Metadata } from "next";
import { RegisterForm } from "./register-form";

export const metadata: Metadata = { title: "Register · OAuth2 PKCE Notes" };

export default function RegisterPage() {
  return (
    <div className="mx-auto max-w-sm space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Create an account</h1>
        <p className="text-sm text-black/60 dark:text-white/60">
          After registering you will sign in on the authorization server.
        </p>
      </div>
      <RegisterForm />
    </div>
  );
}
