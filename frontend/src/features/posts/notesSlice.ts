import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';

export interface NoteState {
  notes: string[];
}

const initialState: NoteState = {
  notes: [],
};

export const notesSlice = createSlice({
  name: 'notes',
  initialState,
  reducers: {
    addNote: (state, action: PayloadAction<string>) => {
      state.notes.push(action.payload);
    },
  },
});

export const { addNote } = notesSlice.actions;

export default notesSlice.reducer;
