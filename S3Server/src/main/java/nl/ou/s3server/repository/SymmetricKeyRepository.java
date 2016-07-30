package nl.ou.s3server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import nl.ou.s3server.domain.SymmetricKey;

/**
 * Spring Data gebaseerde repository voor MongoDB SymmetricKey documents.<br>
 * 
 * Voor beschrijving van het Repository pattern, zie: 
 * <a href="http://martinfowler.com/eaaCatalog/repository.html">http://martinfowler.com/eaaCatalog/repository.html</a>.
 */
public interface SymmetricKeyRepository extends MongoRepository<SymmetricKey, String> {

}
