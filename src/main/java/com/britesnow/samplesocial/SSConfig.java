package com.britesnow.samplesocial;


import com.britesnow.samplesocial.web.SSAuthRequest;
import com.britesnow.snow.web.auth.AuthRequest;
import com.google.inject.AbstractModule;


/**
 * TODO: Rename the package and the class name to fit your application naming convention and
 * update /webapp/WEB-INF/snow.properties "snow.webApplicationModules" accordingly
 * <p/>
 * TODO: add/remove bindings to fit your application's need
 */
public class SSConfig extends AbstractModule {

    @Override
    protected void configure() {

        // Default bind for the HibernateDaoHelper.
       // bind(HibernateDaoHelper.class).to(HibernateDaoHelperImpl.class);
      bind(AuthRequest.class).to(SSAuthRequest.class);
    }

}
