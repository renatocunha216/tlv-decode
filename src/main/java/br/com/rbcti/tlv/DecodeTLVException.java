package br.com.rbcti.tlv;

/**
 * Thrown to indicate that data decoding was unsuccessful.
 *
 * @author Renato Cunha
 * @version 1.0
 */
public class DecodeTLVException extends Exception {

    private static final long serialVersionUID = -2471994922332836822L;

    public DecodeTLVException() {
        super();
    }

    public DecodeTLVException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DecodeTLVException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecodeTLVException(String message) {
        super(message);
    }

    public DecodeTLVException(Throwable cause) {
        super(cause);
    }

}
