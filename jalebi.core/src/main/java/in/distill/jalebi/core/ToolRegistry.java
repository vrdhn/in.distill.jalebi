package in.distill.jalebi.core;

import java.util.List;

public class ToolRegistry {

    public static List<Tool.Factory> getToolFactories() {
        return List.of(new ToolTarget.ToolTargetFactory(), new ToolJava.ToolJavaFactory());
    }
}
