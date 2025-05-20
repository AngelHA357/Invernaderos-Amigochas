package org.itson.ReportesAnomalias.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = {"org.itson.ReportesAnomalias.persistence.reportes"},
        mongoTemplateRef = ReporteDBConfig.MONGO_TEMPLATE
)
public class ReporteDBConfig {

    protected static final String MONGO_TEMPLATE = "reporteMongoTemplate";
}