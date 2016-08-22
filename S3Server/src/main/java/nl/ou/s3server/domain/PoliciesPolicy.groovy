package nl.ou.s3server.domain

import java.time.LocalDateTime
import java.time.ZoneId

import nl.ou.s3server.config.GoogleGeocodingApi

import org.springframework.stereotype.Component

import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.GeocodingApiRequest
import com.google.maps.model.AddressType
import com.google.maps.model.GeocodingResult
import com.google.maps.model.LatLng

/**
 * Bevat de logica voor het checken van aangeleverde data tegen mogelijk aanwezige policies.
 */
@Component
class PoliciesPolicy {
    
    static final String EXPIRATION_ERROR = "EXPIRATION_ERROR: Geldigheid van selfie is verlopen!"
    static final String LOCATION_ERROR = "LOCATION_ERROR:"
    
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
        
        String locatieFoutmelding = null
        
        // Valideer de locationPolicy.
        if (key.locationPolicy) {
            if (!isLocationPolicyCompliant(key, location, locatieFoutmelding)) {
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
     *         <i>Neveneffect:</i> bij foutsituaties wordt de invoerparameter <i>locatieFoutmelding</i> gevuld. 
     */
    private Boolean isLocationPolicyCompliant(SymmetricKey key, LocationDto location, String locatieFoutmelding) {
        GeoApiContext geoApiContext = new GeoApiContext().setApiKey(GoogleGeocodingApi.API_KEY)
        GeocodingResult[] result = null
        LatLng latLng = new LatLng(location.latitude, location.longitude)
        GeocodingApiRequest request = GeocodingApi.reverseGeocode(geoApiContext, latLng).resultType(AddressType.POSTAL_CODE).language("nl")
        
        // Probeer de postcode van meegestuurde locatie te bepalen.
        try {
            result = request.await()
        } catch (Exception e) {
            locatieFoutmelding = "${LOCATION_ERROR} ${e.message}"
            return false
        }
        
        // Geef foutmelding als we geen resultaat hebben ontvangen.
        if (!result) {
            locatieFoutmelding = "${LOCATION_ERROR} Response vanuit GeocodingAPI is leeg!"
            return false
        }

        // Kijk of de 4 cijfers van de postcodes met elkaar matchen.
        if (result[0].formattedAddress[0..3] != key.locationPolicy.postalCode[0..3]) {
            locatieFoutmelding = "${LOCATION_ERROR} Er wordt niet aan de locatiebeperking voldaan!"
            return false
        }
        
        true
    }

}
