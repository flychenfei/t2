package com.britesnow.samplesocial.web;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.LinkedInService;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

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
}
