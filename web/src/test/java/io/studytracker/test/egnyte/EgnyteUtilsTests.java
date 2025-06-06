package io.studytracker.test.egnyte;

import io.studytracker.egnyte.EgnyteUtils;
import org.junit.Assert;
import org.junit.Test;

public class EgnyteUtilsTests {

  @Test
  public void testDirectoryIsSubfolderOf() {
    Assert.assertTrue(EgnyteUtils.directoryIsSubfolderOf("/a/b", "/a/b"));
    Assert.assertTrue(EgnyteUtils.directoryIsSubfolderOf("/a/b", "/a/b/c"));
    Assert.assertTrue(EgnyteUtils.directoryIsSubfolderOf("/a/b/", "/a/b/c/"));
    Assert.assertFalse(EgnyteUtils.directoryIsSubfolderOf("/a/b", "/a/bc"));
    Assert.assertFalse(EgnyteUtils.directoryIsSubfolderOf("/a/b", "/a/bc/d"));
  }
}
