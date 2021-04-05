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

package com.decibeltx.studytracker.egnyte.rest;

import com.decibeltx.studytracker.egnyte.entity.EgnyteFile;
import com.decibeltx.studytracker.egnyte.entity.EgnyteFolder;
import com.decibeltx.studytracker.egnyte.entity.EgnyteObject;
import com.decibeltx.studytracker.exception.StudyTrackerException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EgnyteObjectDeserializer extends StdNodeBasedDeserializer<EgnyteObject> {

  private final URL rootUrl;

  public EgnyteObjectDeserializer(URL rootUrl) {
    super(EgnyteObject.class);
    this.rootUrl = rootUrl;
  }

  @Override
  public EgnyteObject convert(JsonNode root, DeserializationContext deserializationContext)
      throws IOException {
    Type target;
    if (root.has("is_folder") && root.get("is_folder").asBoolean()) {
      target = EgnyteFolder.class;
    } else {
      target = EgnyteFile.class;
    }
    JavaType jacksonType = deserializationContext.getTypeFactory().constructType(target);
    JsonDeserializer<?> deserializer = deserializationContext
        .findRootValueDeserializer(jacksonType);
    JsonParser nodeParser = root.traverse(deserializationContext.getParser().getCodec());
    nodeParser.nextToken();
    EgnyteObject object = (EgnyteObject) deserializer
        .deserialize(nodeParser, deserializationContext);
    if (object.isFolder()) {
      EgnyteFolder folder = (EgnyteFolder) object;
      Path path = Paths.get("/navigate/folder/", folder.getFolderId());
      try {
        folder.setUrl(new URL(rootUrl, path.toString()));
      } catch (MalformedURLException e) {
        throw new StudyTrackerException(e);
      }
      for (EgnyteFolder subFolder : folder.getSubFolders()) {
        path = Paths.get("/navigate/folder/", subFolder.getFolderId());
        try {
          subFolder.setUrl(new URL(rootUrl, path.toString()));
        } catch (MalformedURLException e) {
          throw new StudyTrackerException(e);
        }
      }
      for (EgnyteFile file : folder.getFiles()) {
        path = Paths.get("/navigate/file/", file.getGroupId());
        try {
          file.setUrl(new URL(rootUrl, path.toString()));
        } catch (MalformedURLException e) {
          throw new StudyTrackerException(e);
        }
      }
      return folder;
    } else {
      EgnyteFile file = (EgnyteFile) object;
      Path path = Paths.get("/navigate/file/", file.getGroupId());
      try {
        file.setUrl(new URL(rootUrl, path.toString()));
      } catch (MalformedURLException e) {
        throw new StudyTrackerException(e);
      }
      return file;
    }
  }

}
