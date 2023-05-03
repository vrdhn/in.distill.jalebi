package in.distill.jalebi.core;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ModelBuilder {
    private static Logger LOG = Logger.getLogger(ModelBuilder.class.getSimpleName());

    private final Path projectRoot;

    private final class Module {
        private final Map<Tool.Factory, Tool> toolInstances = new HashMap<>();
        private final Path moduleRoot;

        public Module(Path moduleRoot) {
            this.moduleRoot = moduleRoot;
        }
    }

    private final Map<Path, Module> modules = new HashMap<>();

    public ModelBuilder(Path top, String[] modules) {
        this.projectRoot = top;
    }

    // ModelBuilder will have one instance of a tool for each module
    public void claim(Path moduleRoot, Tool.Factory f, Path frag) {
        LOG.info("CLAIMING " + f.name() + " : " + moduleRoot.getFileName() + " : " + frag);
        modules.computeIfAbsent(moduleRoot, k -> new Module(k))
                .toolInstances
                .computeIfAbsent(f, c -> f.make(moduleRoot))
                .addSource(frag);
    }
}
