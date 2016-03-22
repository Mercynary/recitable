package net.fatlenny.datacitation.api;

/**
 * Exception that can be thrown in case something went wrong.
 */
public class CitationDBException extends RuntimeException {
    private static final long serialVersionUID = 2483237477381737369L;

    public CitationDBException() {
        super();
    }

    public CitationDBException(String message, Throwable cause) {
        super(message, cause);
    }

    public CitationDBException(String message) {
        super(message);
    }

    public CitationDBException(Throwable cause) {
        super(cause);
    }
}
