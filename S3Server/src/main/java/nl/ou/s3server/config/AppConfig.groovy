package nl.ou.s3server.config

import io.beanmapper.BeanMapper

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

import com.mongodb.Mongo
import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.WriteConcern

/**
 * Spring configuratie o.b.v. de AbstractMongoConfiguration klasse.
 */
@Configuration
@ComponentScan(basePackages="nl.ou.s3server.domain")
@EnableMongoRepositories(basePackages="nl.ou.s3server.repository")
@PropertySource("classpath:mongodb.properties")
class AppConfig extends AbstractMongoConfiguration {

    // Interpreteren van de benodigde properties uit mongodb.properties. 
    private @Value('${mongo.dbname}') String dbname
    private @Value('${mongo.pass}') char[] pass
    private @Value('${mongo.portnr}') Integer portnr
    private @Value('${mongo.servername}') String servername
    private @Value('${mongo.user}') String user
    
    // Vereist door de AbstractMongoConfiguration klasse.
    @Override
    protected String getDatabaseName() {
        dbname
    }

    @Override
    Mongo mongo() throws Exception {
        def serverAddress = new ServerAddress(servername, portnr)
        def credential = Arrays.asList( MongoCredential.createCredential(user, dbname, pass) )
        def mongo = new MongoClient(serverAddress, credential)
        mongo.writeConcern = WriteConcern.SAFE
        mongo
    }

    @Override
    @Bean(name="template")
    MongoTemplate mongoTemplate() throws Exception {

        // We willen geen "_class" veld in de MongoDB collections zien!
        def converter = new MappingMongoConverter(mongoDbFactory(), new MongoMappingContext())
        converter.typeMapper = new DefaultMongoTypeMapper(null)
        new MongoTemplate(mongoDbFactory(), converter)
    }

    /**
     * BeanMapper biedt functionaliteit om eenvoudig data tussen Java en/of Groovy klassen te kopieren.<br>
     * Zie ook: <a href="http://beanmapper.io/">http://beanmapper.io/</a>.
     */
    @Bean
    BeanMapper beanMapper() {
        new BeanMapper()
    }
    
}
