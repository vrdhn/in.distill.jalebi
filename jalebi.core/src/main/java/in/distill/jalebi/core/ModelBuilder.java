package in.distill.jalebi.core;

import java.nio.file.Path;
import java.util.logging.Logger;

public class ModelBuilder {
    private static Logger LOG = Logger.getLogger(ModelBuilder.class.getName());

    private final Path projectRoot;

    public ModelBuilder(Path top, String[] modules) {
        this.projectRoot = top;
    }

    public boolean tryToClaim(Path modRoot, Path fragPath) {
        LOG.info("Checking " + modRoot.getFileName() + " : " + fragPath.toString());
        return false;
    }
}
