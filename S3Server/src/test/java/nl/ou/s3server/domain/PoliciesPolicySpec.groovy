package nl.ou.s3server.domain

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

@Title("Unittest voor PoliciesPolicy")
@Subject(PoliciesPolicy)
class PoliciesPolicySpec extends Specification {

    @Shared
    PoliciesPolicy policyPolicy = new PoliciesPolicy()
    
    @Shared
    LocationDto nullLocation = null
    
    // Maak date/timestamp van vandaag aan & zet het tijdgedeelte op 0.    
    @Shared
    S3ExpirationPolicy verlopenExpirationPolicy = new S3ExpirationPolicy(expiryTimestamp: new Date().clearTime())
    
    // Maak date/timestamp voor 'morgen' aan.
    @Shared
    S3ExpirationPolicy geldigeExpirationPolicy = new S3ExpirationPolicy(expiryTimestamp: new Date().next())
   
    def "Test checkForCompliance voor expirationPolicy met verlopen geldigheid"() {
        given: "Een key document waarbij de datum in de expirationPolicy verlopen is"
        SymmetricKey keyVerlopen = new SymmetricKey(expirationPolicy: verlopenExpirationPolicy)

        when: "Tegen de policies wordt gechecked"
        policyPolicy.checkPolicies(keyVerlopen, nullLocation)

        then: "Word een PolicyExeption met EXPIRATION_ERROR gegenereerd"
        PolicyException exception = thrown()
        exception.message.contains(PoliciesPolicy.EXPIRATION_ERROR)
    }
    
    def "Test checkForCompliance voor expirationPolicy met nog geldige verloopdatum/-tijd"() {
        when: "Een key document waarbij de datum in de expirationPolicy nog niet verlopen is"
        SymmetricKey keyNogGeldig = new SymmetricKey(expirationPolicy: geldigeExpirationPolicy)

        then: "Check tegen de policies slaagt"
        policyPolicy.checkPolicies(keyNogGeldig, nullLocation)
    }
    
}
