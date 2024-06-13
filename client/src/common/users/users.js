/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


export const getUserInitials = (user) => {
  return user.displayName.split(" ").map(name => name.charAt(0)).join("");
}

export const getLabelClass = (i) => {
  switch (i % 7) {
    case 0:
      return "team-member-primary";
    case 1:
      return "team-member-success";
    case 2:
      return "team-member-info";
    case 3:
      return "team-member-warning";
    case 4:
      return "team-member-danger";
    case 5:
      return "team-member-secondary";
    case 6:
      return "team-member-tertiary";
    default:
      return "team-member-primary"
  }
}