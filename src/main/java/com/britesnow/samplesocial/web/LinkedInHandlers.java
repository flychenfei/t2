package com.britesnow.samplesocial.web;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.LinkedService;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

@Singleton
public class LinkedInHandlers {
    @Inject
    private LinkedService linkedService;

    @WebGet("/linkedin/connects")
    public WebResponse getConnects(@WebUser User user, @WebParam("pageIndex") Integer pageIndex,@WebParam("pageSize") Integer pageSize) {
        Map result = linkedService.getConnections(user, pageIndex, pageSize);
        WebResponse resp = WebResponse.success(result.get("values"));
        resp.set("result_count", result.get("_total"));
        return resp;
    }
    @WebGet("/linkedin/jobs")
    public WebResponse searchJobs(@WebUser User user, @WebParam("pageIndex") Integer pageIndex,
                                  @WebParam("pageSize") Integer pageSize, @WebParam("keywork") String keywork) {
        Map result = linkedService.searchJobs(user, pageIndex, pageSize, keywork);
        Map jobs = (Map) result.get("jobs");
        WebResponse resp = WebResponse.success(jobs.get("values"));
        resp.set("result_count", result.get("numResults"));
        return resp;
    }
}
