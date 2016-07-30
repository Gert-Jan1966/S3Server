package nl.ou.s3server.domain;

/**
 * Exception voor gebruik binnen S3Server t.b.v. policy-specifieke fouten.
 */
@SuppressWarnings("serial")
public class PolicyException extends Exception {

    public PolicyException(String message) {
        super(message);
    }
    
}
