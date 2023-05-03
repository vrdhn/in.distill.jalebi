package in.distill.jalebi.core;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ToolTarget implements Tool {

    public static final class ToolTargetFactory implements Tool.Factory {

        @Override
        public String name() {
            return "target";
        }

        @Override
        public boolean claim(Path fragment) {
            return "target".equals(fragment.toString());
        }

        @Override
        public ToolTarget make(Path moduleRoot) {
            return new ToolTarget(moduleRoot);
        }
    }

    private final Path moduleRoot;
    private final List<Path> claimedSources = new ArrayList<>();

    public ToolTarget(Path moduleRoot) {
        this.moduleRoot = moduleRoot;
    }

    @Override
    public void addSource(Path claimedSource) {
        this.claimedSources.add(claimedSource);
    }
}
