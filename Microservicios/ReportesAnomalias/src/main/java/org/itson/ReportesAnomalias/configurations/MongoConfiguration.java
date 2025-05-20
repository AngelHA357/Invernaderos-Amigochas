package org.itson.ReportesAnomalias.configurations;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoConfiguration {

    @Primary
    @Bean(name = "reporteDBProperties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.reportes")
    public MongoProperties getReporteProps() {
        return new MongoProperties();
    }

    @Bean(name = "anomaliaDBProperties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.anomalyzer")
    public MongoProperties getAnomaliaProps() {
        return new MongoProperties();
    }

    @Primary
    @Bean(name = "reporteMongoTemplate")
    public MongoTemplate reportesMongoTemplate() throws Exception {
        return new MongoTemplate(reporteMongoDatabaseFactory(getReporteProps()));
    }

    @Bean(name ="anomaliaMongoTemplate")
    public MongoTemplate anomaliaMongoTemplate() throws Exception {
        return new MongoTemplate(anomaliaMongoDatabaseFactory(getAnomaliaProps()));
    }

    @Primary
    @Bean
    public MongoDatabaseFactory reporteMongoDatabaseFactory(MongoProperties mongo) throws Exception {
        return new SimpleMongoClientDatabaseFactory(
                mongo.getUri()
        );
    }

    @Bean
    public MongoDatabaseFactory anomaliaMongoDatabaseFactory(MongoProperties mongo) throws Exception {
        return new SimpleMongoClientDatabaseFactory(
                mongo.getUri()
        );
    }
}