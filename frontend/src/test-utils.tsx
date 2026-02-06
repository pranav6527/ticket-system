import React, { PropsWithChildren } from 'react';
import { configureStore, PreloadedState } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { render, RenderOptions } from '@testing-library/react';
import authReducer from './features/auth/authSlice';
import { baseApi } from './app/baseApi';
import type { RootState } from './app/store';

export const createTestStore = (preloadedState?: PreloadedState<RootState>) =>
  configureStore({
    reducer: {
      auth: authReducer,
      [baseApi.reducerPath]: baseApi.reducer,
    },
    middleware: (getDefault) => getDefault().concat(baseApi.middleware),
    preloadedState,
  });

type ExtendedRenderOptions = Omit<RenderOptions, 'queries'> & {
  preloadedState?: PreloadedState<RootState>;
  store?: ReturnType<typeof createTestStore>;
};

export const renderWithProviders = (
  ui: React.ReactElement,
  { preloadedState, store = createTestStore(preloadedState), ...options }: ExtendedRenderOptions = {}
) => {
  const Wrapper = ({ children }: PropsWithChildren) => (
    <Provider store={store}>{children}</Provider>
  );

  return { store, ...render(ui, { wrapper: Wrapper, ...options }) };
};
