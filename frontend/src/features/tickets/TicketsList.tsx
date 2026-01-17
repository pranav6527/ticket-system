import { useState } from 'react';
import { useGetTicketsQuery, useCreateTicketMutation } from './ticketsApi';
import { nanoid } from 'nanoid';

export default function TicketsList() {
  const { data: tickets, isLoading } = useGetTicketsQuery();
  const [createTicket, { isLoading: creating }] = useCreateTicketMutation();

  const [subject, setSubject] = useState('');
  const [description, setDescription] = useState('');

  const onCreate = async () => {
    if (!subject.trim()) return;
    await createTicket({ subject, description });
    setSubject('');
    setDescription('');
  };

  return (
    <div className="min-h-screen bg-gray-100 p-4">
      <div className="mx-auto max-w-3xl">
        <h1 className="mb-4 text-2xl font-semibold">My Tickets</h1>

        {/* Create Ticket */}
        <div className="mb-6 rounded-xl bg-white p-4 shadow">
          <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
            <input
              placeholder="Subject"
              className="h-10 rounded-lg border px-3 text-sm focus:outline-none focus:ring"
              value={subject}
              onChange={(e) => setSubject(e.target.value)}
            />

            <textarea
              placeholder="Description"
              rows={3}
              className="resize-none rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring"
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
          {isLoading && <p>Loading tickets...</p>}

          {tickets?.length === 0 && (
            <p className="text-gray-500">No tickets yet</p>
          )}

          {tickets?.map((ticket: any, index: number) => (
            <div key={nanoid()} className="rounded-xl bg-white p-4 shadow">
              {/* Index + Subject */}
              <div className="flex items-center gap-3">
                <span className="flex h-7 w-7 items-center justify-center rounded-full bg-gray-100 text-sm font-medium text-gray-600">
                  {index + 1}
                </span>

                <p className="font-medium">{ticket.subject}</p>
              </div>

              {/* Description */}
              <p className="mt-2 text-sm text-gray-500">{ticket.description}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
