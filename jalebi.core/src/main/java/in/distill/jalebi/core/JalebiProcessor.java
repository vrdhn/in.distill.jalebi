package in.distill.jalebi.core;

import in.distill.jalebi.api.Processor;

/** Processor for src/jalebi folder */
public class JalebiProcessor implements Processor {
    @Override
    public String claimedFolderName() {
        return "jalebi";
    }
}
