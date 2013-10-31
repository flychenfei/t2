package com.britesnow.samplesocial.web;

import java.util.Map;

import com.britesnow.samplesocial.manager.OAuthManager;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.samplesocial.service.SalesForceAuthService;
import com.britesnow.samplesocial.service.SalesForceMDService;
import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sforce.soap.metadata.ConnectedApp;
import com.sforce.soap.metadata.ConnectedAppCanvasConfig;

@Singleton
public class SalesforceMDWebHandlers {

    @Inject
    private SalesForceMDService salesForceMDService;
    @Inject
    private SalesForceAuthService salesForceAuthService;
    @Inject
    private OAuthManager oauthManager;

    @WebPost("/salesforce/saveCavnasApp")
    public WebResponse saveCanvasApp(RequestContext rc,@WebParam("objJson") String jsonString) {
        Map<String,String> opts = JsonUtil.toMapAndList(jsonString);
        String token = salesForceAuthService.getSocialIdEntity().getToken();
        String instanceUrl = oauthManager.getInfo(ServiceType.SalesForce).get("instance_url");
        
        ConnectedApp app = new ConnectedApp();
        app.setContactEmail(opts.get("contactMail"));
        app.setLabel(opts.get("appName"));
        app.setFullName(opts.get("appName").replaceAll(" ", "_"));
        ConnectedAppCanvasConfig canvasConfig = new ConnectedAppCanvasConfig();
        canvasConfig.setCanvasUrl(opts.get("canvasUrl"));
        app.setCanvasConfig(canvasConfig);
        
        salesForceMDService.pushCanvasApp(token, instanceUrl,app);
        return WebResponse.success();
    }

}
