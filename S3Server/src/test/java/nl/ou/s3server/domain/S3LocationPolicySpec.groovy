package nl.ou.s3server.domain

import javax.validation.Validation
import javax.validation.Validator

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title
import spock.lang.Unroll

@Title("Unittest voor S3LocationPolicy")
@Subject(S3LocationPolicy)
class S3LocationPolicySpec extends Specification {
    
    @Shared
    Validator validator = Validation.buildDefaultValidatorFactory().validator

    @Unroll
    def "Test validatieconstraints postalCode = #postalCode"() {
        when:
        def s3LocationPolicy = new S3LocationPolicy(postalCode: postalCode)
       
        then:
        validator.validate(s3LocationPolicy).size() == violations
       
        where:
        postalCode || violations
        null       ||     1
        "a"        ||     1
        "1"        ||     1
        "aaaaa"    ||     1
        "11111"    ||     1
        "bbbb"     ||     1
        "1111"     ||     0
    }
    
}
