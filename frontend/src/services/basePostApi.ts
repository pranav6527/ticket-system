import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

export const basePostApi = createApi({
  reducerPath: 'api',
  baseQuery: fetchBaseQuery({
    baseUrl: 'https://jsonplaceholder.typicode.com',
  }),
  tagTypes: ['Post'],
  endpoints: () => ({}),
});
