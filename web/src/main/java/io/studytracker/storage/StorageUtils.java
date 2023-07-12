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

package io.studytracker.storage;

import java.io.File;
import java.util.Arrays;

public class StorageUtils {

  /**
   * Joins two paths into a single path.
   *
   * @param path the first path
   * @param name the second path or file name to join
   * @return the joined path
   */
  public static String joinPath(String path, String name) {
    if (!path.endsWith("/")) {
      path += "/";
    }
    return path + name;
  }

  /**
   * Determines whether the given paths are equivalent.
   *
   * @param path1 the first path
   * @param path2 the second path
   * @return true if the paths are equivalent, false otherwise
   */
  public static boolean comparePaths(String path1, String path2) {
    if (!path1.startsWith("/")) path1 = "/" + path1;
    if (!path1.endsWith("/")) path1 += "/";
    if (!path2.startsWith("/")) path2 = "/" + path2;
    if (!path2.endsWith("/")) path2 += "/";
    String slug1 = String.join("/", (Arrays.asList(path1.toLowerCase().split("/"))));
    String slug2 = String.join("/", (Arrays.asList(path2.toLowerCase().split("/"))));
    return slug1.equals(slug2);
  }

  public static String normalizePath(String path) {
    if (!path.startsWith("/")) path = "/" + path;
    if (!path.endsWith("/")) path += "/";
    return path;
  }

  public static String cleanInputPath(String path) {
    if (path == null) return "";
    return path.replaceAll("\\.\\.", ".");
  }

  public static String getFolderNameFromPath(String path) {
    File file = new File(cleanInputPath(path));
    return file.getName();
  }

  public static String getParentPathFromPath(String path) {
    File file = new File(cleanInputPath(path));
    return file.getParent();
  }

  public static boolean pathIsChildOf(String parentPath, String childPath) {
    if (childPath == null || parentPath == null) return false;
    if (childPath.equals(parentPath)) return true;
    return parentPath.endsWith(childPath);
  }

}
