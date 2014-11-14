package com.britesnow.samplesocial.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.LinkedInService;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LinkedInHandlers {
    @Inject
    private LinkedInService linkedInService;

    @WebGet("/linkedin/currentUserInfo")
    public WebResponse getCurrentUserInfo(@WebUser User user) {
        Map result = linkedInService.getCurrentUserInfo(user);
        return WebResponse.success(result);
    }
  
    @WebGet("/linkedin/jobbookmarks")
    public WebResponse getJobBookmarks(@WebUser User user, @WebParam("pageIndex") Integer pageIndex,@WebParam("pageSize") Integer pageSize) {
        Map result = linkedInService.getJobBookmarks(user, pageIndex, pageSize);
        if(result.get("values") != null){
        	return WebResponse.success(result.get("values")).set("result_count", result.get("_total"));
        }else{
        	return WebResponse.success(new ArrayList()).set("result_count", result.get("_total"));
        }
    }
    
    @WebGet("/linkedin/removebookmark")
    public WebResponse removeBookmark(@WebUser User user, @WebParam("id") String bookid) {
         linkedInService.removeBookmark(user,bookid);
         return WebResponse.success();
    }
    
    @WebPost("/linkedin/addbookmark")
    public WebResponse addBookmark(@WebUser User user, @WebParam("id") String bookid) {
         linkedInService.addBookmark(user,bookid);
         return WebResponse.success();
    }

    @WebGet("/linkedin/connects")
    public WebResponse getConnects(@WebUser User user, @WebParam("pageIndex") Integer pageIndex,@WebParam("pageSize") Integer pageSize) {
        Map result = linkedInService.getConnections(user, pageIndex, pageSize);
        WebResponse resp = WebResponse.success(result.get("values"));
        resp.set("result_count", result.get("_total"));
        return resp;
    }
    
    @WebGet("/linkedin/groups")
    public WebResponse getGroups(@WebUser User user, @WebParam("pageIndex") Integer pageIndex,@WebParam("pageSize") Integer pageSize) {
        Map result = linkedInService.groups(user, pageIndex, pageSize);
        if(result.get("values") != null){
        	return WebResponse.success(result.get("values")).set("result_count", result.get("_total"));
        }else{
        	return WebResponse.success(new ArrayList()).set("result_count", result.get("_total"));
        }
    }
    
    @WebGet("/linkedin/jobs")
    public WebResponse searchJobs(@WebUser User user, @WebParam("pageIndex") Integer pageIndex,
                                  @WebParam("pageSize") Integer pageSize, @WebParam("keywork") String keywork) {
        Map result = linkedInService.searchJobs(user, pageIndex, pageSize, keywork);
        Map jobs = (Map) result.get("jobs");
        WebResponse resp = WebResponse.success(jobs.get("values"));
        resp.set("result_count", result.get("numResults"));
        return resp;
    }

    @WebGet("/linkedin/companys")
    public WebResponse searchCompanys(@WebUser User user, @WebParam("pageIndex") Integer pageIndex,
                                  @WebParam("pageSize") Integer pageSize, @WebParam("keywork") String keywork) {
        Map result = linkedInService.searchCompany(user, pageIndex, pageSize, keywork);
        Map jobs = (Map) result.get("companies");
        WebResponse resp = WebResponse.success(jobs.get("values"));
        resp.set("result_count", result.get("numResults"));
        return resp;
    }
    
    @WebGet("/linkedin/followedCompanys")
    public WebResponse followedCompanys(@WebUser User user) {
        Map result = linkedInService.followedCompanys(user);
        if(result.get("values") != null){
        	return WebResponse.success(result.get("values")).set("result_count", result.get("_total"));
        }else{
        	return WebResponse.success(new ArrayList()).set("result_count", result.get("_total"));
        }
    }
    
    @WebGet("/linkedin/suggestsFollowedCompanys")
    public WebResponse suggestsFollowedCompanys(@WebUser User user) {
        Map result = linkedInService.suggestsFollowedCompanys(user);
        if(result.get("values") != null){
        	return WebResponse.success(result.get("values")).set("result_count", result.get("_total"));
        }else{
        	return WebResponse.success(new ArrayList()).set("result_count", result.get("_total"));
        }
    }
    
    @WebGet("/linkedin/company/startFollowing")
    public WebResponse startFollowingCompany(@WebUser User user, @WebParam("companyId") String companyId) {
        if(linkedInService.startFollowingCompany(user, companyId)){
        	return WebResponse.success();
        }else{
        	return WebResponse.fail();
        }
    }
    
    @WebGet("/linkedin/company/stopFollowing")
    public WebResponse stopFollowingCompany(@WebUser User user, @WebParam("companyId") String companyId) {
        if(linkedInService.stopFollowingCompany(user, companyId)){
        	return WebResponse.success();
        }else{
        	return WebResponse.fail();
        }
    }

    @WebGet("/linkedin/company/companyUpdates")
    public WebResponse GetCompanyUpdates(@WebUser User user, @WebParam("companyId") String companyId, @WebParam("pageIndex") Integer pageIndex,
                                         @WebParam("pageSize") Integer pageSize) {
        Map result = linkedInService.companyUpdates(user, companyId, pageIndex, pageSize);
        if(result == null){
            return WebResponse.fail();
        }
        if(result.get("values") != null){
            return WebResponse.success(result.get("values")).set("result_count", result.get("_total"));
        }else{
            return WebResponse.success(new ArrayList()).set("result_count", result.get("_total"));
        }
    }

    @WebGet("/linkedin/company/commentCompanyUpdates")
    public WebResponse commentCompanyUpdates(@WebUser User user, @WebParam("updateKey") String updateKey, @WebParam("comment") String commentContent) {
        boolean result = linkedInService.commentingCompanyShare(user, updateKey, commentContent);
        if(result){
            return WebResponse.success();
        }else{
            return WebResponse.fail();
        }
    }

    @WebGet("/linkedin/company/likeCompanyUpdates")
    public WebResponse likeCompanyUpdates(@WebUser User user, @WebParam("updateKey") String updateKey, @WebParam("like") String like) {
        boolean result = linkedInService.likingCompanyShare(user, updateKey, like);
        if(result){
            return WebResponse.success();
        }else{
            return WebResponse.fail();
        }
    }

    @WebGet("/linkedin/searchPeople")
    public WebResponse searchPeople(@WebUser User user, @WebParam("pageIndex") Integer pageIndex,
                                  @WebParam("pageSize") Integer pageSize, @WebParam("keywork") String keywork) {
        Map result = linkedInService.searchPeople(user, pageIndex, pageSize, keywork);
        Map peoples = (Map) result.get("people");
        WebResponse resp = WebResponse.success(peoples.get("values"));
        resp.set("result_count", result.get("numResults"));
        return resp;
    }
    
    @WebGet("/linkedin/groupDetails")
    public WebResponse getGroupDetails(@WebUser User user, @WebParam("groupId") String groupId) {
        Map result = linkedInService.groupDetails(user, groupId);
        if (result == null)
        	return WebResponse.fail();
        WebResponse resp = WebResponse.success(result);
        return resp;
    }
    
    @WebGet("/linkedin/groupPost")
    public WebResponse getGroupPosts(@WebUser User user, @WebParam("groupId") String groupId, @WebParam("start") Integer start, @WebParam("count") Integer count) {
        Map result = linkedInService.groupPost(user, groupId, start, count);
        WebResponse resp;
        if(result.get("values") != null){
            resp = WebResponse.success(result.get("values"));
            int remain =  Integer.parseInt(result.get("_total").toString())-(start+1)*count;
            if( remain > 0 ){
                resp.set("next", true);
            }else{
            	resp.set("next", false);
            }
        }else{
            resp = WebResponse.success(new ArrayList());
            resp.set("next", false);
        }
        resp.set("start", start);
        resp.set("count", count);
        return resp;
    }
    
    @WebGet("/linkedin/groupPostComments")
    public WebResponse getGroupPostComments(@WebUser User user, @WebParam("postId") String postId, @WebParam("start") Integer start, @WebParam("count") Integer count) {
        Map result = linkedInService.groupPostComments(user, postId, start, count);
        WebResponse resp;
        if(result.get("values") != null){
            resp = WebResponse.success(result.get("values"));
            if(((List)result.get("values")).size()>0){
            	resp.set("hasValue", true);
            }else{
            	resp.set("hasValue", false);
            }
            int remain =  Integer.parseInt(result.get("_total").toString())-(start+1)*count;
            if( remain > 0 ){
                resp.set("next", true);
            }else{
            	resp.set("next", false);
            }
        }else{
            resp = WebResponse.success(new ArrayList());
            resp.set("hasValue", false);
            resp.set("next", false);
        }
        resp.set("start", start);
        resp.set("count", count);
        return resp;
    }
    
    @WebGet("/linkedin/leaveGroup")
    public WebResponse leaveGroup(@WebUser User user, @WebParam("groupId") String groupId) {
        if(linkedInService.leaveGroup(user, groupId))
        	return WebResponse.success();
        else
        	return WebResponse.fail();
    }
    
    @WebGet("/linkedin/likeGroupPost")
    public WebResponse likeGroupPost(@WebUser User user, @WebParam("postId") String postId, @WebParam("islike") Boolean islike) {
        if(linkedInService.likeGroupPost(user, postId, islike))
        	return WebResponse.success();
        else
        	return WebResponse.fail();
    }
    
    @WebGet("/linkedin/getUserInfo")
    public WebResponse userInfo(@WebUser User user, @WebParam("userId") String userId) {
    	Map result = linkedInService.userInfo(user, userId);
    	return WebResponse.success(result);
    }
}
