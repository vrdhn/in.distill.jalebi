package in.distill.jalebi.bootstrap;

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

        Tools tools = processModuleFolders(top, modules);

        tools.run(args);

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

    // All the tools we'll need to run.

    // a module folder will have 'src/[processor]/
    private static Tools processModuleFolders(Path root, String[] modules) throws Exception {
        Tools tools = new Tools();
        for (String module : modules) {
            Path src = root.resolve(module).resolve("src");
            if (Files.exists(src) && Files.isDirectory(src)) {
                List<Path> toolRoots = Files.list(src).toList();
                for (Path toolRoot : toolRoots) {
                    String toolName = toolRoot.getFileName().toString();
                    tools.addToolRoot(module, toolName, toolRoot);
                }
            }
        }
        return tools;
    }
}
