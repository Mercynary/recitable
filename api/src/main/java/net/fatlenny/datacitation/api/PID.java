package net.fatlenny.datacitation.api;

/**
 * Represents a Persistent Identifier.
 */
public interface PID {
    /**
     * Returns the identifier for this Persistent Identififer.
     * 
     * @return the identifier.
     */
    String getIdentifier();

    /**
     * Returns an optional name for the Persistent Identififer.
     * 
     * @return the name if it exists.
     */
    String getName();
}
