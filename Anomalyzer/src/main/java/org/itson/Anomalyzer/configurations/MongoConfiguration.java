package org.itson.Anomalyzer.configurations;

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
    @Bean(name = "anomaliaDBProperties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.anomalyzer")
    public MongoProperties getAnomaliaProps() {
        return new MongoProperties();
    }

    @Bean(name = "alarmaDBProperties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.alarmas")
    public MongoProperties getAlarmaProps() {
        return new MongoProperties();
    }

    @Primary
    @Bean(name = "anomaliaMongoTemplate")
    public MongoTemplate anomaliasMongoTemplate() throws Exception {
        return new MongoTemplate(anomaliaMongoDatabaseFactory(getAnomaliaProps()));
    }

    @Bean(name ="alarmaMongoTemplate")
    public MongoTemplate alarmaMongoTemplate() throws Exception {
        return new MongoTemplate(alarmaMongoDatabaseFactory(getAlarmaProps()));
    }

    @Primary
    @Bean
    public MongoDatabaseFactory anomaliaMongoDatabaseFactory(MongoProperties mongo) throws Exception {
        return new SimpleMongoClientDatabaseFactory(
                mongo.getUri()
        );
    }

    @Bean
    public MongoDatabaseFactory alarmaMongoDatabaseFactory(MongoProperties mongo) throws Exception {
        return new SimpleMongoClientDatabaseFactory(
                mongo.getUri()
        );
    }
}