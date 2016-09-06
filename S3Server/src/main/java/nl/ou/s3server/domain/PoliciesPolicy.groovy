package nl.ou.s3server.domain

import java.time.LocalDateTime
import java.time.ZoneId

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component

import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.GeocodingApiRequest
import com.google.maps.model.AddressType
import com.google.maps.model.GeocodingResult
import com.google.maps.model.LatLng

import nl.ou.s3server.controller.SymmetricKeyController;

/**
 * Bevat de logica voor het checken van aangeleverde data tegen mogelijk aanwezige policies.
 */
class PoliciesPolicy { 
    Logger logger = LoggerFactory.getLogger(PoliciesPolicy.class);
    
    static final String EXPIRATION_ERROR = "EXPIRATION_ERROR: Geldigheid van selfie is verlopen!"
    static final String LOCATION_ERROR = "LOCATION_ERROR:"
    
    String googleApiKey
    
    /**
     * Controleer of aan eventueel bij <i>key</i> aanwezige policies wordt voldaan.
     * 
     * @return True indien aan van toepassing zijnde policies is voldaan.
     * @throws PolicyException indien <u>niet</u> aan van toepassing zijnde policies is voldaan.
     */
    Boolean checkPolicies(SymmetricKey key, LocationDto location) throws PolicyException {

        // Valideer de expirationPolicy.
        if (key.expirationPolicy) {
            if (!isExpirationPolicyCompliant(key)) {
                throw new PolicyException(EXPIRATION_ERROR)
            }
        }
        
        // Valideer de locationPolicy.
        if (key.locationPolicy) {
            String locatieFoutmelding = isLocationPolicyCompliant(key, location)
            
            if (locatieFoutmelding) {
                throw new PolicyException(locatieFoutmelding)
            }
        }
        
        true
    }
    
    /**
     * Wordt aan de expirationPolicy voldaan?<br>
     * Ofwel: Is de verlooptijd van de selfie nog niet verstreken?<br><br>
     * 
     * Dit is Java 8, dus we gebruiken de nieuwe java.time API dus wel de Java Date uit de policy converteren!
     * 
     * @return Is "huidige date/timestamp" < "tijdstip einde geldigheid" ?
     */
    private Boolean isExpirationPolicyCompliant(SymmetricKey key) {
        LocalDateTime now = LocalDateTime.now()
        
        def expiryTimeStamp = key.expirationPolicy.expiryTimestamp
        LocalDateTime expDateTime = LocalDateTime.ofInstant(expiryTimeStamp.toInstant(), ZoneId.systemDefault())
        
        now.isBefore(expDateTime)
    }
    
    /**
     * Wordt aan de locationPolicy voldaan?<br>
     * Ofwel: valt de meegestuurde locatie binnen de postcode van de policy?
     * 
     * @return Komt de postcode volgend uit de ontvangen coordinaten overeen met postcode uit de policy?<br>
     *         Bij foutsituaties wordt de locatieFoutmelding teruggegeven, anders wordt null geretourneerd. 
     */
    private String isLocationPolicyCompliant(SymmetricKey key, LocationDto location) {
        GeoApiContext geoApiContext = new GeoApiContext().setApiKey(googleApiKey)
        GeocodingResult[] result = null
        LatLng latLng = new LatLng(location.latitude, location.longitude)
        
        // Probeer de postcode van meegestuurde locatie te bepalen.
        try {
            result = GeocodingApi.reverseGeocode(geoApiContext, latLng).resultType(AddressType.POSTAL_CODE)
                    .language("nl").await()
        } catch (Exception e) {
            return "${LOCATION_ERROR} ${e.message}"
        }
        
        // Geef foutmelding als we geen resultaat hebben ontvangen.
        if (!result) {
            return "${LOCATION_ERROR} Response vanuit GeocodingAPI is leeg!"
        }

        // Kijk of de 4 cijfers van de postcodes met elkaar matchen.
        if (result[0].formattedAddress[0..3] != key.locationPolicy.postalCode[0..3]) {
            return "${LOCATION_ERROR} Er wordt niet aan de locatiebeperking voldaan!"
        }
        
        null
    }

}
