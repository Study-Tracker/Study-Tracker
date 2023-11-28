import {createSlice} from "@reduxjs/toolkit";

export const tabSlice = createSlice({
  name: "tab",
  initialState: {
    value: null
  },
  reducers: {
    setTab: (state, action) => {
      state.value = action.payload
    }
  }
});

export const {setTab} = tabSlice.actions;

export default tabSlice.reducer;