package com.britesnow.samplesocial.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.Checkin;
import fi.foyt.foursquare.api.entities.CompactUser;
import fi.foyt.foursquare.api.entities.CompleteUser;
import fi.foyt.foursquare.api.entities.UserGroup;


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

}
