package com.cjrequena.sample.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 *
 * @author cjrequena
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
  entityManagerFactoryRef = "entityManagerFactoryPostgres", transactionManagerRef = "transactionManagerPostgres",
  basePackages = {"com.cjrequena.sample.db.repository"}
)
public class PostgresConfiguration {

  @Bean(name = "dataSourcePostgres", destroyMethod = "")
  @Validated
  @ConfigurationProperties(prefix = "spring.datasource.postgres")
  @ConditionalOnClass({HikariDataSource.class})
  public HikariDataSource dataSourcePostgres() {
    return new HikariDataSource();
  }

  @Bean("entityManagerFactoryPostgres")
  public LocalContainerEntityManagerFactoryBean entityManagerFactoryPostgres(EntityManagerFactoryBuilder builder, @Qualifier("dataSourcePostgres") DataSource dataSource) {
    return builder
      .dataSource(dataSource)
      .packages("com.cjrequena.sample.db.entity")
      .persistenceUnit("chinook")
      .build();
  }
  
  @Bean("transactionManagerPostgres")
  public PlatformTransactionManager transactionManagerPostgres(@Qualifier("entityManagerFactoryPostgres") EntityManagerFactory entityManagerFactoryPostgres) {
    return new JpaTransactionManager(entityManagerFactoryPostgres);
  }
}
