package in.distill.jalebi.bootstrap;

import in.distill.jalebi.api.Processor;

import java.util.concurrent.ConcurrentHashMap;

/**
 * A src/[Processor]/[Type]/ is handled by a particular processor. This is registry of such
 * processors.
 *
 * <p>This is going to be downloaded and cached.
 */
public class ProcessorsRegistry {

    // { x.claimedFolderName() ==> x }
    ConcurrentHashMap<String, Processor> processors;
}
