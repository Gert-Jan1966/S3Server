package nl.ou.s3server.domain

import groovy.transform.CompileStatic

import javax.validation.constraints.NotNull

/**
 * Deze klasse implementeert de benodigde gegevens & bewerkingen voor de expiry-policy.
 */
@CompileStatic
class S3ExpirationPolicy implements Serializable {

    /**
     * Timestamp die verloopdatum & -tijd voor een selfie bevat.
     */
    @NotNull(message="Veld expiryTimestamp mag niet leeg zijn.")
    Date expiryTimestamp

}
