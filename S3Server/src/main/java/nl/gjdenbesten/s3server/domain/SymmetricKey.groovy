package nl.gjdenbesten.s3server.domain

import groovy.transform.CompileStatic
import nl.gjdenbesten.s3.common.S3ExpirationPolicy;
import nl.gjdenbesten.s3.common.S3LocationPolicy;

import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Deze Document-klasse wordt gemapped op documents van de SymmetricKey Collection in MongoDB.
 */
@CompileStatic
@Document(collection="SymmetricKey")
class SymmetricKey {

    /** Dit id wordt door de S3App gebruikt om een key op te vragen. */
    @Id 
    String id
    
    /* 
     * Alle policies waaraan voldaan moet worden voordat de key wordt verstrekt. 
     */
    S3ExpirationPolicy expirationPolicy
    S3LocationPolicy locationPolicy
    
    /** De daadwerkelijke symmetrische key. */
    @NotEmpty(message="Veld key mag niet leeg zijn.")
    String key

}
