package org.itson.ReportesAnomalias.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = {"org.itson.ReportesAnomalias.persistence.anomalias"},
        mongoTemplateRef = AnomaliaDBConfig.MONGO_TEMPLATE
)
public class AnomaliaDBConfig {
    protected static final String MONGO_TEMPLATE = "anomaliaMongoTemplate";
}