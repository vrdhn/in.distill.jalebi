// SPDX-License-Identifier: AGPL-3.0-or-later; (C) Vardhan Varma, Distill.in

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * This file is an example of vendored <code>jalebi.core</code>, where it is compiled, rather than
 * downloaded from upstream. A normal usage of jalebi build system will <em>NOT</em> have to use
 * this, instead using a copy of {@link JalebiBuild}.java
 *
 * <pre>
 *    java JalebiBuildBootstrapped.java
 * </pre>
 */
public final class JalebiBuildBootstrapped {
    private static Logger LOG = Logger.getLogger(JalebiBuildBootstrapped.class.getName());

    /**
     * Here you define all the modules. A module should have src/main/java/module-info.java and
     * optionally src/jalebi/java/module-info.java
     */
    private static final String[] MODULES = {
        "jalebi.core", // Gets compiled twice , once as vendored, then as module.
        "jalebi.examples.library",
        "jalebi.examples.executable",
    };

    // This *should* use java compiler API, but for now
    // it's okay if this launches java,
    // vendoring of jalebi.botstrap is a *special* case.
    private static class Vendored {

        public static Path getJalebiCoreJar(Path projectRoot)
                throws IOException, InterruptedException {

            // This is fine on linux . Windows has additional complexity of javaw and .exe
            final Path javaBinary = Paths.get(System.getProperty("java.home"), "bin", "java");
            // Look at jalebi.core/JalebiCoreVendoredBuild.java
            final Path vendoredCoreBuildScript =
                    projectRoot.resolve("jalebi.core/JalebiCoreVendoredBuild.java");
            // Look at jalebi.core/JalebiCoreVendoredBuild#TARGET_JAR
            final Path vendoredCoreJar =
                    projectRoot.resolve("jalebi.core/target/jalebi-core-vendored.jar");

            if (!Files.exists(vendoredCoreBuildScript)) {
                LOG.severe("Cannot not read file " + vendoredCoreBuildScript);
                return null;
            }
            Process proc =
                    new ProcessBuilder()
                            .inheritIO()
                            .directory(vendoredCoreBuildScript.getParent().toFile())
                            .command(javaBinary.toString(), vendoredCoreBuildScript.toString())
                            .start();
            int ret = proc.waitFor();
            if (ret != 0 || !Files.exists(vendoredCoreJar)) {
                LOG.severe("** ERROR compiling vendored " + vendoredCoreBuildScript);
                return null;
            }

            return vendoredCoreJar;
        }
    } // class Vendored

    public static class JarFileLoader extends URLClassLoader {
        public JarFileLoader() {
            super(new URL[0]);
        }

        public void addFile(Path path) throws URISyntaxException, MalformedURLException {
            String urlPath = "jar:file://" + path + "!/";
            addURL(new URI(urlPath).toURL());
        }
    } // class JarFileLoader

    public static void main(String[] args) throws Exception {
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT > %4$-6.6s %3$-25.25s] %5$s%6$s%n");
        Path scriptFile = Paths.get(System.getProperty("jdk.launcher.sourcefile")).toAbsolutePath();
        Path projectRoot = scriptFile.getParent();

        LOG.info("Jalebi is starting in " + projectRoot);

        final Path coreJar = Vendored.getJalebiCoreJar(projectRoot);
        if (coreJar == null) {
            System.exit(1);
        }
        LOG.info("jalebi.core: " + coreJar);

        try (JarFileLoader jfl = new JarFileLoader()) {
            jfl.addFile(coreJar);
            // public static void launcher(Path script, String[] modules, String[] args)
            Class<?> cls = jfl.loadClass("in.distill.jalebi.core.Launcher");
            Method launcher =
                    cls.getMethod(
                            "launcher",
                            Path.class,
                            String.class.arrayType(),
                            String.class.arrayType());
            launcher.invoke(null, projectRoot, MODULES, (Object) args);
        }
    }
}
