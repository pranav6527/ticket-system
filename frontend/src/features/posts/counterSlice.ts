import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';

export interface CounterState {
  value: number;
}

const initialState: CounterState = {
  value: 0,
};

export const counterSlice = createSlice({
  name: 'counter',
  initialState,
  reducers: {
    increament: (state) => {
      state.value += 1;
    },
    decreament: (state) => {
      state.value -= 1;
    },
    increamentByNumber: (state, action: PayloadAction<number>) => {
      state.value += action.payload;
    },
  },
});

export const { increament, decreament, increamentByNumber } =
  counterSlice.actions;

export default counterSlice.reducer;
