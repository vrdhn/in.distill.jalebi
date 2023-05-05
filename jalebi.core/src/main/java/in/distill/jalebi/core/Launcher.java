package in.distill.jalebi.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Launcher {
    private static Logger LOG = Logger.getLogger(Launcher.class.getSimpleName());

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

        System.out.println("jalebi.core is launched");
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
                // LOG.info("NOT FOUND " + moduleInfo);
            }
        }
    }

    private static List<Path> getSubDirs(Path p) throws Exception {
        return Files.list(p).filter(f -> Files.isDirectory(f)).toList();
    }

    // This function can need fine tuning.
    // It needs to iterate for all path-frag of given depth, trying to find tool.
    // The tricky part is giving unclaimed message for the correct path-fragments.
    private static boolean walk(ModelBuilder mb, Path moduleRoot, Path p, int depth)
            throws Exception {
        if (depth > 0) {
            depth--;
            List<Path> unclaimed = new ArrayList<>();
            int claimed = 0;
            List<Path> subs = getSubDirs(p);
	    if( subs.isEmpty() ) {
		return true;
	    }
            List<Tool.Factory> tools = ToolRegistry.getToolFactories();
            for (Path sub : subs) {
                Path frag = moduleRoot.relativize(sub);
                List<Tool.Factory> claimers = tools.stream().filter(f -> f.claim(frag)).toList();
                if (claimers.isEmpty()) {
                    unclaimed.add(sub);
                } else {
                    if (claimers.size() > 1) {
                        LOG.severe(
                                "Too many claims for "
                                        + frag
                                        + " : "
                                        + claimers.stream()
                                                .map(f -> f.name())
                                                .collect(Collectors.joining(", ")));
                        // Picking the first claim...
                    }
                    claimed++;
                    mb.claim(moduleRoot, claimers.get(0), frag);
                }
            }
            // recurse in unclaimed...
	    boolean ret = claimed > 0;
            for (Path sub : unclaimed) {
                if (walk(mb, moduleRoot, sub, depth)) {
		    ret = true;
                } else {
                    LOG.severe(
                            "Unclaimed directory: "
                                    + moduleRoot.getFileName()
                                    + "/"
                                    + moduleRoot.relativize(sub));
		}
            }
            return ret;
        }
        return false;
    }

    private static ModelBuilder processModuleFolders(Path top, String[] modules) throws Exception {
        ModelBuilder mb = new ModelBuilder(top, modules);
        boolean ret = true;
        for (String module : modules) {
            Path moduleRoot = top.resolve(module);
            if (!walk(mb, moduleRoot, moduleRoot, 3)) {
                ret = false;
            }
        }
        // return ret ? mb : null;
        return mb;
    }
}
