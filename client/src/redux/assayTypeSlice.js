import {createSlice} from "@reduxjs/toolkit";

export const assayTypeSlice = createSlice({
  name: "assayTypes",
  initialState: {
    value: []
  },
  reducers: {
    setAssayTypes: (state, action) => {
      state.value = action.payload
    }
  }
})

export const { setAssayTypes } = assayTypeSlice.actions;

export default assayTypeSlice.reducer;