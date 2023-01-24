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

package io.studytracker.aws;

public class AWSAttributes {

  public static final String REGION = "_aws.region";

  public static final String S3_BUCKET = "_aws.s3.bucket";
  public static final String S3_PREFIX = "_aws.s3.prefix";
  public static final String S3_REGION = "_aws.s3.region";
  public static final String S3_KEY = "_aws.s3.key";
  public static final String S3_ARN = "_aws.s3.arn";

  public static final String EVENTBRIDGE_BUS = "_aws.eventbridge.bus";
  public static final String EVENTBRIDGE_ARN = "_aws.eventbridge.arn";
  public static final String EVENTBRIDGE_REGION = "_aws.eventbridge.region";

}
