package org.example.ic_lab1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class JpaConfig {

    private final DataSource dataSource;

    public JpaConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("org.example.ic_lab1.domain");

        EclipseLinkJpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        em.setJpaProperties(eclipselinkProperties());

        return em;
    }

    private java.util.Properties eclipselinkProperties() {
        java.util.Properties properties = new java.util.Properties();
        properties.setProperty("eclipselink.target-database", "PostgreSQL");
        properties.setProperty("eclipselink.logging.level", "FINE");
        properties.setProperty("eclipselink.logging.level.sql", "FINE");
        properties.setProperty("eclipselink.logging.parameters", "true");
        properties.setProperty("eclipselink.jdbc.batch-writing", "JDBC");
        properties.setProperty("eclipselink.jdbc.batch-writing.size", "1000");
        properties.setProperty("eclipselink.cache.shared.default", "false");
        properties.setProperty("eclipselink.ddl-generation", "create-tables");
        return properties;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory);
        return tm;
    }
}