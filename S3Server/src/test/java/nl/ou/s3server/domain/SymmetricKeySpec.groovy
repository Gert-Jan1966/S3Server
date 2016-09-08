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
        def symmetricKey = new SymmetricKey(key: key, owner: owner)
       
        then:
        validator.validate(symmetricKey).size() == violations
       
        where:
        key  |    owner    || violations
        null |    null     ||     2
        null |   "a@a.a"   ||     1    
        ""   |   "a@a.a"   ||     1
        "A"  |   "a@a.a"   ||     0
        "A"  |    null     ||     1
        "A"  |     ""      ||     1
        "A"  |    "a"      ||     1
    }
    
    def "Test transitieve constraints m.b.t. Policies in SymmetricKey"() {
        when: "SymmetricKey 2 lege Policies bevat"
        def locationPolicy = new S3LocationPolicy()
        def expirationPolicy = new S3ExpirationPolicy()
        def symmetricKey = new SymmetricKey(
                locationPolicy: locationPolicy, expirationPolicy: expirationPolicy, key: "key", owner: "a@a.a")
        
        then: "moeten deze Policies zelf ook ingevulde waarden hebben"
        validator.validate(symmetricKey).size() == 2
    }

}
