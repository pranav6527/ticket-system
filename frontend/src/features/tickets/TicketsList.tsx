import { useState } from 'react';
import { useGetTicketsQuery, useCreateTicketMutation } from './ticketsApi';
import { useLogoutMutation } from '../auth/authApi';
import { nanoid } from 'nanoid';
import { LogOut } from 'lucide-react'; // Added Lucide icon import

export default function TicketsList() {
  const { data: tickets, isLoading } = useGetTicketsQuery();
  const [createTicket, { isLoading: creating }] = useCreateTicketMutation();
  const [logout] = useLogoutMutation();

  const [subject, setSubject] = useState('');
  const [description, setDescription] = useState('');

  const onCreate = async () => {
    if (!subject.trim()) return;
    await createTicket({ subject, description });
    setSubject('');
    setDescription('');
  };

  const handleLogout = async () => {
    try {
      await logout().unwrap();
      // Optionally, you can redirect the user to the login page here
    } catch (err) {
      console.error('Logout failed', err);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-4">
      <div className="mx-auto max-w-3xl">
        {/* Header Section */}
        <div className="mb-6 flex flex-col items-start justify-between gap-4 sm:flex-row sm:items-center">
          <h1 className="text-2xl font-semibold text-gray-800">My Tickets</h1>

          {/* Responsive Red Logout Button */}
          <button
            onClick={handleLogout}
            className="group flex w-full items-center justify-center gap-2 rounded-lg border border-red-200 bg-red-50 px-4 py-2 text-sm font-semibold text-red-600 transition-all hover:bg-red-600 hover:text-white active:scale-95 sm:w-auto"
          >
            <LogOut
              size={18}
              className="transition-colors group-hover:text-white"
            />
            <span>Logout</span>
          </button>
        </div>

        {/* Create Ticket */}
        <div className="mb-6 rounded-xl bg-white p-4 shadow">
          <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
            <input
              placeholder="Subject"
              className="h-10 rounded-lg border px-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={subject}
              onChange={(e) => setSubject(e.target.value)}
            />

            <textarea
              placeholder="Description"
              rows={3}
              className="resize-none rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          </div>

          <div className="mt-4 flex justify-end">
            <button
              onClick={onCreate}
              disabled={creating}
              className="w-full rounded-lg bg-blue-600 px-6 py-2 text-white transition hover:bg-blue-700 disabled:opacity-50 md:w-auto"
            >
              Add Ticket
            </button>
          </div>
        </div>

        {/* Tickets List */}
        <div className="space-y-3">
          {isLoading && (
            <p className="text-center text-gray-500">Loading tickets...</p>
          )}

          {tickets?.length === 0 && !isLoading && (
            <p className="text-center text-gray-500">No tickets yet</p>
          )}

          {tickets?.map((ticket: any, index: number) => (
            <div
              key={nanoid()}
              className="rounded-xl bg-white p-4 shadow transition hover:shadow-md"
            >
              {/* Index + Subject */}
              <div className="flex items-center gap-3">
                <span className="flex h-7 w-7 items-center justify-center rounded-full bg-blue-50 text-xs font-bold text-blue-600">
                  {index + 1}
                </span>

                <p className="font-semibold text-gray-800">{ticket.subject}</p>
              </div>

              {/* Description */}
              <p className="mt-2 text-sm text-gray-600">{ticket.description}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
