package in.distill.jalebi.bootstrap;


import in.distill.jalebi.api.Processor;

/**
 * Processor for src/jalebi folder
 */
public class JalebiProcessor implements Processor {
    @Override
    public String claimedFolderName() {
        return "jalebi";
    }
}
