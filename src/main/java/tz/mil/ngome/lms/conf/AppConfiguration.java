package tz.mil.ngome.lms.conf;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableTransactionManagement
public class AppConfiguration {
	@Autowired
    private DataSource dataSource;

    @Autowired
    private JpaVendorAdapter jpaVendorAdapter;
    
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean emf() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan(new String[] {"tz.mil.ngome.lms.entity"});
        emf.setJpaVendorAdapter(jpaVendorAdapter);
        Map<String, Object> jpaProperties = emf.getJpaPropertyMap();
        emf.setJpaPropertyMap(jpaProperties);
        
        return emf;
    }
    
    @Bean
    public PlatformTransactionManager transactionManager(
            EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
