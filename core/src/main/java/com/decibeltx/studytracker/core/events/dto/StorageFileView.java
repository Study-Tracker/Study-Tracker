package com.decibeltx.studytracker.core.events.dto;

import com.decibeltx.studytracker.core.storage.StorageFile;
import lombok.Data;

@Data
public class StorageFileView {

  private String name;

  private String path;

  private String url;

  public static StorageFileView from(StorageFile storageFile) {
    StorageFileView view = new StorageFileView();
    view.setName(storageFile.getName());
    view.setPath(storageFile.getPath());
    view.setUrl(storageFile.getUrl());
    return view;
  }

}
