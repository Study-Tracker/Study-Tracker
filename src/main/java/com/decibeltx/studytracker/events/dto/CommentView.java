package com.decibeltx.studytracker.events.dto;

import com.decibeltx.studytracker.model.Comment;
import java.util.Date;
import lombok.Data;

@Data
public final class CommentView {

  private String text;

  private Date date;

  private String user;

  public static CommentView from(Comment comment) {
    CommentView view = new CommentView();
    view.setText(comment.getText());
    view.setDate(comment.getUpdatedAt() != null ? comment.getUpdatedAt() : comment.getCreatedAt());
    view.setUser(comment.getCreatedBy().getDisplayName());
    return view;
  }

}
