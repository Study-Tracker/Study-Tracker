package com.decibeltx.studytracker.benchling.eln.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/*
{
  "archiveRecord": null,
  "id": "src_YzU5p4dR",
  "name": "Quality Control",
  "owner": {
    "handle": "lpasteur",
    "id": "ent_jDKampO5",
    "name": "Louis Pasteur"
  }
}
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BenchlingProject {

  private Object archiveRecord;

  private String id;

  private String name;

  private BenchlingUser owner;

}
