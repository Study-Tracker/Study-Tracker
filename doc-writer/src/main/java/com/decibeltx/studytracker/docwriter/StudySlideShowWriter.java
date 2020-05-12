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

package com.decibeltx.studytracker.docwriter;

import com.decibeltx.studytracker.core.exception.StudyTrackerException;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.springframework.core.io.Resource;

public class StudySlideShowWriter {

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMMM dd, yyyy");

  private final Path tempDir;
  private final Resource template;

  public StudySlideShowWriter(Path tempDir, Resource template) {
    this.tempDir = tempDir;
    this.template = template;
  }

  public File writeStudySummaryDocument(Study study) {
    File outputFile = null;
    FileOutputStream output = null;
    try {

      // Get the Slideshow object
      XMLSlideShow ppt =
          template != null ? createSlideShowFromTemplate(template) : createNewSlideShow();

      // Write the content
      XSLFSlide titleSlide = ppt.getSlides().get(0);
      XSLFTextShape title = titleSlide.getPlaceholder(0);
      title.clearText();
      title.setText(study.getCode() + ": " + study.getName());
      XSLFTextShape body = titleSlide.getPlaceholder(1);
      body.clearText();
      body.addNewTextParagraph().addNewTextRun()
          .setText("Start Date: " + DATE_FORMAT.format(study.getStartDate()));
      body.addNewTextParagraph().addNewTextRun()
          .setText("Owner: " + study.getOwner().getDisplayName());
      body.addNewTextParagraph().addNewTextRun()
          .setText("Team: " + study.getUsers().stream()
              .map(User::getDisplayName)
              .collect(Collectors.joining(", ")));

      XSLFSlide contentSlide = ppt.getSlides().get(1);
      XSLFTextShape contentTitle = contentSlide.getPlaceholder(0);
      contentTitle.clearText();
      contentTitle.setText(study.getCode());
      XSLFTextShape contentBody = contentSlide.getPlaceholder(1);
      contentBody.clearText();
      contentBody.setText(study.getDescription());

      // Save the file
      Path filePath = tempDir.resolve(study.getCode() + ".pptx");
      output = new FileOutputStream(filePath.toString());
      outputFile = filePath.toFile();
      ppt.write(output);
      output.close();
    } catch (IOException e) {
      throw new StudyTrackerException(e);
    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException ex) {
          throw new StudyTrackerException(ex);
        }
      }
    }
    return outputFile;
  }

  private XMLSlideShow createSlideShowFromTemplate(Resource resource) throws IOException {
    return createSlideShowFromTemplate(resource.getInputStream());
  }

  private XMLSlideShow createSlideShowFromTemplate(InputStream inputStream) throws IOException {
    return new XMLSlideShow(inputStream);
  }

  private XMLSlideShow createNewSlideShow() {
    XMLSlideShow ppt = new XMLSlideShow();
    XSLFSlideMaster slideMaster = ppt.getSlideMasters().get(0);
    ppt.createSlide(slideMaster.getLayout(SlideLayout.TITLE));
    ppt.createSlide(slideMaster.getLayout(SlideLayout.TITLE_AND_CONTENT));
    return ppt;
  }

}
