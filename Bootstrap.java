// SPDX-License-Identifier: AGPL-3.0-or-later; (C) Vardhan Varma, Distill.in

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.lang.reflect.Method;

/**
 * This file is stage 0 of a project built using Jalebi. It should be invoked directly using java
 * from a JDK installation. i.e.
 *
 * <pre>
 *    java Bootstrap.java
 * </pre>
 */
public final class Bootstrap {


  /**
   * Update this to vendored copy of jalebi-bootstrap or null to save a few micosecond to check
   * existance of this. (in which case, the class VendoredBootstrapper can be removed as well)
   */
  private static final Path PROJECT_ROOT =
      Paths.get(System.getProperty("jdk.launcher.sourcefile")).toAbsolutePath().getParent();

  private static final Path VENDORED =
      PROJECT_ROOT.resolve("jalebi-bootstrap/BootstrapperJalebi.java");
  private static final Path VENDORED_JAR =
      PROJECT_ROOT.resolve("jalebi-bootstrap/target/jalebi-bootstrap.jar");

  private static final Path JALEBI_HOME =
      Paths.get(System.getProperty("user.home"), ".config", "Jalebi");

  // This is fine on linux . Windows has additional complexity of javaw and .exe
  private static final Path JAVA_BINARY = Paths.get(System.getProperty("java.home"), "bin", "java");

  public void run(String[] args) throws Exception {

    final Path stage1Jar = getStage1JarVendored();

    JarFileLoader jfl = new JarFileLoader();
    jfl.addFile(stage1Jar);
    Class<?> cls = jfl.loadClass("in.distill.jalebi.bootstrap.Launcher");
    Method launcher = cls.getMethod("launcher", String.class.arrayType());
    launcher.invoke(null,(Object)args);
  }

  public static class JarFileLoader extends URLClassLoader {
    public JarFileLoader() {
      super(new URL[0]);
    }

      public void addFile(Path path) throws URISyntaxException,MalformedURLException {
      String urlPath = "jar:file://" + path + "!/";
      addURL(new URI(urlPath).toURL());
    }
  }

  // TODO: this should use compiler api, and not launch java.
  private Path getStage1JarVendored() throws IOException, InterruptedException {
    if (!Files.exists(VENDORED)) {
      return null;
    }
    Process proc =
        new ProcessBuilder()
            .directory(VENDORED.getParent().toFile())
            .command(JAVA_BINARY.toString(), VENDORED.toString())
            .start();
    int ret = proc.waitFor();
    if (ret != 0 || !Files.exists(VENDORED_JAR)) {
      System.out.println("** ERROR compiling vendored " + VENDORED);
      proc.getInputStream().transferTo(System.out);
      proc.getErrorStream().transferTo(System.out);
      System.exit(1);
    }

    return VENDORED_JAR;
  }

  public static void main(String[] args) throws Exception {
    new Bootstrap().run(args);
  }
}
