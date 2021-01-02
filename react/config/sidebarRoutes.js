/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from "react";
import {Activity, Clipboard, Layers, Target, Users} from "react-feather";

const timelineRoutes = {
  path: "/",
  name: "Activity",
  header: "Navigation",
  icon: Activity,
  containsHome: true,
  children: null
}

const studyRoutes = {
  path: "/studies?title=All Studies",
  name: "Studies",
  icon: Clipboard,
  children: [
    {
      path: "/studies?title=All Studies",
      name: "All Studies"
    },
    {
      path: "/studies?myStudy=true&title=My Studies",
      name: "My Studies",
      protected: true
    },
    {
      path: "/studies?status=IN_PLANNING,ACTIVE&title=Active Studies",
      name: "Active Studies"
    },
    {
      path: "/studies?legacy=true&title=Legacy Studies",
      name: "Legacy Studies"
    },
    {
      path: "/studies?external=true&title=External Studies",
      name: "External Studies"
    }
  ]
};

const assayRoutes = {
  path: "/assays",
  name: "Assays",
  icon: Layers,
  children: [
    {
      path: "/assays?title=All Assays",
      name: "All Assays"
    },
    {
      path: "/assays?myAssay=true&title=My Assays",
      name: "My Assays",
      protected: true
    },
    {
      path: "/assays?status=IN_PLANNING,ACTIVE&title=Active Assays",
      name: "Active Assays"
    },
    {
      path: "/assays?legacy=true&title=Legacy Assays",
      name: "Legacy Assays"
    },
    {
      path: "/assays?external=true&title=External Assays",
      name: "External Assays"
    }
  ]
};

const programRoutes = {
  path: "/programs",
  name: "Programs",
  icon: Target,
  children: [
    {
      path: '/programs?title=All Programs',
      name: 'All Programs'
    },
    {
      path: '/programs?myProgram=true&title=My Programs',
      name: 'My Programs',
      protected: true
    },
    {
      path: '/programs?active=true&title=Active Programs',
      name: 'Active Programs'
    },
    {
      path: '/programs?inactive=true&title=Legacy Programs',
      name: 'Legacy Programs'
    }
  ]
};

const userRoutes = {
  path: "/users",
  name: "Users",
  icon: Users,
  children: null
};

export default [
  timelineRoutes,
  studyRoutes,
  assayRoutes,
  programRoutes,
  userRoutes
]