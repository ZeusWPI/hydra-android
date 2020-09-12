/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.ugent.zeus.hydra.licenses;

import static org.gradle.internal.impldep.org.testng.Assert.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for {@link LicensesTask} */
@RunWith(JUnit4.class)
public class LicensesTaskTest {
  private static final Charset UTF_8 = StandardCharsets.UTF_8;
  private static final String BASE_DIR = "src/test/resources";
  private static final String LINE_BREAK = System.getProperty("line.separator");
  private LicensesTask licensesTask;
  private File outputDir;

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Before
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void setUp() throws IOException {
    outputDir = temporaryFolder.newFolder();
    File outputLicenses = new File(outputDir, "testHtml");
    outputLicenses.createNewFile();

    Project project = ProjectBuilder.builder().withProjectDir(new File(BASE_DIR)).build();
    licensesTask = project.getTasks().create("generateLicenses", LicensesTask.class);
    licensesTask.setHtml(outputLicenses);
  }

  private void createLicenseZip(String name) throws IOException {
    File zipFile = new File(name);
    ZipOutputStream output = new ZipOutputStream(new FileOutputStream(zipFile));
    File input = new File(BASE_DIR + "/sampleLicenses");
    for (File file : Objects.requireNonNull(input.listFiles())) {
      ZipEntry entry = new ZipEntry(file.getName());
      byte[] bytes = Files.readAllBytes(file.toPath());
      output.putNextEntry(entry);
      output.write(bytes, 0, bytes.length);
      output.closeEntry();
    }
    output.close();
  }

  @Test
  public void testInitLicenseFile() throws IOException {
    licensesTask.initLicenseFile();

    assertTrue(licensesTask.getHtml().exists());
    assertEquals(0, Files.size(licensesTask.getHtml().toPath()));
  }

  @Test
  public void testIsGranularVersion_True() {
    String versionTrue = "14.6.0";
    assertTrue(LicensesTask.isGranularVersion(versionTrue));
  }

  @Test
  public void testIsGranularVersion_False() {
    String versionFalse = "11.4.0";
    assertFalse(LicensesTask.isGranularVersion(versionFalse));
  }

  @Test
  public void testAddLicensesFromPom() {
    File deps1 = getResourceFile("dependencies/groupA/deps1.pom");
    String name1 = "deps1";
    String group1 = "groupA";
    licensesTask.addLicensesFromPom(deps1, group1, name1);

    assertTrue(licensesTask.licensesMap.containsKey("groupA:deps1"));
  }

  @Test
  public void testAddLicensesFromPom_withoutDuplicate() {
    File deps1 = getResourceFile("dependencies/groupA/deps1.pom");
    String name1 = "deps1";
    String group1 = "groupA";
    licensesTask.addLicensesFromPom(deps1, group1, name1);

    File deps2 = getResourceFile("dependencies/groupB/bcd/deps2.pom");
    String name2 = "deps2";
    String group2 = "groupB";
    licensesTask.addLicensesFromPom(deps2, group2, name2);

    assertThat(licensesTask.licensesMap.size(), is(2));
    assertTrue(licensesTask.licensesMap.containsKey("groupA:deps1"));
    assertTrue(licensesTask.licensesMap.containsKey("groupB:deps2"));
  }

  @Test
  public void testAddLicensesFromPom_withMultiple() {
    File deps1 = getResourceFile("dependencies/groupA/deps1.pom");
    String name1 = "deps1";
    String group1 = "groupA";
    licensesTask.addLicensesFromPom(deps1, group1, name1);

    File deps2 = getResourceFile("dependencies/groupE/deps5.pom");
    String name2 = "deps5";
    String group2 = "groupE";
    licensesTask.addLicensesFromPom(deps2, group2, name2);

    assertThat(licensesTask.licensesMap.size(), is(3));
    assertTrue(licensesTask.licensesMap.containsKey("groupA:deps1"));
    assertTrue(licensesTask.licensesMap.containsKey("groupE:deps5 MIT License"));
    assertTrue(licensesTask.licensesMap.containsKey("groupE:deps5 Apache License 2.0"));
  }

  @Test
  public void testAddLicensesFromPom_withDuplicate() {
    File deps1 = getResourceFile("dependencies/groupA/deps1.pom");
    String name1 = "deps1";
    String group1 = "groupA";
    licensesTask.addLicensesFromPom(deps1, group1, name1);

    File deps2 = getResourceFile("dependencies/groupA/deps1.pom");
    String name2 = "deps1";
    String group2 = "groupA";
    licensesTask.addLicensesFromPom(deps2, group2, name2);

    assertThat(licensesTask.licensesMap.size(), is(1));
    assertTrue(licensesTask.licensesMap.containsKey("groupA:deps1"));
  }

