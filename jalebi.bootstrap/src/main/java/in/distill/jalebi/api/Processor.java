package in.distill.jalebi.api;

public interface Processor {

    /**
     * Get name of all folders which this processor will handle.
     * e.g. "main", "test", "jalebi" etc.
     * @return
     */
    String  claimedFolderName();


}
