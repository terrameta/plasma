/**
 * Copyright 2017 TerraMeta Software, Inc.
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

package org.plasma.mojo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import org.apache.maven.plugin.MojoExecutionException;
import org.plasma.provisioning.cli.XSDTool;
import org.plasma.provisioning.cli.XSDToolAction;

/**
 * Mojo implementation for generating SDO model artifacts from XML Schema, such
 * as UML/XMI. Sets up the mojo environment and then calls the Plasma XSD
 * command-line (CLI) tool, passing it mojo args.
 * 
 * @author Scott Cinnamond
 * @since 1.1.3
 * 
 * @goal xsd
 * @phase generate-sources
 * 
 * @see org.plasma.provisioning.cli.XSDTool
 */
public class XSDMojo extends ClassRealmMojo {
  /**
   * The tool action to be performed
   * 
   * @parameter expression="${xsd.action}" default-value="convert"
   */
  private String action;

  /**
   * The XML Schema source file name
   * 
   * @parameter expression="${xsd.sourceFile}"
   */
  private String sourceFile;

  /**
   * The destination or target file name
   * 
   * @parameter expression="${xsd.destFile}"
   */
  private String destFile;

  /**
   * The destination or target file type
   * 
   * @parameter expression="${xsd.destFileType}"
   * @see org.plasma.provisioning.cli.ProvisioningTarget
   */
  private String destFileType;

  /**
   * The destination or target namespage URI
   * 
   * @parameter expression="${xsd.destNamespaceURI}"
   */
  private String destNamespaceURI;

  /**
   * The destination or target namespage prefix
   * 
   * @parameter expression="${xsd.destNamespacePrefix}" default-value="tns"
   */
  private String destNamespacePrefix;

  public void execute() throws MojoExecutionException {
    super.execute();

    getLog().debug("tool: " + XSDTool.class.getName());
    getLog().debug("classRealm: " + this.classRealm);

    try {
      XSDToolAction toolAction = getToolAction(this.action);

      long lastExecution = 0L;
      File mojoDir = new File(MojoConstants.MOJO_DIR);
      if (mojoDir.exists()) {
        File mojoFile = new File(mojoDir, this.getClass().getSimpleName() + "_"
            + MojoConstants.MOJO_STALE_FLAG);
        if (mojoFile.exists()) {
          lastExecution = mojoFile.lastModified();
          getLog().info("last successful execution: " + String.valueOf(new Date(lastExecution)));
        }
      }

      String[] args = { "-" + toolAction.name(), this.sourceFile, this.destFile, this.destFileType,
          this.destNamespaceURI, this.destNamespacePrefix, String.valueOf(lastExecution) };

      getLog().info("executing tool: " + XSDTool.class.getName());
      XSDTool.main(args);

      mojoDir.mkdirs();
      if (mojoDir.exists()) {
        File mojoFile = new File(mojoDir, this.getClass().getSimpleName() + "_"
            + MojoConstants.MOJO_STALE_FLAG);
        if (mojoFile.exists())
          mojoFile.delete();
        FileOutputStream out = null;
        try {
          out = new FileOutputStream(mojoFile);
          out.write(this.getClass().getSimpleName().getBytes());
          out.flush();
        } finally {
          if (out != null)
            out.close();
        }
      } else
        getLog().warn(
            "mojo: " + this.getClass().getName() + " - could not create dir, "
                + MojoConstants.MOJO_DIR);

    } catch (IllegalArgumentException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    } catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  private XSDToolAction getToolAction(String action) {
    XSDToolAction command = null;
    try {
      command = XSDToolAction.valueOf(action);
    } catch (IllegalArgumentException e) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < XSDToolAction.values().length; i++) {
        if (i > 0)
          buf.append(", ");
        buf.append(XSDToolAction.values()[i].name());
      }

      throw new IllegalArgumentException("'" + action + "' - expected one of [" + buf.toString()
          + "]");
    }
    return command;
  }
}