  private File getResourceFile(String resourcePath) {
    return new File(Objects.requireNonNull(getClass().getClassLoader().getResource(resourcePath)).getFile());
  }

  @Test
  public void testGetBytesFromInputStream_throwException() throws IOException {
    InputStream inputStream = mock(InputStream.class);
    when(inputStream.read(any(byte[].class), anyInt(), anyInt())).thenThrow(new IOException());
    try {
      LicensesTask.getBytesFromInputStream(inputStream, 1, 1);
      fail("This test should throw Exception.");
    } catch (RuntimeException e) {
      assertEquals("Failed to read license text.", e.getMessage());
    }
  }

  @Test
  public void testGetBytesFromInputStream_normalText() {
    String test = "test";
    InputStream inputStream = new ByteArrayInputStream(test.getBytes(UTF_8));
    String content = new String(LicensesTask.getBytesFromInputStream(inputStream, 1, 1), UTF_8);
    assertEquals("e", content);
  }

  @Test
  @Ignore("Who knows why")
  public void testGetBytesFromInputStream_specialCharacters() {
    String test = "Copyright © 1991-2017 Unicode";
    InputStream inputStream = new ByteArrayInputStream(test.getBytes(UTF_8));
    String content = new String(LicensesTask.getBytesFromInputStream(inputStream, 4, 18), UTF_8);
    assertEquals("right © 1991-2017", content);
  }

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void testAddGooglePlayServiceLicenses() throws IOException {
    File tempOutput = new File(outputDir, "dependencies/groupC");
    tempOutput.mkdirs();
    createLicenseZip(tempOutput.getPath() + "play-services-foo-license.aar");
    File artifact = new File(tempOutput.getPath() + "play-services-foo-license.aar");
    licensesTask.addGooglePlayServiceLicenses(artifact);

    assertThat(licensesTask.googleServiceLicenses.size(), is(2));
    assertTrue(licensesTask.googleServiceLicenses.contains("safeparcel"));
    assertTrue(licensesTask.googleServiceLicenses.contains("JSR 305"));
    assertThat(licensesTask.licensesMap.size(), is(2));
    assertTrue(licensesTask.licensesMap.containsKey("safeparcel"));
    assertTrue(licensesTask.licensesMap.containsKey("JSR 305"));
  }

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void testAddGooglePlayServiceLicenses_withoutDuplicate() throws IOException {
    File groupC = new File(outputDir, "dependencies/groupC");
    groupC.mkdirs();
    createLicenseZip(groupC.getPath() + "/play-services-foo-license.aar");
    File artifactFoo = new File(groupC.getPath() + "/play-services-foo-license.aar");

    File groupD = new File(outputDir, "dependencies/groupD");
    groupD.mkdirs();
    createLicenseZip(groupD.getPath() + "/play-services-bar-license.aar");
    File artifactBar = new File(groupD.getPath() + "/play-services-bar-license.aar");

    licensesTask.addGooglePlayServiceLicenses(artifactFoo);
    licensesTask.addGooglePlayServiceLicenses(artifactBar);

    assertThat(licensesTask.googleServiceLicenses.size(), is(2));
    assertTrue(licensesTask.googleServiceLicenses.contains("safeparcel"));
    assertTrue(licensesTask.googleServiceLicenses.contains("JSR 305"));
    assertThat(licensesTask.licensesMap.size(), is(2));
    assertTrue(licensesTask.licensesMap.containsKey("safeparcel"));
    assertTrue(licensesTask.licensesMap.containsKey("JSR 305"));
  }

  @Test
  public void testAppendLicense() {
    licensesTask.appendLicense("license1", "test".getBytes(UTF_8));
    assertTrue(licensesTask.licensesMap.containsKey("license1"));
  }

  @Test
  public void testWriteMetadata() throws IOException {
    licensesTask.licensesMap.put("license1", "terms1");
    licensesTask.licensesMap.put("license2", "terms2");
    licensesTask.writeMetadata();

    String content = Files.readString(licensesTask.getHtml().toPath(), UTF_8);
    assertThat(content, containsString("license1"));
    assertThat(content, containsString("terms1"));
    assertThat(content, containsString("license2"));
    assertThat(content, containsString("terms2"));
  }
}
