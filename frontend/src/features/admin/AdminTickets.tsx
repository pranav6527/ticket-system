import { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  useGetAllTicketsQuery,
  useUpdateTicketMutation,
  type AdminTicket,
} from './adminApi';

const STATUSES = ['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'] as const;

export default function AdminTickets() {
  const { data: tickets, isLoading, error } = useGetAllTicketsQuery();
  const [updateTicket, { isLoading: saving }] = useUpdateTicketMutation();

  const [editingId, setEditingId] = useState<number | null>(null);
  const [form, setForm] = useState({
    subject: '',
    description: '',
    status: 'OPEN',
  });

  const startEdit = (ticket: AdminTicket) => {
    setEditingId(ticket.id);
    setForm({
      subject: ticket.subject,
      description: ticket.description,
      status: ticket.status,
    });
  };

  const cancelEdit = () => {
    setEditingId(null);
  };

  const saveEdit = async () => {
    if (editingId == null) return;
    await updateTicket({
      id: editingId,
      subject: form.subject,
      description: form.description,
      status: form.status,
    }).unwrap();
    setEditingId(null);
  };

  const renderError = () => {
    if (!error) return null;
    return (
      <div className="rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-700">
        Unable to load admin tickets. You may not have access.
      </div>
    );
  };

  return (
    <div className="min-h-screen bg-slate-50 p-4">
      <div className="mx-auto max-w-4xl">
        <div className="mb-6 flex flex-col items-start justify-between gap-3 sm:flex-row sm:items-center">
          <div>
            <h1 className="text-2xl font-semibold text-slate-800">
              Admin Tickets
            </h1>
            <p className="text-sm text-slate-500">
              Review and update tickets across all users.
            </p>
          </div>

          <Link
            to="/tickets"
            className="rounded-lg border border-slate-200 bg-white px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100"
          >
            Back to My Tickets
          </Link>
        </div>

        {isLoading && (
          <p className="text-center text-slate-500">Loading tickets...</p>
        )}

        {renderError()}

        {!isLoading && tickets?.length === 0 && (
          <p className="text-center text-slate-500">No tickets found.</p>
        )}

        <div className="space-y-4">
          {tickets?.map((ticket) => {
            const isEditing = editingId === ticket.id;
            return (
              <div
                key={ticket.id}
                className="rounded-xl bg-white p-4 shadow-sm"
              >
                <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                  <div className="flex-1">
                    <div className="text-xs font-semibold uppercase tracking-wide text-slate-400">
                      #{ticket.id} • {ticket.userEmail}
                    </div>

                    {isEditing ? (
                      <div className="mt-3 grid gap-3">
                        <input
                          value={form.subject}
                          onChange={(e) =>
                            setForm((prev) => ({
                              ...prev,
                              subject: e.target.value,
                            }))
                          }
                          className="h-10 rounded-lg border px-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                          placeholder="Subject"
                        />
                        <textarea
                          value={form.description}
                          onChange={(e) =>
                            setForm((prev) => ({
                              ...prev,
                              description: e.target.value,
                            }))
                          }
                          rows={3}
                          className="resize-none rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                          placeholder="Description"
                        />
                      </div>
                    ) : (
                      <div className="mt-2">
                        <p className="text-base font-semibold text-slate-800">
                          {ticket.subject}
                        </p>
                        <p className="mt-1 text-sm text-slate-600">
                          {ticket.description}
                        </p>
                      </div>
                    )}
                  </div>

                  <div className="flex flex-col items-start gap-2 sm:items-end">
                    {isEditing ? (
                      <div className="flex gap-2">
                        <button
                          onClick={saveEdit}
                          disabled={saving}
                          className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700 disabled:opacity-50"
                        >
                          Save
                        </button>
                        <button
                          onClick={cancelEdit}
                          className="rounded-lg border border-slate-200 bg-white px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100"
                        >
                          Cancel
                        </button>
                      </div>
                    ) : (
                      <button
                        onClick={() => startEdit(ticket)}
                        className="rounded-lg border border-blue-200 bg-blue-50 px-4 py-2 text-sm font-semibold text-blue-700 hover:bg-blue-100"
                      >
                        Edit
                      </button>
                    )}

                    {isEditing ? (
                      <select
                        value={form.status}
                        onChange={(e) =>
                          setForm((prev) => ({
                            ...prev,
                            status: e.target.value,
                          }))
                        }
                        className="h-9 rounded-lg border px-3 text-xs font-semibold text-slate-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                      >
                        {STATUSES.map((status) => (
                          <option key={status} value={status}>
                            {status}
                          </option>
                        ))}
                      </select>
                    ) : (
                      <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-600">
                        {ticket.status}
                      </span>
                    )}
                  </div>
                </div>

                <div className="mt-3 text-xs text-slate-400">
                  Created: {ticket.createdAt} • Updated: {ticket.updatedAt}
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
