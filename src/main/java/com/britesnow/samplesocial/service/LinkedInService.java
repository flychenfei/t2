package com.britesnow.samplesocial.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.json.simple.JSONValue;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.model.SocialIdEntity;
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

    public static final String CURRENTUSERINFO_ENDPOINT = "http://api.linkedin.com/v1/people/~:(first-name,last-name,headline,site-standard-profile-request:(url),picture-url)";
    public static final String JOBBOOKMARKS_ENDPOINT = "https://api.linkedin.com/v1/people/~/job-bookmarks:(job:(id,company:(name),description,location-description))";
    public static final String REMOVEJOBBOOKMARK_ENDPOINT = "https://api.linkedin.com/v1/people/~/job-bookmarks/%s";
    public static final String ADDJOBBOOKMARK_ENDPOINT = "https://api.linkedin.com/v1/people/~/job-bookmarks";
    public static final String JOBBOOKMARKId_ENDPOINT = "https://api.linkedin.com/v1/people/~/job-bookmarks:(job:(id))";
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
    public static final String PEOPLE_INFO_ENDPOINT = "http://api.linkedin.com/v1/people/%s:(first-name,last-name,headline,picture-url)";
    public static final String FOLLOWED_COMPANYS_ENDPOINT = "https://api.linkedin.com/v1/people/~/following/companies:(id,name,universal-name,website-url,logo-url,locations,description,num-followers)";
    public static final String SUGGESTED_COMPANYS_ENDPOINT = "https://api.linkedin.com/v1/people/~/suggestions/to-follow/companies:(id,name,universal-name,website-url,logo-url,locations,description,num-followers)";
    public static final String STARTFOLLOWING_COMPANYS_ENDPOINT = "https://api.linkedin.com/v1/people/~/following/companies";
    public static final String STOPFOLLOWING_COMPANYS_ENDPOINT = "https://api.linkedin.com/v1/people/~/following/companies/id=%s";
    public static final String UPDATE_COMPANYS_ENDPOINT = "https://api.linkedin.com/v1/companies/%s/updates?event-type=status-update";
    public static final String COMMENTING_COMPANYS_SHARE_ENDPOINT = "https://api.linkedin.com/v1/people/~/network/updates/key=%s/update-comments";
    public static final String LIKING_COMPANYS_SHARE_ENDPOINT = "https://api.linkedin.com/v1/people/~/network/updates/key=%s/is-liked";

    private OAuthService oAuthService;

    @Inject
    public LinkedInService(OAuthServiceHelper oauthServiceFactory) {
        oAuthService = oauthServiceFactory.getOauthService(ServiceType.LinkedIn);
    }

    /**
     * get current user information by auth user
     * @param user  login user
     * @return  user map
     */
    public Map getCurrentUserInfo(User user) {
        OAuthRequest request = createRequest(Verb.GET, CURRENTUSERINFO_ENDPOINT);
        oAuthService.signRequest(getToken(user), request);
        Response response = request.send();
        return JsonUtil.toMapAndList(response.getBody());
    }

    /**
     * get user jobbookmarks   by auth user
     * @param user  login user
     * @param pageIndex  page index
     * @param pageSize   page size
     * @return  user map
     */
    public Map getJobBookmarks(User user, Integer pageIndex, Integer pageSize) {
        OAuthRequest request = createRequest(Verb.GET, JOBBOOKMARKS_ENDPOINT);
        addPageParameter(pageIndex, pageSize, request);
        oAuthService.signRequest(getToken(user), request);
        Response response = request.send();
        return JsonUtil.toMapAndList(response.getBody());
    }
    
    /**
     * remove user's jobbookmark   by auth user and bookid
     * @param user  login user
     * @return  user map
     */
    public void removeBookmark(User user, String bookid) {
        OAuthRequest request = createRequest(Verb.DELETE, String.format(REMOVEJOBBOOKMARK_ENDPOINT, bookid));
        oAuthService.signRequest(getToken(user), request);
        request.send();
    }
    
    /**
     * add a jobbookmark   by auth user
     * @param user  login user
     * @return  user map
     */
	public void addBookmark(User user, String bookid) {
		OAuthRequest request = createRequest(Verb.POST, ADDJOBBOOKMARK_ENDPOINT);
		HashMap jsonMap = new HashMap();
		JSONObject contentObject = new JSONObject();
		contentObject.put("id", bookid);
		jsonMap.put("job", contentObject);
		request.addPayload(JSONValue.toJSONString(jsonMap));
		oAuthService.signRequest(getToken(user), request);
		request.send();
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
		oAuthService.signRequest(getToken(user), request);
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
	@SuppressWarnings("unchecked")
	public Map searchJobs(User user, Integer pageIndex, Integer pageSize, String keywork) {
    	ArrayList<JSONObject> bookmark = new ArrayList<JSONObject>();
    	ArrayList<JSONObject> bookmarkIdTotal = new ArrayList<JSONObject>();
    	ArrayList<JSONObject> resultBookmark = new ArrayList<JSONObject>();
        if (Strings.isNullOrEmpty(keywork)) {
            keywork = "hibernate";
        }
        OAuthRequest request = createRequest(Verb.GET, String.format(JOB_ENDPOINT, keywork));
        
        //get the job jobsIDs list
        addPageParameter(pageIndex, pageSize, request);
        oAuthService.signRequest(getToken(user), request);
        Response response = request.send();
        
        Map result = JsonUtil.toMapAndList(response.getBody());
        Map job = (Map) result.get("jobs");
        JSONArray jobList = (JSONArray) job.get("values");
        
        if (jobList == null || jobList.size() == 0){
        	return result;
        }

        resultBookmark = (ArrayList<JSONObject>) jobList.stream().collect(Collectors.toList());
        
        //get the job bookmarkIDs list. And if it's null,just return map
        JSONArray BookmarkId = (JSONArray)jobmarkId(user).get("values");
        if(BookmarkId == null || BookmarkId.size() == 0){
        	resultBookmark.stream().forEach(p -> {
    			p.put("check", "addbookmark");
    			p.put("mark", "Save as Bookmark");
        	});
        	return result;
        }

        bookmark = (ArrayList<JSONObject>) BookmarkId.stream().collect(Collectors.toList());
        bookmark.stream().forEach(p -> {
        	bookmarkIdTotal.add((JSONObject) p.get("job"));
        });

        //do a mark if it has saved
    	resultBookmark.stream().forEach(p1 -> {
    		bookmarkIdTotal.stream().forEach(p2 -> {
    			if((p1.get("id")).equals(p2.get("id"))){
    				p1.put("check", "removebookmark");
    				p1.put("mark", "Remove Bookmark");
    			}
    		});
    		if(!p1.containsKey("check")){
				p1.put("check", "addbookmark");
    			p1.put("mark", "Save as Bookmark");
			}
    	});

        return result;
    }

    /**
     * search company by auth user
     * @param user   user have auth
     * @param pageIndex  page index
     * @param pageSize   page size
     * @param keywork    key work to search
     * @return   company map
     */
    public Map searchCompany(User user, Integer pageIndex, Integer pageSize, String keywork) {
        if (Strings.isNullOrEmpty(keywork)) {
            keywork = "inc";
        }
        OAuthRequest request = createRequest(Verb.GET, String.format(COMPANY_ENDPOINT, keywork));
        addPageParameter(pageIndex, pageSize, request);
        oAuthService.signRequest(getToken(user), request);
        Response response = request.send();
        return JsonUtil.toMapAndList(response.getBody());
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
        if (Strings.isNullOrEmpty(keywork)) {
            keywork = "self";
        }
        OAuthRequest request = createRequest(Verb.GET, String.format(PEOPLE_SEARCH_ENDPOINT, keywork));
        addPageParameter(pageIndex, pageSize, request);
        oAuthService.signRequest(getToken(user), request);
        Response resp = request.send();
        return JsonUtil.toMapAndList(resp.getBody());
    }

    /**
     * 
     * @param user
     * @param userId
     * @return
     */
    public Map userInfo(User user,String userId) {
        if (Strings.isNullOrEmpty(userId)) {
            return null;
        }
        OAuthRequest request = createRequest(Verb.GET, String.format(PEOPLE_INFO_ENDPOINT, userId));
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
        oAuthService.signRequest(getToken(user), request);
        Response response = request.send();
        return JsonUtil.toMapAndList(response.getBody());
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
     * get the post Comments list by postId
     * 
     * @param user
     * @param postId
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
    	if (Strings.isNullOrEmpty(groupId)) {
    		return false;
    	}
    	OAuthRequest request = createRequest(Verb.DELETE, String.format(GROUPS_LEAVE_ENDPOINT, groupId));
        oAuthService.signRequest(getToken(user),request);
        Response response = request.send();
        return Strings.isNullOrEmpty(response.getBody());
    }
    
    /**
     * set like or unlike to a post
     * @param user
     * @param postId
     * @param islike
     * @return
     */
    public boolean likeGroupPost(User user, String postId, boolean islike){
    	OAuthRequest request = createRequest(Verb.PUT, String.format(GROUPS_POST_LIKE_ENDPOINT, postId));
    	if(islike){
        	request.addPayload("true");
    	}else{
        	request.addPayload("false");
    	}
        oAuthService.signRequest(getToken(user),request);
        Response response = request.send();
        return Strings.isNullOrEmpty(response.getBody());
    }
    
    /**
     * get followed company by auth user
     * 
     * @param user
     * @return
     */
    public Map followedCompanys(User user) {
        OAuthRequest request = createRequest(Verb.GET, FOLLOWED_COMPANYS_ENDPOINT);
        oAuthService.signRequest(getToken(user), request);
        Response response = request.send();
        return JsonUtil.toMapAndList(response.getBody());
    }
    
    /**
     * get suggests followed company by auth user
     * 
     * @param user
     * @return
     */
    public Map suggestsFollowedCompanys(User user) {
        OAuthRequest request = createRequest(Verb.GET, SUGGESTED_COMPANYS_ENDPOINT);
        oAuthService.signRequest(getToken(user), request);
        Response response = request.send();
        return JsonUtil.toMapAndList(response.getBody());
    }
    
    /**
     * following a company by auth user
     * @param user
     * @param companyId
     * @return
     */
    public boolean startFollowingCompany(User user, String companyId) {
    	if (Strings.isNullOrEmpty(companyId)) {
    		return false;
    	}
    	OAuthRequest request = createRequest(Verb.POST, STARTFOLLOWING_COMPANYS_ENDPOINT);
		HashMap jsonMap = new HashMap();
		jsonMap.put("id", Integer.valueOf(companyId));
		request.addPayload(JSONValue.toJSONString(jsonMap));
		oAuthService.signRequest(getToken(user), request);
		Response response = request.send();
		return Strings.isNullOrEmpty(response.getBody());
    }
    
    /**
     * stop following a company by auth user
     * @param user
     * @param companyId
     * @return
     */
    public boolean stopFollowingCompany(User user, String companyId) {
    	if (Strings.isNullOrEmpty(companyId)) {
    		return false;
    	}
        OAuthRequest request = createRequest(Verb.DELETE, String.format(STOPFOLLOWING_COMPANYS_ENDPOINT, companyId));
        oAuthService.signRequest(getToken(user), request);
        Response response = request.send();
        return Strings.isNullOrEmpty(response.getBody());
    }

    /**
     * get the updates of company by auth user and companyId
     *
     * @param user
     * @param companyId
     * @param start
     * @param count
     * @return
     */
    public Map companyUpdates(User user, String companyId, Integer start, Integer count) {
        if (Strings.isNullOrEmpty(companyId)) {
            return null;
        }
        OAuthRequest request = createRequest(Verb.GET, String.format(UPDATE_COMPANYS_ENDPOINT, companyId));
        addPageParameter(start, count, request);
        oAuthService.signRequest(getToken(user), request);
        Response response = request.send();
        return JsonUtil.toMapAndList(response.getBody());
    }

    /**
     * commenting on a company share with  updateKey
     *
     * @param user
     * @param updateKey
     * @param commentContent
     * @return
     */
    public boolean commentingCompanyShare(User user, String updateKey, String commentContent){
        if (Strings.isNullOrEmpty(updateKey) || Strings.isNullOrEmpty(commentContent)){
            return false;
        }
        OAuthRequest request = createRequest(Verb.POST, String.format(COMMENTING_COMPANYS_SHARE_ENDPOINT, updateKey.trim()));
        HashMap jsonMap = new HashMap();
        jsonMap.put("comment", commentContent.trim());
        request.addPayload(JSONValue.toJSONString(jsonMap));
        oAuthService.signRequest(getToken(user), request);
        Response response = request.send();
        return Strings.isNullOrEmpty(response.getBody());
    }

    /**
     * like or dislike on a company share with  updateKey
     *
     * @param user
     * @param updateKey
     * @param like
     * @return
     */
    public boolean likingCompanyShare(User user, String updateKey, String like){
        if (Strings.isNullOrEmpty(updateKey) || Strings.isNullOrEmpty(like)){
            return false;
        }
        OAuthRequest request = createRequest(Verb.PUT, String.format(LIKING_COMPANYS_SHARE_ENDPOINT, updateKey.trim()));
        if("like".equals(like)){
            request.addPayload("true");
        }else if("dislike".equals(like)){
            request.addPayload("false");
        }else{
            return false;
        }
        oAuthService.signRequest(getToken(user), request);
        Response response = request.send();
        return Strings.isNullOrEmpty(response.getBody());
    }

    private Map jobmarkId(User user) {
        OAuthRequest request = createRequest(Verb.GET, JOBBOOKMARKId_ENDPOINT);
        oAuthService.signRequest(getToken(user),request);
        Response response = request.send();
        return JsonUtil.toMapAndList(response.getBody());
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
