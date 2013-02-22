package com.britesnow.samplesocial.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.*;

import java.util.Map;


@Singleton
public class FoursquareService {
    @Inject
	private FoursquareAuthService oAuthService;


    public Result<UserGroup> getFriends(Long userId) throws FoursquareApiException {
        return oAuthService.getApi(userId).usersFriends(null);
    }

    public Result<CompleteUser> getUserInfo(Long userId) throws FoursquareApiException {
        return oAuthService.getApi(userId).user(null);
    }

    public Result<CompactUser[]> userSearch(Long userId, String phone, String email, String twitter, String twiiterSource, String fbid, String name) throws FoursquareApiException {
        return oAuthService.getApi(userId).usersSearch(phone, email, twitter, twiiterSource, fbid, name);
    }


    public Result<Checkin[]> recentCheckins(Long userId, String ll, int limit, long afterTime) throws FoursquareApiException {
        return oAuthService.getApi(userId).checkinsRecent(ll, limit, afterTime);
    }

    public Result<Category[]> venuesCategories(Long userId) throws FoursquareApiException {
        return oAuthService.getApi(userId).venuesCategories();
    }

    public Result<CompactVenue[]> venuesTrending(Long userId, String ll, Integer limit, Integer radius) throws FoursquareApiException {
       return   oAuthService.getApi(userId).venuesTrending(ll, limit, radius);
    }

    public Result<CompactUser[]> usersRequests(Long userId) throws FoursquareApiException {
       return oAuthService.getApi(userId).usersRequests();
    }

    public Result<VenuesSearchResult> venuesSearch(Long userId, Map param) throws FoursquareApiException {
        return oAuthService.getApi(userId).venuesSearch(param);
    }

}
