package com.decibeltx.studytracker.core.model;

import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Document(collection = "notebook_entry_templates")
@Data
public class NotebookEntryTemplate implements Persistable<String> {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotNull(message = "Template name must not be empty")
    private String name;

    @Indexed(unique = true)
    @NotNull(message = "Template id must not be empty")
    private String templateId;

    @CreatedBy
    @Linked(model = NotebookEntryTemplate.class)
    @NotNull
    @DBRef
    private User createdBy;

    @LastModifiedBy
    @Linked(model = NotebookEntryTemplate.class)
    @NotNull
    @DBRef
    private User lastModifiedBy;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    private boolean active = true;

    public static NotebookEntryTemplate of(User user, String templateId,
                                           String name, Date timeStamp) {
        NotebookEntryTemplate notebookEntryTemplate = new NotebookEntryTemplate();
        notebookEntryTemplate.setTemplateId(templateId);
        notebookEntryTemplate.setName(name);
        notebookEntryTemplate.setCreatedBy(user);
        notebookEntryTemplate.setLastModifiedBy(user);
        notebookEntryTemplate.setCreatedAt(timeStamp);
        notebookEntryTemplate.setUpdatedAt(timeStamp);
        return notebookEntryTemplate;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
}
