package nl.ou.s3server.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nl.ou.s3server.domain.LocationDto;
import nl.ou.s3server.domain.PoliciesPolicy;
import nl.ou.s3server.domain.PolicyException;
import nl.ou.s3server.domain.SymmetricKey;
import nl.ou.s3server.repository.SymmetricKeyRepository;

/**
 * RESTful controller voor afhandeling van requests naar /S3Server/symmetrickey.
 */
@RestController
@RequestMapping("/symmetrickey")
public class SymmetricKeyController {

    public static final String NOT_AUTHORIZED = "AUTHORIZATION_ERROR: U bent niet bevoegd deze aktie uit te voeren!";
    
    Logger logger = LoggerFactory.getLogger(SymmetricKeyController.class);

    @Autowired
    private PoliciesPolicy policiesPolicy;
    
    @Autowired
    private SymmetricKeyRepository symmetricKeyRepository;
    
    /**
     * Opvragen SymmetricKey m.b.v. een id en meegestuurde locatiegegevens.<br>
     * Gebruikt returntype <i>ResponseEntity</i>, dit geeft meer flexibiliteit voor terugsturen van meldingen e.d.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(value = "/{id}", method = POST)
    public ResponseEntity<?> findKey(
            @PathVariable("id") String id,
            @RequestParam("user") String user, 
            @RequestBody LocationDto location) {
        
        logger.info("Ontvangen locatiegegevens: " + location.toString());

        SymmetricKey key = symmetricKeyRepository.findOne(id);
        
        // Mag de gebruiker deze actie uitvoeren?
        if (!key.getOwner().equals(user)) {
            return new ResponseEntity(NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        
        try {
            policiesPolicy.checkPolicies(key, location);
            return new ResponseEntity(key, HttpStatus.OK);
        } catch (PolicyException pe) {
            return new ResponseEntity(pe.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Opslaan van een nieuwe symmetrische key.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(method = POST)
    public ResponseEntity<?> createKey(@RequestParam("user") String user, @RequestBody SymmetricKey symmetricKey) {
        
        // Mag de gebruiker deze actie uitvoeren?
        if (!symmetricKey.getOwner().equals(user)) {
            return new ResponseEntity(NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        
        SymmetricKey storedKey = symmetricKeyRepository.save(symmetricKey);
        return new ResponseEntity(storedKey, HttpStatus.OK);
    }
    
    /**
     * Updaten van een bestaande symmetrische key.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(value = "/{id}", method = PUT)
    public ResponseEntity<?> updateKey(
            @PathVariable(value = "id") String id, 
            @RequestParam("user") String user, 
            @RequestBody SymmetricKey symmetricKey) {
        
        // Mag de gebruiker deze actie uitvoeren?
        SymmetricKey key = symmetricKeyRepository.findOne(id);
        if (!key.getOwner().equals(user)) {
            return new ResponseEntity(NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        
        SymmetricKey storedKey = symmetricKeyRepository.save(symmetricKey);
        return new ResponseEntity(storedKey, HttpStatus.OK);
    }

    /**
     * Verwijderen SymmetricKey m.b.v. een id.<br>
     * Er wordt geen bevestiging teruggestuurd.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(value = "/{id}", method = DELETE)
    public ResponseEntity<?> deleteKey(@PathVariable(value = "id") String id, @RequestParam("user") String user) {
        logger.info("Verwijderen verzocht voor key: " + id + " door gebruiker: " + user);
        
        // Mag de gebruiker deze actie uitvoeren?
        SymmetricKey key = symmetricKeyRepository.findOne(id);
        if (!key.getOwner().equals(user)) {
            return new ResponseEntity(NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        
        symmetricKeyRepository.delete(id);
        return new ResponseEntity("", HttpStatus.OK);
    }

}
