package com.decibeltx.studytracker.service;

import lombok.Data;

@Data
public class NamingOptions {

  /**
   * study.study-code-counter-start
   */
  private Integer studyCodeCounterStart = 10001;

  /**
   * study.study-code-min-digits
   */
  private Integer studyCodeMinimumDigits = 5;

  /**
   * study.assay-code-counter-start
   */
  private Integer assayCodeCounterStart = 1;

  /**
   * study.assay-code-min-digits
   */
  private Integer assayCodeMinimumDigits = 3;

  /**
   * study.external-code-counter-start
   */
  private Integer externalStudyCodeCounterStart = 1;

  /**
   * study.external-code-min-digits
   */
  private Integer externalStudyCodeMinimumDigits = 5;

}
