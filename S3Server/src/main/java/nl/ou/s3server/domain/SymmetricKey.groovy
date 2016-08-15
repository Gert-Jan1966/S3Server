package nl.ou.s3server.domain

import groovy.transform.CompileStatic

import javax.validation.Valid

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
    
    /** Optionele Policy t.b.v. tijdstip einde geldigheid.  */
    @Valid
    S3ExpirationPolicy expirationPolicy
    
    /** Optionele Policy m.b.t. locatie van gebruiker/device. */
    @Valid
    S3LocationPolicy locationPolicy
    
    /** De daadwerkelijke symmetrische key. */
    @NotEmpty(message="Veld key mag niet leeg zijn.")
    String key

}
