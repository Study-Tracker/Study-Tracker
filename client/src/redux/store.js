import {configureStore} from '@reduxjs/toolkit'
import userReducer from "./userSlice";
import sidebarReducer from './sidebarSlice';
import featuresReducer from './featuresSlice';
import programReducer from './programSlice';
import filterReducer from "./filterSlice";
import assayTypeReducer from "./assayTypeSlice";

export default configureStore({
  reducer: {
    user: userReducer,
    sidebar: sidebarReducer,
    features: featuresReducer,
    programs: programReducer,
    filters: filterReducer,
    assayTypes: assayTypeReducer
  },
})