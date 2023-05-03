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
import java.util.logging.Logger;

/**
 * This file is an example of vendored <code>jalebi-bootstrap</code>, where it is compiled, rather
 * than downloaded from upstream. A normal usage of jalebi build system will <em>NOT</em> have to
 * use this, instead using a copy of {@link JalebiBuild}.java
 *
 * <pre>
 *    java JalebiBuildBootstrapped.java
 * </pre>
 */
public final class JalebiBuildBootstrapped {
    private static Logger LOG = Logger.getLogger(JalebiBuildBootstrapped.class.getName());

    /**
     * Here you define all the modules there are ...... A module should have
     * src/main/java/module-info.java and optionally src/jalebi/java/module-info.java
     */
    private static final String[] MODULES = {
        "jalebi.boostrap", // Gets compiled twice , once as vendored, then as modules.
        "jalebi.examples.library",
        "jalebi.example.executable",
    };

    private static final Path SCRIPT_FILE =
            Paths.get(System.getProperty("jdk.launcher.sourcefile"));

    private static final Path PROJECT_ROOT = SCRIPT_FILE.toAbsolutePath().getParent();


    private static final Path JALEBI_HOME =
            Paths.get(System.getProperty("user.home"), ".config", "Jalebi");

    // This is fine on linux . Windows has additional complexity of javaw and .exe
    private static final Path JAVA_BINARY =
            Paths.get(System.getProperty("java.home"), "bin", "java");

    public static class JarFileLoader extends URLClassLoader {
        public JarFileLoader() {
            super(new URL[0]);
        }

        public void addFile(Path path) throws URISyntaxException, MalformedURLException {
            String urlPath = "jar:file://" + path + "!/";
            addURL(new URI(urlPath).toURL());
        }
    }

    // This *should* use java compiler API, but for now
    // it's okay if this launches java,
    // vendoring of jalebi-botstrap is a *special* case.
    private static Path getJalebiBootstrapVendoredJar() throws IOException, InterruptedException {
        final Path VENDORED_BOOTSTRAP =
                PROJECT_ROOT.resolve("jalebi.bootstrap/JalebiBootstrapBuild.java");
         final Path VENDORED_BOOTSTRAP_JAR =
                PROJECT_ROOT.resolve("jalebi-bootstrap/target/jalebi-bootstrap-vendored.jar");

        if (!Files.exists(VENDORED_BOOTSTRAP)) {
            LOG.severe("Cannot not read file " + VENDORED_BOOTSTRAP);
            return null;
        }
        Process proc =
                new ProcessBuilder()
                        .directory(VENDORED_BOOTSTRAP.getParent().toFile())
                        .command(JAVA_BINARY.toString(), VENDORED_BOOTSTRAP.toString(),VENDORED_BOOTSTRAP_JAR.toString())
                        .start();
        int ret = proc.waitFor();
        if (ret != 0 || !Files.exists(VENDORED_BOOTSTRAP_JAR)) {
            System.out.println("** ERROR compiling vendored " + VENDORED_BOOTSTRAP);
            proc.getInputStream().transferTo(System.out);
            proc.getErrorStream().transferTo(System.out);
            System.exit(1);
        }

        return VENDORED_BOOTSTRAP_JAR;
    }

    /**
     * Entry point for the Jalebi build.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.logging.SimpleFormatter.format","[%1$tF %1$tT > %3$s] %5$s%6$s%n");
        LOG.info("Jalebi is bootstrapping.");

        final Path stage1Jar = getJalebiBootstrapVendoredJar();
        LOG.info("Prepared jar " +stage1Jar);


        JarFileLoader jfl = new JarFileLoader();
        jfl.addFile(stage1Jar);
        // public static void launcher(Path script, String[] modules, String[] args)
        Class<?> cls = jfl.loadClass("in.distill.jalebi.bootstrap.Launcher");
        Method launcher =
                cls.getMethod(
                        "launcher", Path.class, String.class.arrayType(), String.class.arrayType());
        launcher.invoke(null, SCRIPT_FILE.getParent(), MODULES, (Object) args);
    }
}
