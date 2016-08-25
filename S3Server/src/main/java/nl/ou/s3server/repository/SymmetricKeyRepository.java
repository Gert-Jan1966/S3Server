package nl.ou.s3server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import nl.ou.s3server.domain.SymmetricKey;

/**
 * Spring Data gebaseerde repository voor MongoDB SymmetricKey documents.<br>
 * <br>
 * Voor beschrijving van het Repository pattern, zie: 
 * <a href="http://martinfowler.com/eaaCatalog/repository.html">http://martinfowler.com/eaaCatalog/repository.html</a>.<br>
 * <br>
 * Voor de beschrijving van de standaard methods die worden gegenereerd, zie:<br>
 * <a href="http://docs.spring.io/spring-data/mongodb/docs/1.9.2.RELEASE/api/org/springframework/data/mongodb/repository/MongoRepository.html">
 * MongoRepository (Spring Data MongoDB 1.9.2)</a>.<br>
 */
public interface SymmetricKeyRepository extends MongoRepository<SymmetricKey, String> {

}
