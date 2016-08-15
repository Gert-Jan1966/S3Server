package nl.ou.s3server.domain

import groovy.transform.CompileStatic

import org.hibernate.validator.constraints.NotEmpty

/**
 * Deze klasse implementeert de benodigde gegevens & bewerkingen voor de locatie-policy.
 */
@CompileStatic
class S3LocationPolicy implements Serializable {

    /**
     * De postcode waarbinnen de locatie zich moet bevinden.
     */
    @NotEmpty(message="Veld postalCode mag niet leeg zijn.")
    String postalCode

}
