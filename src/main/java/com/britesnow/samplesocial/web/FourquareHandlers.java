package com.britesnow.samplesocial.web;


import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.FoursquareService;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.Checkin;
import fi.foyt.foursquare.api.entities.CompactUser;
import fi.foyt.foursquare.api.entities.CompleteUser;
import fi.foyt.foursquare.api.entities.UserGroup;


@Singleton
public class FourquareHandlers {
    @Inject
    private FoursquareService foursquareService;

    @WebGet("/foursquare/getUserFriends")
    public WebResponse getFriends(@WebUser User user, RequestContext rc) throws Exception {
        if (user != null) {
            Result<UserGroup> result = foursquareService.getFriends(user.getId());
            return WebResponse.success(result);
        }else {
            return WebResponse.fail();
        }
    }

    @WebGet("/foursquare/getUserInfo")
    public WebResponse getUserInfo(@WebUser User user, RequestContext rc) throws Exception {
        if (user != null) {
            Result<CompleteUser> result = foursquareService.getUserInfo(user.getId());
            return WebResponse.success(result);
        }else {
            return WebResponse.fail();
        }
    }

    @WebGet("/foursquare/searchUser")
    public WebResponse searchUser(@WebUser User user, RequestContext rc, @WebParam("phone") String phone,
                                  @WebParam("email") String email,@WebParam("twitter") String twitter,
                                  @WebParam("twitterSource") String twitterSource, @WebParam("fbid") String fbid,
                                  @WebParam("name") String name) throws Exception {
        if (user != null) {
            Result<CompactUser[]> result = foursquareService.userSearch(user.getId(), phone, email, twitter, twitterSource, fbid, name);
            return WebResponse.success(result);
        }else {
            return WebResponse.fail();
        }
    }
    @WebGet("/foursquare/recentCheckins")
    public WebResponse recentCheckins(@WebUser User user, RequestContext rc, @WebParam("ll") String ll,
                                  @WebParam("limit") int limit,@WebParam("after") String after) throws Exception {
        if (user != null) {
            long afterTime = System.currentTimeMillis() - 4 * 1000*60*60;
            Result<Checkin[]> result = foursquareService.recentCheckins(user.getId(), ll, limit, afterTime);
            return WebResponse.success(result);
        }else {
            return WebResponse.fail();
        }
    }

}
