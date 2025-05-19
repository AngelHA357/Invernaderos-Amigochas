package org.itson.Anomalyzer.configurations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = {"org.itson.Anomalyzer.persistence.alarmas"},
        mongoTemplateRef = AlarmaDBConfig.MONGO_TEMPLATE
)
public class AlarmaDBConfig {
    protected static final String MONGO_TEMPLATE = "alarmaMongoTemplate";
}