/*
 * Copyright 2022 the original author or authors.
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

package io.studytracker.test.aws;

import io.studytracker.Application;
import io.studytracker.aws.S3Utils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"aws-test"})
public class S3ClientTests {

  @Value("${aws.example-s3-bucket}")
  private String bucketName;

  @Autowired(required = false)
  private S3Client s3Client;

  @Test
  public void pathBuilderTest() throws Exception {
    System.out.println(S3Utils.joinS3Path("path/to/thing", "file.txt"));
    System.out.println(S3Utils.joinS3Path("path/to/thing/", "file.txt"));
    System.out.println(S3Utils.joinS3Path("/path/to/thing/", "file.txt"));
    System.out.println(S3Utils.joinS3Path("/path/to/thing/", "/file.txt"));
    System.out.println(S3Utils.joinS3Path("path/to/thing", "new_folder"));
    System.out.println(S3Utils.joinS3Path("", "file.txt"));
    System.out.println(S3Utils.joinS3Path("", "new_folder/"));
  }

  @Test
  public void configTest() throws Exception {
    Assert.assertNotNull(s3Client);
  }

  @Test
  public void listRootFolder() throws Exception {
    ListObjectsV2Request request = ListObjectsV2Request.builder()
        .bucket(bucketName)
        .prefix("")
        .delimiter("/")
        .build();
    ListObjectsV2Response response = s3Client.listObjectsV2(request);
    System.out.println(response.toString());
    Assert.assertTrue(response.contents().size() > 0);
    response.contents().forEach(object -> {
      System.out.println(object.toString());
    });
  }

  @Test
  public void listFolderContentsTest() throws Exception {

    // List contents of folder
    ListObjectsV2Request request = ListObjectsV2Request.builder()
        .bucket(bucketName)
        .prefix("test/")
        .delimiter("/")
        .build();
    ListObjectsV2Response response = s3Client.listObjectsV2(request);
    System.out.println(response.toString());
    Assert.assertTrue(response.contents().size() > 0);
    response.contents().forEach(object -> {
      System.out.println(object.toString());
    });

  }

  @Test
  public void objectExistsTest() throws Exception {

    ListObjectsV2Request request = ListObjectsV2Request.builder()
        .bucket(bucketName)
        .prefix("test/test1.txt")
        .delimiter("/")
        .build();
    ListObjectsV2Response response = s3Client.listObjectsV2(request);
    System.out.println(response.toString());
    Assert.assertEquals(response.contents().size(), 1);
    response.contents().forEach(object -> {
      System.out.println(object.toString());
    });

    request = ListObjectsV2Request.builder()
        .bucket(bucketName)
        .prefix("test/doesnt-exist.txt")
        .delimiter("/")
        .build();
    response = s3Client.listObjectsV2(request);
    Assert.assertEquals(response.contents().size(), 0);
    System.out.println(response.toString());

    request = ListObjectsV2Request.builder()
        .bucket(bucketName)
        .prefix("doesnt-exist/")
        .build();
    response = s3Client.listObjectsV2(request);
    Assert.assertEquals(response.keyCount().intValue(), 0);
    System.out.println(response.toString());

    request = ListObjectsV2Request.builder()
        .bucket(bucketName)
        .prefix("test/")
        .build();
    response = s3Client.listObjectsV2(request);
    Assert.assertTrue(response.keyCount() > 0);
    System.out.println(response.toString());

  }

  @Test
  public void fetchObjectInfoTest() throws Exception {

    // Get file info
    HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
        .bucket(bucketName)
        .key("test/test1.txt")
        .build();
    HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
    Assert.assertTrue(headObjectResponse.contentLength() > 0);
    System.out.println(headObjectResponse.toString());

    headObjectRequest = HeadObjectRequest.builder()
        .bucket(bucketName)
        .key("test/")
        .build();
    headObjectResponse = s3Client.headObject(headObjectRequest);
    System.out.println(headObjectResponse.toString());
    Assert.assertTrue(headObjectResponse.contentLength() == 0);

    Exception exception = null;
    try {
      headObjectRequest = HeadObjectRequest.builder()
          .bucket(bucketName)
          .key("test/does-not-exist.txt")
          .build();
      s3Client.headObject(headObjectRequest);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof NoSuchKeyException);

  }

  @Test
  public void createFolderTest() throws Exception {
    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucketName)
        .key("test/create-test/")
        .build();
    PutObjectResponse response = s3Client.putObject(request, RequestBody.empty());
    Assert.assertNotNull(response);
    Assert.assertNotNull(response.eTag());
    System.out.println(response.toString());
  }

  @Test
  public void uploadTest() throws Exception {
    ClassPathResource resource = new ClassPathResource("upload-test.txt");
    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucketName)
        .key("test/create-test/" + resource.getFilename())
        .build();
    PutObjectResponse response = s3Client.putObject(request, RequestBody.fromFile(resource.getFile()));
    Assert.assertNotNull(response);
    Assert.assertNotNull(response.eTag());
    System.out.println(response.toString());
  }

}
