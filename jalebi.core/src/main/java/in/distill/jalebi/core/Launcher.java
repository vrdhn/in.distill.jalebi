package in.distill.jalebi.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public final class Launcher {
    private static Logger LOG = Logger.getLogger(Launcher.class.getName());

    /**
     * Launcher will run command line.
     *
     * @param top the Top directory of the project.
     * @param modules the list of modules, relative to Top
     * @param args command line arguments typed by user.
     */
    public static void launcher(Path top, String[] modules, String[] args) throws Exception {
        processJalebiFolders(top, modules);

        ModelBuilder mb = processModuleFolders(top, modules);

        // tools.run(args);

        System.out.println("Bootstrap is launched");
    }

    // if src/jalebi/java/module-info.java exists, compile and load it.
    // loading it will register *things*
    private static void processJalebiFolders(Path root, String[] modules) {
        for (String module : modules) {
            Path moduleInfo =
                    root.resolve(module)
                            .resolve("src")
                            .resolve("jalebi")
                            .resolve("java")
                            .resolve("module-info.java")
                            .toAbsolutePath();
            if (Files.exists(moduleInfo)) {
                LOG.info("Found " + moduleInfo);
            } else {
                LOG.info("NOT FOUND " + moduleInfo);
            }
        }
    }

    private static List<Path> getSubDirs(Path p) throws Exception {
        return Files.list(p).filter(f -> Files.isDirectory(f)).toList();
    }

    private static void walk(ModelBuilder mb, Path modRoot, Path p, int depth) throws Exception {
        if (depth > 0) {
            depth--;
            for (Path sub : getSubDirs(p)) {
                Path f = modRoot.relativize(sub);
                if (!mb.tryToClaim(modRoot, f)) {
                    walk(mb, modRoot, sub, depth);
                }
            }
        }
    }

    private static ModelBuilder processModuleFolders(Path top, String[] modules) throws Exception {
        ModelBuilder mb = new ModelBuilder(top, modules);
        for (String module : modules) {
            Path modRoot = top.resolve(module);
            walk(mb, modRoot, modRoot, 3);
        }
        return mb;
    }
}
