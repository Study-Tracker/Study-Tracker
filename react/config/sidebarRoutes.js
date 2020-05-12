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
import {Clipboard, Droplet, GitHub, Layers, Target, Users} from "react-feather";

const studyRoutes = {
  path: "/",
  name: "Studies",
  header: "Navigation",
  icon: Clipboard,
  containsHome: true,
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
      path: "/assays",
      name: "All Assays"
    },
    {
      path: "/assays/my",
      name: "My Assays",
      protected: true
    },
    {
      path: "/assays/active",
      name: "Active Assays"
    },
    {
      path: "/assays/legacy",
      name: "Legacy Assays"
    },
    {
      path: "/assays/external",
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
      path: '/programs',
      name: 'All Programs'
    },
    {
      path: '/programs?my=true',
      name: 'My Programs',
      protected: true
    },
    {
      path: '/programs?active=true',
      name: 'Active Programs'
    },
    {
      path: '/programs?legacy=true',
      name: 'Legacy Programs'
    }
  ]
};

const animalRoutes = {
  path: "/animals",
  name: "Animals",
  icon: GitHub,
  children: null
};

const sampleRoutes = {
  path: "/samples",
  name: "Samples",
  icon: Droplet,
  children: null
};

const userRoutes = {
  path: "/users",
  name: "Users",
  icon: Users,
  children: null
};

export default [
  studyRoutes,
  assayRoutes,
  programRoutes,
  animalRoutes,
  sampleRoutes,
  userRoutes
]