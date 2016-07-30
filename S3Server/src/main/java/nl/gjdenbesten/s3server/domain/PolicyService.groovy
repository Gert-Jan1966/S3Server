package nl.gjdenbesten.s3server.domain

import static nl.gjdenbesten.s3server.config.GoogleGeocodingApi.API_KEY

import java.time.LocalDateTime
import java.time.ZoneId

import org.springframework.stereotype.Service

import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.AddressType
import com.google.maps.model.GeocodingResult
import com.google.maps.model.LatLng

import nl.gjdenbesten.s3.common.LocationDto

/**
 * Bevat het 'transaction script' voor afhandeling van logica voor het checken tegen policies.<br>
 * 
 * Voor beschrijving van het Transaction Script pattern, zie: 
 * <a href="http://martinfowler.com/eaaCatalog/transactionScript.html">http://martinfowler.com/eaaCatalog/transactionScript.html</a>.
 */
@Service
class PolicyService {
    
    static final String EXPIRATION_ERROR = "EXPIRATION_ERROR: Geldigheid van selfie is verlopen!"
    static final String LOCATION_ERROR = "LOCATION_ERROR:"

    String locatieFoutmelding = null
    
    /**
     * Zoek de gewenste key en controleer vervolgens of aan aanwezige policies wordt voldaan.
     */
    Boolean checkForCompliance(SymmetricKey key, LocationDto location) throws PolicyException {

        // Check de expirationPolicy.
        if (key.expirationPolicy) {
            if (!isExpirationPolicyCompliant(key)) {
                throw new PolicyException(EXPIRATION_ERROR)
            }
        }
        
        // Check de locationPolicy.
        if (key.locationPolicy) {
            if (!isLocationPolicyCompliant(key, location)) {
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
     */
    private Boolean isLocationPolicyCompliant(SymmetricKey key, LocationDto location) {
        GeoApiContext geoApiContext = new GeoApiContext().setApiKey(API_KEY)
        GeocodingResult[] result = null
        
        // Probeer de postcode van meegestuurde locatie te bepalen.
        try {
            LatLng latLng = new LatLng(location.latitude, location.longitude)
            result = GeocodingApi.reverseGeocode(geoApiContext, latLng).resultType(AddressType.POSTAL_CODE)
                    .language("nl").await()
        } catch (Exception e) {
            locatieFoutmelding = "${LOCATION_ERROR} ${e.message}"
            return false
        }
        
        // Geef foutmelding als we geen resultaat hebben ontvangen
        if (result == null || result.length == 0) {
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
