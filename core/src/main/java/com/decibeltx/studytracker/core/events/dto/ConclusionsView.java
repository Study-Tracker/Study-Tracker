package com.decibeltx.studytracker.core.events.dto;

import com.decibeltx.studytracker.core.model.Conclusions;
import java.util.Date;
import lombok.Data;

@Data
public final class ConclusionsView {

  private String content;

  private Date date;

  private String user;

  public static ConclusionsView from(Conclusions conclusions) {
    ConclusionsView view = new ConclusionsView();
    view.setContent(conclusions.getContent());
    view.setDate(conclusions.getUpdatedAt() != null
        ? conclusions.getUpdatedAt() : conclusions.getCreatedAt());
    view.setUser(conclusions.getLastModifiedBy() != null
        ? conclusions.getLastModifiedBy().getDisplayName()
        : conclusions.getCreatedBy().getDisplayName());
    return view;
  }

}
