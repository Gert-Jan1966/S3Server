package nl.ou.s3server.domain

import groovy.transform.CompileStatic

import javax.validation.constraints.Future
import javax.validation.constraints.NotNull

/**
 * Deze klasse implementeert de benodigde gegevens & bewerkingen voor de expiry-policy.
 */
@CompileStatic
class S3ExpirationPolicy implements Serializable {

    /**
     * Timestamp die verloopdatum & -tijd voor een selfie bevat.<br>
     * Mag niet leeg zijn en de waarde moet in de toekomst liggen.
     */
    @NotNull(message="Veld expiryTimestamp mag niet leeg zijn.")
    @Future(message="Timestamp in expiryTimestamp moet in de toekomst liggen.")
    Date expiryTimestamp

}
