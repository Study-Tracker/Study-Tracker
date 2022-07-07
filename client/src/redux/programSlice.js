import {createSlice} from "@reduxjs/toolkit";

export const programSlice = createSlice({
  name: "programs",
  initialState: {
    value: []
  },
  reducers: {
    setPrograms: (state, action) => {
      state.value = action.payload
    }
  }
})

export const { setPrograms } = programSlice.actions;

export default programSlice.reducer;