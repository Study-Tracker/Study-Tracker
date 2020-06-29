import {
  faCheckCircle,
  faExclamationTriangle,
} from "@fortawesome/free-solid-svg-icons";

export const statuses = {
  "ACTIVE": {
    icon: faCheckCircle,
    color: "success",
    label: "Active",
    value: "ACTIVE"
  },
  "INACTIVE": {
    icon: faExclamationTriangle,
    color: "warning",
    label: "Inactive",
    value: "INACTIVE"
  }
};