package ar.edu.itba.paw.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan({
        "ar.edu.itba.paw.webapp.jersey",
        "ar.edu.itba.paw.services",
        "ar.edu.itba.paw.persistance"
})
@Import(EmailConfig.class)
@PropertySource("classpath:application.properties")
public class WebConfig extends WebMvcConfigurerAdapter {

    @Value("classpath:schema.sql")
    private Resource schemaSql;
    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        final SimpleDriverDataSource ds = new SimpleDriverDataSource();
        ds.setDriverClass(org.postgresql.Driver.class);
        ds.setUrl(env.getProperty("DB_URL","jdbc:postgresql://localhost/paw"));
        ds.setUsername(env.getProperty("DB_USER","root"));
        ds.setPassword(env.getProperty("DB_PASSWORD","root"));
        return ds;
    }
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan("ar.edu.itba.paw.model");
        factoryBean.setDataSource(dataSource());

        final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(vendorAdapter);

        final Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL94Dialect");

        //FIXME: Si ponen esto en prod, hay tabla!!!
        //properties.setProperty("hibernate.show_sql", "true");
        //properties.setProperty("format_sql", "true");

        factoryBean.setJpaProperties(properties);

        return factoryBean;
    }

    @Bean
    public CharacterEncodingFilter characterEncodingFilter(){
        CharacterEncodingFilter cef = new CharacterEncodingFilter();
        cef.setEncoding("UTF-8");
        cef.setForceEncoding(true);
        return cef;
    }
    @Bean
    public MessageSource messageSource() {
        final ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();

        ms.addBasenames("classpath:i18n/messages", "classpath:mail/i18n/email-messages");
        ms.setDefaultEncoding(StandardCharsets.UTF_8.name());
        ms.setCacheSeconds((int) TimeUnit.MINUTES.toSeconds(5));

        return ms;
    }
    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource ds) {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(ds);
        initializer.setDatabasePopulator(dsPopulator());
        return initializer;
    }
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver mpr = new CommonsMultipartResolver();
        mpr.setMaxUploadSize(1024 * 1024 * 10);
        mpr.setMaxUploadSizePerFile(1024 * 1024 * 2);
        return mpr;
    }
    private DatabasePopulator dsPopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schemaSql);
        return populator;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        // Sirve archivos estáticos desde la raíz de webapp
        registry.addResourceHandler("/**")
                .addResourceLocations("/");

    }

}
