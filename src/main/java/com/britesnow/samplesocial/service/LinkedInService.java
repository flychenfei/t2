package com.britesnow.samplesocial.service;

import java.util.Map;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.model.SocialIdEntity;
import com.britesnow.samplesocial.model.User;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.util.JsonUtil;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LinkedInService {
    @Inject
    private LinkedInAuthService authService;

    public static final String CONNECTION_ENDPOINT = "http://api.linkedin.com/v1/people/~/connections:(id,first-name,last-name,industry)";
    public static final String JOB_ENDPOINT = "http://api.linkedin.com/v1/job-search?keywords=%s";
    public static final String COMPANY_ENDPOINT = "http://api.linkedin.com/v1/company-search?keywords=%s";
    public static final String PEOPLE_SEARCH_ENDPOINT = "http://api.linkedin.com/v1/people-search?keywords=%s";
    public static final String Groups_ENDPOINT = "http://api.linkedin.com/v1/people/~/group-memberships:(group:(id,name),membership-state)";
    public static final String GROUPS_DETAIL_ENDPOINT = "http://api.linkedin.com/v1/groups/%s:(id,name,short-description,description,relation-to-viewer:(membership-state,available-actions),counts-by-category,is-open-to-non-members,category,website-url,locale,location:(country,postal-code),allow-member-invites,site-group-url,small-logo-url,large-logo-url,num-members)";
    public static final String GROUPS_POST_ENDPOINT = "http://api.linkedin.com/v1/groups/%s/posts:(id,type,creation-timestamp,title,summary,creator:(first-name,last-name,picture-url,headline),likes,attachment:(image-url,content-domain,content-url,title,summary),relation-to-viewer,site-group-post-url)?order=recency";
    public static final String GROUPS_POST_COMMENTS_ENDPOINT = "http://api.linkedin.com/v1/posts/%s/comments:(id,creator:(first-name,last-name,picture-url),creation-timestamp,text,relation-to-viewer)";
    public static final String GROUPS_LEAVE_ENDPOINT = "http://api.linkedin.com/v1/people/~/group-memberships/%s";
    public static final String GROUPS_POST_LIKE_ENDPOINT = "http://api.linkedin.com/v1/posts/%s/relation-to-viewer/is-liked";
    
    private OAuthService oAuthService;

    @Inject
    public LinkedInService(OAuthServiceHelper oauthServiceFactory) {
        oAuthService = oauthServiceFactory.getOauthService(ServiceType.LinkedIn);
    }

    /**
     * get user connections   by auth user
     * @param user  login user
     * @param pageIndex  page index
     * @param pageSize   page size
     * @return  user map
     */
    public Map getConnections(User user, Integer pageIndex, Integer pageSize) {

        OAuthRequest request = createRequest(Verb.GET, CONNECTION_ENDPOINT);

        addPageParameter(pageIndex, pageSize, request);
        oAuthService.signRequest(getToken(user),request);
        Response response = request.send();
        Map map = JsonUtil.toMapAndList(response.getBody());
        return map;
    }

    /**
     * get user groups   by auth user
     * @param user  login user
     * @param pageIndex  page index
     * @param pageSize   page size
     * @return  group map
     */
    public Map groups(User user, Integer pageIndex, Integer pageSize) {

        OAuthRequest request = createRequest(Verb.GET, Groups_ENDPOINT);

        addPageParameter(pageIndex, pageSize, request);
        oAuthService.signRequest(getToken(user),request);
        Response response = request.send();
        Map map = JsonUtil.toMapAndList(response.getBody());
        return map;
    }

    /**
     * search jobs by auth user
     * @param user   user have auth
     * @param pageIndex  page index
     * @param pageSize   page size
     * @param keywork    keywork to search
     * @return  job map
     */
    public Map searchJobs(User user, Integer pageIndex, Integer pageSize, String keywork) {
        if (keywork == null) {
            keywork = "hibernate";
        }
        OAuthRequest request = createRequest(Verb.GET, String.format(JOB_ENDPOINT, keywork));
        addPageParameter(pageIndex, pageSize, request);
        oAuthService.signRequest(getToken(user), request);
        Response resp = request.send();
        return JsonUtil.toMapAndList(resp.getBody());
    }

    /**
     * search compan by auth user
     * @param user   user have auth
     * @param pageIndex  page index
     * @param pageSize   page size
     * @param keywork    key work to search
     * @return   company map
     */
    public Map searchCompany(User user, Integer pageIndex, Integer pageSize, String keywork) {
        if (keywork == null) {
            keywork = "inc";
        }
        OAuthRequest request = createRequest(Verb.GET, String.format(COMPANY_ENDPOINT, keywork));
        addPageParameter(pageIndex, pageSize, request);
        oAuthService.signRequest(getToken(user), request);
        Response resp = request.send();
        return JsonUtil.toMapAndList(resp.getBody());
    }

    /**
     * search people  by auth user
     * @param user   auth user
     * @param pageIndex  page index
     * @param pageSize   page side
     * @param keywork   keywork to search
     * @return   people map
     */
    public Map searchPeople(User user, Integer pageIndex, Integer pageSize, String keywork) {
        if (keywork == null) {
            keywork = "self";
        }
        OAuthRequest request = createRequest(Verb.GET, String.format(PEOPLE_SEARCH_ENDPOINT, keywork));
        addPageParameter(pageIndex, pageSize, request);
        oAuthService.signRequest(getToken(user), request);
        Response resp = request.send();
        return JsonUtil.toMapAndList(resp.getBody());
    }

    /**
     * get group details by groupId
     * 
     * @param user
     * @param groupId
     * @return
     */
    public Map groupDetails(User user, String groupId){
    	if (Strings.isNullOrEmpty(groupId)) {
    		return null;
    	}
    	OAuthRequest request = createRequest(Verb.GET, String.format(GROUPS_DETAIL_ENDPOINT, groupId));

        oAuthService.signRequest(getToken(user),request);
        Response response = request.send();
        Map map = JsonUtil.toMapAndList(response.getBody());
        return map;
    }
    
    /**
     * get the post list by groupId
     * 
     * @param user
     * @param groupId
     * @param start
     * @param count
     * @return
     */
    public Map groupPost(User user, String groupId, Integer start, Integer count){
    	if (Strings.isNullOrEmpty(groupId)) {
    		return null;
    	}
    	OAuthRequest request = createRequest(Verb.GET, String.format(GROUPS_POST_ENDPOINT, groupId));

    	addPageParameter(start, count, request);
        oAuthService.signRequest(getToken(user),request);
        Response response = request.send();
        Map map = JsonUtil.toMapAndList(response.getBody());
        return map;
    }
    
    /**
     * get the post list by groupId
     * 
     * @param user
     * @param groupId
     * @param start
     * @param count
     * @return
     */
    public Map groupPostComments(User user, String postId, Integer start, Integer count){
    	if (Strings.isNullOrEmpty(postId)) {
    		return null;
    	}
    	OAuthRequest request = createRequest(Verb.GET, String.format(GROUPS_POST_COMMENTS_ENDPOINT, postId));

    	addPageParameter(start, count, request);
        oAuthService.signRequest(getToken(user),request);
        Response response = request.send();
        Map map = JsonUtil.toMapAndList(response.getBody());
        return map;
    }
    
    /**
     * Leave a group
     * @param user
     * @param groupId
     * @return boolean
     */
    public boolean leaveGroup(User user, String groupId){
    	OAuthRequest request = createRequest(Verb.DELETE, String.format(GROUPS_LEAVE_ENDPOINT, groupId));
    	
        oAuthService.signRequest(getToken(user),request);
        Response response = request.send();
        return Strings.isNullOrEmpty(response.getBody());
    }
    
    /**
     * set like or unlike to a post
     * @param user
     * @param postId
     * @return
     */
    public boolean likeGroupPost(User user, String postId, boolean islike){
    	OAuthRequest request = new OAuthRequest(Verb.PUT, String.format(GROUPS_POST_LIKE_ENDPOINT, postId));
    	request.addHeader("x-li-format","json");
    	if(islike){
        	request.addPayload("true");
    	}else{
        	request.addPayload("false");
    	}
    	
        oAuthService.signRequest(getToken(user),request);
        Response response = request.send();
        return Strings.isNullOrEmpty(response.getBody());
    }
    
    private void addPageParameter(Integer pageIndex, Integer pageSize, OAuthRequest request) {
        int start = pageIndex*pageSize;
        request.addQuerystringParameter("start", String.valueOf(start));
        request.addQuerystringParameter("count", String.valueOf(pageSize));
    }

    private Token getToken(User user) {
        SocialIdEntity soId = authService.getSocialIdEntity();
        if (soId != null) {
            return new Token(soId.getToken(), soId.getSecret());
        }
        throw new OauthException(oAuthService.getAuthorizationUrl(oAuthService.getRequestToken()));
    }

    private OAuthRequest createRequest(Verb verb, String url) {
        OAuthRequest request = new OAuthRequest(verb, url);
        request.addHeader("x-li-format","json");
        return request;
    }

}
