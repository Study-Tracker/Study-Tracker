import {createSlice} from "@reduxjs/toolkit";

export const featuresSlice = createSlice({
  name: "features",
  initialState: {
    value: {}
  },
  reducers: {
    setFeatures: (state, action) => {
      state.value = action.payload
    }
  }
})

export const { setFeatures } = featuresSlice.actions;

export default featuresSlice.reducer;