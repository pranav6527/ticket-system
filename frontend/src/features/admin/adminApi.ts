import { baseApi } from '../../app/baseApi';

export type AdminTicket = {
  id: number;
  userEmail: string;
  subject: string;
  status: string;
  description: string;
  createdAt: string;
  updatedAt: string;
};

export const adminApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getAllTickets: builder.query<AdminTicket[], void>({
      query: () => '/admin/tickets',
      providesTags: ['Ticket'],
    }),

    updateTicket: builder.mutation<
      AdminTicket,
      { id: number; subject?: string; description?: string; status?: string }
    >({
      query: ({ id, ...body }) => ({
        url: `/admin/tickets/${id}`,
        method: 'PUT',
        body,
      }),
      invalidatesTags: ['Ticket'],
    }),
  }),
});

export const { useGetAllTicketsQuery, useUpdateTicketMutation } = adminApi;
