package com.cjrequena.sample.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
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
  entityManagerFactoryRef = "entityManagerFactoryH2",
  transactionManagerRef = "transactionManagerH2",
  basePackages = {"com.cjrequena.sample.db.repository"}
)
@Profile({"h2"})
public class H2Configuration {

  @Bean(name = "dataSourceH2", destroyMethod = "")
  @Validated
  @ConfigurationProperties(prefix = "spring.datasource.h2")
  @ConditionalOnClass({HikariDataSource.class})
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
  }

  /**
   *
   * @param builder
   * @param dataSource
   * @return
   */
  @Bean("entityManagerFactoryH2")
  public LocalContainerEntityManagerFactoryBean entityManagerFactoryH2(EntityManagerFactoryBuilder builder, @Qualifier("dataSourceH2") DataSource dataSource) {
    return builder
      .dataSource(dataSource)
      .packages("com.cjrequena.sample.db.entity")
      .persistenceUnit("chinook")
      .build();
  }

  /**
   *
   * @param entityManagerFactoryH2
   * @return
   */
  @Bean("transactionManagerH2")
  public PlatformTransactionManager transactionManagerH2(@Qualifier("entityManagerFactoryH2") EntityManagerFactory entityManagerFactoryH2) {
    return new JpaTransactionManager(entityManagerFactoryH2);
  }
}

