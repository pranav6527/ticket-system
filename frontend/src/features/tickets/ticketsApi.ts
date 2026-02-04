import { baseApi } from '../../app/baseApi';

export type Ticket = {
  id: number;
  subject: string;
  status: string;
  description: string;
};

export const ticketsApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getTickets: builder.query<Ticket[], void>({
      query: () => '/tickets',
      providesTags: ['Ticket'],
    }),

    createTicket: builder.mutation<
      void,
      { subject: string; description: string }
    >({
      query: (body) => ({
        url: '/tickets',
        method: 'POST',
        body,
      }),
      invalidatesTags: ['Ticket'],
    }),
  }),
});

export const { useGetTicketsQuery, useCreateTicketMutation } = ticketsApi;
