package in.distill.jalebi.bootstrap;

import java.nio.file.Files;
import java.nio.file.Path;
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
    public static void launcher(Path top, String[] modules, String[] args) {
        processJalebiFolders(top,modules);

        // 1. Process
        // 1, get all 'src/[Processor]' names.
        // 2. get all the JalebiProcessor targets.
        // 3. background-Download all the missing processors : src/[Processor]
        // 4. run the JalebiProcessor target
        // 5.

        // Processors registry will tell the processor to use for src/[processor]
        // except for src/jalebi, which is special
        //

        // Module should contain /src/[type]/[subtype]
        // compile all the src/jalebi/java/
        // if it doesn't exist , look at other
        //   src/*/ and provide standard stuffs
        //

        System.out.println("Bootstrap is launched");
    }

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
            }
        }
    }
}
