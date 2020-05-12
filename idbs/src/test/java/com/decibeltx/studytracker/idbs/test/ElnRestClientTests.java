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

package com.decibeltx.studytracker.idbs.test;

import com.decibeltx.studytracker.idbs.eln.IdbsElnOptions;
import com.decibeltx.studytracker.idbs.eln.IdbsRestElnClient;
import com.decibeltx.studytracker.idbs.eln.entities.IdbsNotebookEntry;
import com.decibeltx.studytracker.idbs.exception.EntityNotFoundException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class ElnRestClientTests {

  @Autowired
  private IdbsRestElnClient client;

  @Autowired
  private IdbsElnOptions options;

  @Test
  public void findItemByIdTest() throws Exception {
    Exception exception = null;
    IdbsNotebookEntry entry = null;
    try {
      entry = client.findEntityById(options.getRootEntity());
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(entry);
    Assert.assertEquals(options.getRootEntity(), entry.getEntityId());

    try {
      entry = client.findEntityById("xxx");
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof EntityNotFoundException);
  }

  @Test
  public void findEntityChildrenTest() throws Exception {
    Exception exception = null;
    List<IdbsNotebookEntry> entries = null;
    try {
      entries = client.findEntityChildren(options.getRootEntity());
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(entries);
    Assert.assertFalse(entries.isEmpty());

    try {
      entries = client.findEntityChildren("xxx");
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof EntityNotFoundException);
  }

}
