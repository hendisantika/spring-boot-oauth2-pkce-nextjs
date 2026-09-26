import type { Metadata } from "next";
import { ApiError, apiFetch, type User } from "@/lib/api";

export const metadata: Metadata = { title: "Admin · OAuth2 PKCE Notes" };

export default async function AdminPage() {
  let users: User[];
  try {
    users = await apiFetch<User[]>("/api/admin/users");
  } catch (error) {
    if (error instanceof ApiError && error.problem.status === 403) {
      return <p className="text-sm">You need the ADMIN role to view this page.</p>;
    }
    throw error;
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-semibold tracking-tight">Users</h1>
      <div className="overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead className="border-b border-black/10 text-black/60 dark:border-white/10 dark:text-white/60">
            <tr>
              <th className="py-2 pr-4 font-medium">Username</th>
              <th className="py-2 pr-4 font-medium">Email</th>
              <th className="py-2 pr-4 font-medium">Roles</th>
              <th className="py-2 font-medium">Status</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id} className="border-b border-black/5 dark:border-white/5">
                <td className="py-2 pr-4">{user.username}</td>
                <td className="py-2 pr-4">{user.email}</td>
                <td className="py-2 pr-4">{user.roles.join(", ")}</td>
                <td className="py-2">{user.enabled ? "Active" : "Disabled"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
