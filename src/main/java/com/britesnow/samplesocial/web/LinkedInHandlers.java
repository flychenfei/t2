package com.britesnow.samplesocial.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.britesnow.samplesocial.model.User;
import com.britesnow.samplesocial.service.LinkedInService;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LinkedInHandlers {
    @Inject
    private LinkedInService linkedInService;

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
        WebResponse resp;
        if(result.get("values") != null){
            resp = WebResponse.success(result.get("values"));
        }else{
            resp = WebResponse.success(new ArrayList());
        }
        resp.set("result_count", result.get("_total"));
        return resp;
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
}
