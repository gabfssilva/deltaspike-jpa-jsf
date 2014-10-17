package com.wehavescience.producers;

import com.wehavescience.qualifiers.InjectableProperties;
import com.wehavescience.qualifiers.Startup;
import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
public class DefaultProducer {
    @Produces
    @ApplicationScoped
    @Default
    @Startup
    public EntityManagerFactory entityManagerFactoryMySQL(@InjectableProperties(file = "jpa-mysql") Map<String, String> properties) {
        PersistenceProvider persistenceProvider = new HibernatePersistenceProvider();
        return persistenceProvider.createEntityManagerFactory("MySQL", properties);
    }

    @Produces
    @Default
    @RequestScoped
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    public void close(@Disposes @Any EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

    @Produces
    @InjectableProperties(file = "")
    public Map<String, String> properties(InjectionPoint injectionPoint) throws IOException {
        InjectableProperties annotation = injectionPoint.getAnnotated().getAnnotation(InjectableProperties.class);
        Map<String, String> map = new HashMap<String, String>();
        final ResourceBundle bundle = ResourceBundle.getBundle(annotation.file());
        bundle.keySet().forEach(key -> map.put(key, bundle.getString(key)));
        return map;
    }
}
