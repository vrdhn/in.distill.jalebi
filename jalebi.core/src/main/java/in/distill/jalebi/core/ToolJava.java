package in.distill.jalebi.core;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ToolJava implements Tool {

    public static final class ToolJavaFactory implements Tool.Factory {

        @Override
        public String name() {
            return "java";
        }

        @Override
        public boolean claim(Path fragment) {
            return "src/main/java".equals(fragment.toString());
        }

        @Override
        public ToolJava make(Path moduleRoot) {
            return new ToolJava(moduleRoot);
        }
    }

    private final Path moduleRoot;
    private final List<Path> claimedSources = new ArrayList<>();

    public ToolJava(Path moduleRoot) {
        this.moduleRoot = moduleRoot;
    }

    @Override
    public void addSource(Path claimedSource) {
        this.claimedSources.add(claimedSource);
    }
}
