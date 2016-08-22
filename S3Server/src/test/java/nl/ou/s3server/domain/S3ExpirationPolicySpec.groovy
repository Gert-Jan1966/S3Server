package nl.ou.s3server.domain

import javax.validation.Validation
import javax.validation.Validator

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title
import spock.lang.Unroll

@Title("Unittest voor S3ExpirationPolicy")
@Subject(S3ExpirationPolicy)
class S3ExpirationPolicySpec extends Specification {
    
    @Shared
    Validator validator = Validation.buildDefaultValidatorFactory().validator

    @Unroll
    def "Test validatieconstraints expiryTimestamp = #expiryTimestamp"() {
        when:
        def s3ExpirationPolicy = new S3ExpirationPolicy(expiryTimestamp: expiryTimestamp)
       
        then:
        validator.validate(s3ExpirationPolicy).size() == violations
       
        where:
        expiryTimestamp      || violations
        null                 ||     1
        new Date(70,0,1)     ||     1        // 1 januari 1970 00:00:00
        new Date(8099,11,31) ||     0        // 31 december 9999 00:00:00

    }
    
}
