import { baseApi } from '../../app/baseApi';
import { setToken } from './authSlice';

export const authApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    login: builder.mutation<any, { email: string; password: string }>({
      query: (body) => ({
        url: '/auth/login',
        method: 'POST',
        body,
      }),
      async onQueryStarted(_, { dispatch, queryFulfilled }) {
        const { data } = await queryFulfilled;
        dispatch(setToken(data.accessToken));
      },
    }),

    signup: builder.mutation<any, { email: string; password: string }>({
      query: (body) => ({
        url: '/auth/signup',
        method: 'POST',
        body,
      }),
      async onQueryStarted(_, { dispatch, queryFulfilled }) {
        const { data } = await queryFulfilled;
        dispatch(setToken(data.accessToken));
      },
    }),

    me: builder.query<any, void>({
      query: () => '/auth/me',
    }),
  }),
});

export const { useLoginMutation, useSignupMutation, useMeQuery } = authApi;
