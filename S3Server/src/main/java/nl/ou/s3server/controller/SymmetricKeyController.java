package nl.ou.s3server.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.ou.s3.common.LocationDto;
import nl.ou.s3server.domain.PolicyException;
import nl.ou.s3server.domain.PolicyService;
import nl.ou.s3server.domain.SymmetricKey;
import nl.ou.s3server.repository.SymmetricKeyRepository;

/**
 * RESTful controller voor afhandeling van requests naar /S3Server/symmetrickey.
 */
@RestController
@RequestMapping("/symmetrickey")
public class SymmetricKeyController {

    @Autowired
    private PolicyService policyService;
    
    @Autowired
    private SymmetricKeyRepository symmetricKeyRepository;

    /**
     * Opvragen SymmetricKey m.b.v. een id en meegestuurde locatiegegevens.<br>
     * Gebruikt returntype <i>ResponseEntity</i>, dit geeft meer flexibiliteit voor terugsturen van meldingen e.d.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(value = "/{id}", method = POST)
    public ResponseEntity<?> findKey(@PathVariable("id") String id, @RequestBody LocationDto location) {
        SymmetricKey key = symmetricKeyRepository.findOne(id);
        
        try {
            policyService.checkForCompliance(key, location);
            return new ResponseEntity(key, HttpStatus.OK);
        } catch (PolicyException pe) {
            return new ResponseEntity(pe.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Opslaan van een nieuwe symmetrische key.
     */
    @RequestMapping(method = POST)
    public SymmetricKey create(@RequestBody SymmetricKey symmetricKey) {
        return symmetricKeyRepository.save(symmetricKey);
    }
    
    /**
     * Updaten van een bestaande symmetrische key.
     */
    @RequestMapping(value = "/{id}", method = PUT)
    public SymmetricKey update(@RequestBody SymmetricKey symmetricKey) {
        return symmetricKeyRepository.save(symmetricKey);
    }

    /**
     * Verwijderen SymmetricKey m.b.v. een id.<br>
     * Er wordt geen bevestiging teruggestuurd.
     */
    @RequestMapping(value = "/{id}", method = DELETE)
    public void deleteKey(@PathVariable(value = "id") String id) {
        symmetricKeyRepository.delete(id);
    }

}
