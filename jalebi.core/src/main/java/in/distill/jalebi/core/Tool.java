package in.distill.jalebi.core;

import java.nio.file.Path;

/** Keep module-name ==> Tool Tool will keep multiple */
public interface Tool {

    // This is a hash key, pointer equivalence is ok right now.
    public interface Factory {

        String name();

        boolean claim(Path frag);

        Tool make(Path moduleRoot);
    }

    void addSource(Path claimedSource);
}
