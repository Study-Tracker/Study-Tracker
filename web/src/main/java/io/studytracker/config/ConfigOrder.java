/*
 * Copyright 2019-2023 the original author or authors.
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

package io.studytracker.config;

public class ConfigOrder {

  public static final int SERVICE_INIT = 0;
  public static final int PROFILE_INIT = 100;
  public static final int DATA_INIT = 200;
  public static final int EXAMPLE_DATA_INIT = 500;
  public static final int AFTER_INIT = 1000;

}
