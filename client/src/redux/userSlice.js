import {createSlice} from "@reduxjs/toolkit";

export const userSlice = createSlice({
  name: "user",
  initialState: {
    value: null
  },
  reducers: {
    setSignedInUser: (state, action) => {
      state.value = action.payload
    }
  }
})

export const { setSignedInUser } = userSlice.actions;

export default userSlice.reducer;