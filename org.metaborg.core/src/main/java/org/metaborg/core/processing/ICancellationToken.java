package org.metaborg.core.processing;

/**
 * Interface for figuring out if something has been cancelled.
 */
public interface ICancellationToken {
    /**
     * @return If cancellation has been requested.
     */
    public abstract boolean cancelled();
}
