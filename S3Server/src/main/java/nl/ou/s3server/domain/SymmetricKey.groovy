package nl.ou.s3server.domain

import groovy.transform.CompileStatic

import javax.validation.Valid

import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Deze Document-klasse wordt gemapped op documents van de SymmetricKey Collection in MongoDB.
 */
@Document(collection="SymmetricKey")
@CompileStatic
class SymmetricKey {

    @Id
    String id
    
    @NotEmpty(message="Veld owner mag niet leeg zijn.")
    @Email(message="Veld owner moet een geldig emailadres bevatten.")
    String owner

//    List<String> recipients
    
    /** Optionele Policy t.b.v. tijdstip einde geldigheid. */
    @Valid
    S3ExpirationPolicy expirationPolicy
    
    /** Optionele Policy m.b.t. locatie van gebruiker/device. */
    @Valid
    S3LocationPolicy locationPolicy
    
    /** De daadwerkelijke symmetrische key. */
    @NotEmpty(message="Veld key mag niet leeg zijn.")
    String key

}
