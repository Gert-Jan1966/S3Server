package nl.ou.s3server.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
//import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

//import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.ou.s3.common.LocationDto;
import nl.ou.s3server.domain.PolicyException;
import nl.ou.s3server.domain.PoliciesPolicy;
import nl.ou.s3server.domain.SymmetricKey;
import nl.ou.s3server.repository.SymmetricKeyRepository;

/**
 * RESTful controller voor afhandeling van requests naar /S3Server/symmetrickey.
 */
@RestController
@RequestMapping("/symmetrickey")
public class SymmetricKeyController {

    Logger logger = LoggerFactory.getLogger(SymmetricKeyController.class);

    @Autowired
    private PoliciesPolicy policiesPolicy;
    
    @Autowired
    private SymmetricKeyRepository symmetricKeyRepository;
    
//    /**
//     * Retourneert een JSON-array met alle opgeslagen symmetric keys.<br>
//     * ===>>> NIET IN DE PRODUCTIEVE VERSIE OPNEMEN!!!!!!! <<<===
//     */
//    @RequestMapping(method = GET)
//    public List<SymmetricKey> findAllKeys() {
//        logger.warn("Iemand heeft alle opgeslagen keys opgehaald!");
//        return symmetricKeyRepository.findAll();
//    }
    
    /**
     * Opvragen SymmetricKey m.b.v. een id en meegestuurde locatiegegevens.<br>
     * Gebruikt returntype <i>ResponseEntity</i>, dit geeft meer flexibiliteit voor terugsturen van meldingen e.d.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(value = "/{id}", method = POST)
    public ResponseEntity<?> findKey(@PathVariable("id") String id, @RequestBody LocationDto location) {
        logger.warn("Ontvangen locatiegegevens: " + location.toString());
        
        SymmetricKey key = symmetricKeyRepository.findOne(id);
        try {
            policiesPolicy.checkForCompliance(key, location);
            return new ResponseEntity(key, HttpStatus.OK);
        } catch (PolicyException pe) {
            return new ResponseEntity(pe.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Opslaan van een nieuwe symmetrische key.
     */
    @RequestMapping(method = POST)
    public SymmetricKey createKey(@RequestBody SymmetricKey symmetricKey) {
        return symmetricKeyRepository.save(symmetricKey);
    }
    
    /**
     * Updaten van een bestaande symmetrische key.
     */
    @RequestMapping(value = "/{id}", method = PUT)
    public SymmetricKey updateKey(@RequestBody SymmetricKey symmetricKey) {
        return symmetricKeyRepository.save(symmetricKey);
    }

    /**
     * Verwijderen SymmetricKey m.b.v. een id.<br>
     * Er wordt geen bevestiging teruggestuurd.
     */
    @RequestMapping(value = "/{id}", method = DELETE)
    public void deleteKey(@PathVariable(value = "id") String id) {
        logger.warn("Verwijderen verzocht voor key: " + id);
        
        symmetricKeyRepository.delete(id);
    }

}
