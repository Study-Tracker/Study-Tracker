package com.decibeltx.studytracker.events.dto;

import com.decibeltx.studytracker.storage.StorageFile;
import lombok.Data;

@Data
public final class StorageFileView {

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
