// SPDX-License-Identifier: AGPL-3.0-or-later; (C) Vardhan Varma, Distill.in

// This ia specialized BootstrapperJalebi.java for the bootstrapper. Using this elsewhere
// is not recommended.

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Stream;

/*
 * Collect all the java  files from  src/main/java
 * Compare if any of them, or this file, is newer then target/jalebi-bootstrap.jar
 * if yes compile and recreate the jalebi-bootstrap.jar
 */

class BootstrapperJalebi {

  private static final Path JALEBI_FILE =
      Paths.get(System.getProperty("jdk.launcher.sourcefile")).toAbsolutePath();
  private static final Path PROJECT_ROOT = JALEBI_FILE.getParent();

  private static final Path TARGET_JAR = PROJECT_ROOT.resolve("target/jalebi-bootstrap.jar");
  private static final Path TARGET_KLS = PROJECT_ROOT.resolve("target/classes");

  private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

  static boolean recompileCheck(List<Path> sourceFiles) throws IOException {
    if (!Files.exists(TARGET_JAR)) {
      return true;
    }
    FileTime jarTime = Files.getLastModifiedTime(TARGET_JAR);
    if (jarTime.compareTo(Files.getLastModifiedTime(JALEBI_FILE)) < 0) {
      return true;
    }
    for (Path s : sourceFiles) {
      if (jarTime.compareTo(Files.getLastModifiedTime(s)) < 0) {
        return true;
      }
    }
    return false;
  }

  public static void main(String[] args) throws IOException {

    List<Path> sourceFiles =
        Files.walk(PROJECT_ROOT.resolve("src/main/java/"), FileVisitOption.FOLLOW_LINKS)
            .filter((p) -> p.toString().endsWith(".java"))
            .filter((p) -> Files.isRegularFile(p))
            .toList();
    sourceFiles.forEach(x -> System.out.println(x));
    if (!recompileCheck(sourceFiles)) {
      return;
    }

    DiagnosticCollector<JavaFileObject> ds = new DiagnosticCollector<>();
    try (StandardJavaFileManager fm = COMPILER.getStandardFileManager(ds, null, null)) {

      Iterable<? extends JavaFileObject> sources = fm.getJavaFileObjectsFromPaths(sourceFiles);
      JavaCompiler.CompilationTask task =
          COMPILER.getTask(null, fm, ds, List.of("-d", TARGET_KLS.toString()), null, sources);
      boolean result = task.call();
      ds.getDiagnostics().forEach(x -> System.out.println(x.toString()));
      if (!result) {
        System.exit(1);
      }
    }

    Manifest manifest = new Manifest();
    manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
    JarOutputStream target = new JarOutputStream(Files.newOutputStream(TARGET_JAR), manifest);
    add(TARGET_KLS, target);
    target.close();

    System.out.println("Executed");
  }

  private static void add(Path source, JarOutputStream target) throws IOException {
    String name = TARGET_KLS.relativize(source).toString();
    if (Files.isDirectory(source)) {
      if (!name.endsWith("/")) {
        name += "/";
      }
      JarEntry entry = new JarEntry(name);
      entry.setTime(Files.getLastModifiedTime(source).toMillis());
      target.putNextEntry(entry);
      target.closeEntry();
      try (Stream<Path> list = Files.list(source)) {
        for (Iterator<Path> it = list.iterator(); it.hasNext(); ) {
          Path p = it.next();
          add(p, target);
        }
      }
    } else {
      JarEntry entry = new JarEntry(name);
      entry.setTime(Files.getLastModifiedTime(source).toMillis());
      target.putNextEntry(entry);
      try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(source))) {
        byte[] buffer = new byte[1024];
        while (true) {
          int count = in.read(buffer);
          if (count == -1) break;
          target.write(buffer, 0, count);
        }
        target.closeEntry();
      }
    }
  }
}
