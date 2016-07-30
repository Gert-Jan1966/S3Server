package nl.ou.s3server.domain

import nl.ou.s3.common.S3ExpirationPolicy
import nl.ou.s3server.domain.PolicyException;
import nl.ou.s3server.domain.PolicyService;
import nl.ou.s3server.domain.SymmetricKey;
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

@Title("Unittest voor PolicyService")
@Subject(PolicyService)
class PolicyServiceSpec extends Specification {

    @Shared
    PolicyService policyService = new PolicyService()
    
    // Maak date/timestamp van vandaag aan & zet het tijdgedeelte op 0.    
    @Shared
    S3ExpirationPolicy verlopenExpirationPolicy = new S3ExpirationPolicy(expiryTimestamp: new Date().clearTime())
    
    // Maak date/timestamp voor 'morgen' aan.
    @Shared
    S3ExpirationPolicy geldigeExpirationPolicy = new S3ExpirationPolicy(expiryTimestamp: new Date().next())
   
    def "Test checkForCompliance voor expirationPolicy met verlopen geldigheid"() {
        given:
        SymmetricKey keyVerlopen = new SymmetricKey(expirationPolicy: verlopenExpirationPolicy)

        when:
        policyService.checkForCompliance(keyVerlopen)

        then:
        PolicyException exception = thrown()
        exception.message.contains(PolicyService.EXPIRATION_ERROR)
    }
    
    def "Test checkForCompliance voor expirationPolicy met nog geldige verloopdatum/-tijd"() {
        when:
        SymmetricKey keyNogGeldig = new SymmetricKey(expirationPolicy: geldigeExpirationPolicy)

        then:
        policyService.checkForCompliance(keyNogGeldig)
    }
    
}
