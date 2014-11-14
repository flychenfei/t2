package com.britesnow.samplesocial.service;

import com.britesnow.snow.util.JsonUtil;
import com.google.gdata.util.common.base.StringUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import java.util.Map;


@Singleton
public class LiveAblumService {
    @Inject
	private LiveAuthService oAuthService;

    public static final String ALBUMLIST_ENDPOINT = "https://apis.live.net/v5.0/me/albums";
    public static final String LIVE_ENDPOINT = "https://apis.live.net/v5.0";

    /**
     * get user ablum lists
     * @return
     */
    public Map getUserAblums() {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, ALBUMLIST_ENDPOINT);
        Response response = request.send();
        Map profile = JsonUtil.toMapAndList(response.getBody());
        return profile;
    }

    public Map getAblum(String ablumId) {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, LIVE_ENDPOINT + "/" + ablumId);
        Response response = request.send();
        Map ablumInfo = JsonUtil.toMapAndList(response.getBody());
        return ablumInfo;
    }

    /**
     * create ablum
     * @return
     */
    public Map saveAblum(Map ablum, String ablumId) {
        OAuthRequest request = null;
        if(StringUtil.isEmpty(ablumId)){
            request = oAuthService.createRequest(Verb.POST, ALBUMLIST_ENDPOINT);
        }else{
            request = oAuthService.createRequest(Verb.PUT, LIVE_ENDPOINT + "/" + ablumId);
        }
        request.addBodyParameter("name", (String) ablum.get("name"));
        request.addBodyParameter("description", (String) ablum.get("description"));
        Response response = request.send();
        Map ablumInfo = JsonUtil.toMapAndList(response.getBody());
        return ablumInfo;
    }

}
