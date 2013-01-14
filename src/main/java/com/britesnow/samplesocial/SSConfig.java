package com.britesnow.samplesocial;

import com.britesnow.samplesocial.dao.DaoRegistry;
import com.britesnow.samplesocial.entity.BaseEntity;
import com.britesnow.samplesocial.web.SSAuthRequest;
import com.britesnow.snow.web.auth.AuthRequest;
import com.britesnow.snow.web.binding.EntityClasses;
import com.britesnow.snow.web.db.hibernate.HibernateDaoHelper;
import com.britesnow.snow.web.db.hibernate.HibernateDaoHelperImpl;
import com.google.inject.*;
import com.metapossum.utils.scanner.reflect.ClassesInPackageScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;


/**
 * TODO: Rename the package and the class name to fit your application naming convention and
 * update /webapp/WEB-INF/snow.properties "snow.webApplicationModules" accordingly
 * <p/>
 * TODO: add/remove bindings to fit your application's need
 */
public class SSConfig extends AbstractModule {
    private static Logger log = LoggerFactory.getLogger(SSConfig.class);

    @Override
    protected void configure() {

        // Default bind for the HibernateDaoHelper.
        bind(HibernateDaoHelper.class).to(HibernateDaoHelperImpl.class);
      bind(AuthRequest.class).to(SSAuthRequest.class);
    }

    // Used by the Hibernate support to inject the entity class
    @Provides
    @Singleton
    @Inject
    @EntityClasses
    public Class[] provideEntityClasses() {
        Set<Class<?>> entitySet;
        try {
            entitySet = new ClassesInPackageScanner().findAnnotatedClasses(BaseEntity.class.getPackage().getName(), javax.persistence.Entity.class);
            Class[] entityClasses = new Class[entitySet.size()];
            entitySet.toArray(entityClasses);
            return entityClasses;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Cannot get all the enity class: " + e.getMessage());
        }

    }

    @Provides
    @Singleton
    @Inject
    public DaoRegistry providesDaoRegistry(Injector injector, @EntityClasses Class[] entityClasses) {
        DaoRegistry daoRegistry = new DaoRegistry();
        daoRegistry.init(injector, entityClasses);
        return daoRegistry;
    }


}
