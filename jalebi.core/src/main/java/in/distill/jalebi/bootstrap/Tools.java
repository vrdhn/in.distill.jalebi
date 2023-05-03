package in.distill.jalebi.bootstrap;

import java.nio.file.Path;
import java.util.logging.Logger;

public class Tools {
    private static Logger LOG = Logger.getLogger(Tools.class.getName());

    public void addToolRoot(Path top, String moduleName, String toolName, Path toolRoot) {
        LOG.info("Adding tool: " + toolName + " : " + moduleName + "/" + top.relativize(toolRoot));
    }

    public void run(String[] ars) {}
}
