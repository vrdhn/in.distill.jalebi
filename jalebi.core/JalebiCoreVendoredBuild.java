// SPDX-License-Identifier: AGPL-3.0-or-later; (C) Vardhan Varma, Distill.in

/*
 * This builds vendored jalebi-core, there doesn't not exist any other use.
 * Any other vendored plugin will be compiled by jalebi-core.
 *
 */

import java.io.BufferedInputStream;
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
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/*
 * Collect all the java  files from  src/main/java
 * Compare if any of them, or this file, is newer then target/jalebi-core.jar
 * if yes compile and recreate the jalebi-core.jar
 *
 * <code>
 *   java JalebiCoreVendoredBuild.java  <Full-Path-to-output-jar>
 * </code>
 */

class JalebiCoreVendoredBuild {
    private static Logger LOG = Logger.getLogger(JalebiCoreVendoredBuild.class.getName());

    private static final Path JALEBI_FILE =
            Paths.get(System.getProperty("jdk.launcher.sourcefile")).toAbsolutePath();
    private static final Path PROJECT_ROOT = JALEBI_FILE.getParent();

    private static Path TARGET_JAR;
    private static Path TARGET_KLS;

    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    static boolean recompileCheck(List<Path> sourceFiles) throws IOException {
        if (!Files.exists(TARGET_JAR)) {
            // LOG.info("compile because no jar");
            return true;
        }
        FileTime jarTime = Files.getLastModifiedTime(TARGET_JAR);
        if (jarTime.compareTo(Files.getLastModifiedTime(JALEBI_FILE)) < 0) {
            // LOG.info("compile because newer:" + JALEBI_FILE);
            return true;
        }
        for (Path s : sourceFiles) {
            if (jarTime.compareTo(Files.getLastModifiedTime(s)) < 0) {
                // LOG.info("compile because newer:" + s);
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT > %4$-6.6s %3$-25.25s] %5$s%6$s%n");

        TARGET_JAR = Paths.get(args[0]);
        TARGET_KLS = TARGET_JAR.getParent().resolve("classes");

        // Should source be kept in src/jalebi/java or src/main/java ???
        // by convention, src/jalebi is NEVER SHIPPED, so it's src/main
        Path moduleInfoFolder = PROJECT_ROOT.resolve("src/main/java/");

        List<Path> sourceFiles =
                Files.walk(moduleInfoFolder, FileVisitOption.FOLLOW_LINKS)
                        .filter((p) -> p.toString().endsWith(".java"))
                        .filter((p) -> Files.isRegularFile(p))
                        .toList();
        // for (Path s : sourceFiles) {
        //    LOG.info("SOURCE : " + moduleInfoFolder.relativize(s));
        // }
        if (!recompileCheck(sourceFiles)) {
            LOG.info("Skipping recompilation of vendored jalebi.core");
            return;
        }
        LOG.info("Building vendored jalebi.core.");

        DiagnosticCollector<JavaFileObject> ds = new DiagnosticCollector<>();
        Files.createDirectories(TARGET_KLS);
        try (StandardJavaFileManager fm = COMPILER.getStandardFileManager(ds, null, null)) {

            Iterable<? extends JavaFileObject> sources =
                    fm.getJavaFileObjectsFromPaths(sourceFiles);
            // fm.setLocation(javax.tools.StandardLocation.CLASS_OUTPUT,
            // Arrays.asList(TARGET_KLS.toFile()));
            // fm.setLocation(javax.tools.StandardLocation.CLASS_PATH,
            // Arrays.asList(TARGET_KLS.toFile()));
            JavaCompiler.CompilationTask task =
                    COMPILER.getTask(
                            null, fm, ds, List.of("-d", TARGET_KLS.toString()), null, sources);
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

        LOG.info("Created " + TARGET_JAR);
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
