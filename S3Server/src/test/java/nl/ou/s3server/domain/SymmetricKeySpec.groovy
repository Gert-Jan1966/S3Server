package nl.ou.s3server.domain

import javax.validation.Validation
import javax.validation.Validator

import nl.ou.s3server.domain.SymmetricKey;
import spock.lang.*

@Title("Unittest voor SymmetricKey")
@Subject(SymmetricKey)
class SymmetricKeySpec extends Specification {
   
    @Shared
    Validator validator = Validation.buildDefaultValidatorFactory().validator

    @Unroll
    def "Test validatieconstraints op velden SymmetricKey"() {
        when:
        def symmetricKey = new SymmetricKey(key: key)
       
        then:
        validator.validate(symmetricKey).size() == violations
       
        where:
        key  || violations
        null ||     1    
        ""   ||     1
        "A"  ||     0
    }
    
    def "Test transitieve constraints m.b.t. Policies in SymmetricKey"() {
        when: "SymmetricKey 1 of 2 Policies bevat"
        def locationPolicy = new S3LocationPolicy()
        def expirationPolicy = new S3ExpirationPolicy()
        def symmetricKey = new SymmetricKey(
                locationPolicy: locationPolicy, expirationPolicy: expirationPolicy, key: "key")
        
        then: "moeten deze Policies zelf ook ingevulde waarden hebben"
        validator.validate(symmetricKey).size() == 2
    }

}
