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
import fi.foyt.foursquare.api.entities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Singleton
public class FoursquareHandlers {
    @Inject
    private FoursquareService foursquareService;

    @WebGet("/foursquare/getUserFriends")
    public WebResponse getFriends(@WebUser User user, RequestContext rc) throws Exception {
        if (user != null) {
            Result<UserGroup> result = foursquareService.getFriends(user.getId());
            if(result.getMeta().getCode() == 200) {
                return WebResponse.success(result.getResult());
            }{
                return WebResponse.fail();
            }
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
            if (result.getMeta().getCode() == 200) {
                return WebResponse.success(result.getResult());
            }else{
                return WebResponse.fail();
            }

        }else {
            return WebResponse.fail();
        }
    }
    @WebGet("/foursquare/recentCheckins")
    public WebResponse recentCheckins(@WebUser User user, RequestContext rc, @WebParam("ll") String ll,
                                  @WebParam("limit") Integer limit,@WebParam("after") String after) throws Exception {
        if (user != null) {
            long afterTime = System.currentTimeMillis() - 4 * 1000*60*60;
            Result<Checkin[]> result = foursquareService.recentCheckins(user.getId(), ll, limit, afterTime);
            return WebResponse.success(result);
        }else {
            return WebResponse.fail();
        }
    }

    @WebGet("/foursquare/venuesCategories")
    public WebResponse venuesCategories(@WebUser User user, RequestContext rc) throws Exception {
        if (user != null) {
            Result<Category[]> result = foursquareService.venuesCategories(user.getId());
            if(result.getMeta().getCode()==200){
                return WebResponse.success(result.getResult());
            }
        }
        return WebResponse.fail();
    }

    @WebGet("/foursquare/venuesTrending")
    public WebResponse venuesTrending(@WebUser User user, RequestContext rc, @WebParam("ll") String ll,
                                      @WebParam("limit") Integer limit,@WebParam("after") Integer radius) throws Exception {
        if (user != null) {
            Result<CompactVenue[]> result = foursquareService.venuesTrending(user.getId(), ll, limit, radius);
            if (result.getMeta().getCode() == 200) {
                return WebResponse.success(convertCompactVenues(result.getResult()));
            }
        }

        return WebResponse.fail();
    }

    /**
     * convert compact venues array to map list
     * @param result  compact venues array
     * @return list of map
     */
    private List convertCompactVenues(CompactVenue[] result) {
        List<Map> list = new ArrayList<Map>();
        for (CompactVenue compactVenue : result) {
            Map map = convertCompactVenue(compactVenue);
            list.add(map);
        }
        return list;
    }

    private Map convertCompactVenue(CompactVenue compactVenue) {
        Map map = new HashMap();
        Category[] cgs = compactVenue.getCategories();
        if (cgs != null && cgs.length > 0) {
            map.put("category", cgs[0].getName());
        } else {
            map.put("category", "");
        }
        map.put("name", compactVenue.getName());
        map.put("url", compactVenue.getUrl());
        map.put("location", buildLocation(compactVenue.getLocation()));
        return map;
    }

    private String buildLocation(Location location) {
        StringBuilder builder = new StringBuilder();
        //"{address},{crossStreet},{city},{country}, {postalCode}"
        if (location.getAddress() != null) {
            builder.append(location.getAddress()).append(",");
        }
        if (location.getCrossStreet() != null) {
            builder.append(location.getCrossStreet()).append(",");
        }
        if (location.getCity() != null) {
            builder.append(location.getCity()).append(",");
        }
        if (location.getCountry() != null) {
            builder.append(location.getCountry()).append(",");
        }
        if (location.getPostalCode() != null) {
            builder.append(location.getPostalCode());
        }
        return builder.toString();

    }

    @WebGet("/foursquare/venuesSearch")
    public WebResponse venuesSearch(@WebUser User user, RequestContext rc) throws Exception {
        if (user != null) {
            Map venues = rc.getParamMap("venues.");
            Result<VenuesSearchResult> result = foursquareService.venuesSearch(user.getId(), venues);
            return WebResponse.success(convertCompactVenues(result.getResult().getVenues()));
        }else {
            return WebResponse.fail();
        }
    }

    @WebGet("/foursquare/venuesExplore")
    public WebResponse venuesExplore(@WebUser User user, RequestContext rc, @WebParam("ll") String ll, @WebParam("limit") Integer limit) throws Exception {
        if (user != null) {
            Result<Recommended> result = foursquareService.venuesExplore(user.getId(), ll, limit);
            if (result.getMeta().getCode() == 200) {
                List<Map> dtos = new ArrayList<Map>();
                for (RecommendationGroup group : result.getResult().getGroups()) {
                    for (Recommendation recommendation : group.getItems()) {
                        Map dto = convertCompactVenue(recommendation.getVenue());
                        dtos.add(dto);
                    }
                }
                return WebResponse.success(dtos);
            }
        }

        return WebResponse.fail();
    }

    @WebGet("/foursquare/specialSearch")
    public WebResponse specialSearch(@WebUser User user, RequestContext rc, @WebParam("ll") String ll, @WebParam("limit") Integer limit) throws Exception {
        if (user != null) {
            Result<SpecialGroup> result = foursquareService.specialSearch(user.getId(), ll, limit);
            if (result.getMeta().getCode() == 200) {
                return WebResponse.success(result.getResult());
            }
        }

        return WebResponse.fail();
    }

    @WebGet("/foursquare/usersBadges")
    public WebResponse usersBadges(@WebUser User user, RequestContext rc) throws Exception {
        if (user != null) {
            Result<Badges> result = foursquareService.usersBadges(user.getId());
            if (result.getMeta().getCode() == 200) {
                return WebResponse.success(result.getResult().getBadges());
            }
        }

        return WebResponse.fail();
    }

}
