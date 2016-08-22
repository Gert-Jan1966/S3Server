package nl.ou.s3server.domain

import groovy.transform.CompileStatic

import javax.validation.constraints.Pattern

import org.hibernate.validator.constraints.NotEmpty

/**
 * Deze klasse implementeert de benodigde gegevens & bewerkingen voor de locatie-policy.
 */
@CompileStatic
class S3LocationPolicy implements Serializable {

    /**
     * De postcode waarbinnen de locatie zich moet bevinden.<br>
     * Mag niet leeg zijn en moet uit precies 4 cijfers bestaan.
     */
    @NotEmpty(message="Veld postalCode mag niet leeg zijn.")
    @Pattern(regexp="\\d{4}", message="Veld postalCode moet uit 4 cijfers bestaan.")
    String postalCode

}
