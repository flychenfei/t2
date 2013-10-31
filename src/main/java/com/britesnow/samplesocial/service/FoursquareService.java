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

    /**
     * get user friends
     * @param userId user id
     * @return  userGroup result.
     * @throws FoursquareApiException
     */
    public Result<UserGroup> getFriends() throws FoursquareApiException {
        return oAuthService.getApi().usersFriends(null);
    }

    public Result<CompleteUser> getUserInfo() throws FoursquareApiException {
        return oAuthService.getApi().user(null);
    }

    public Result<CompactUser[]> userSearch(String phone, String email, String twitter, String twiiterSource, String fbid, String name) throws FoursquareApiException {
        return oAuthService.getApi().usersSearch(phone, email, twitter, twiiterSource, fbid, name);
    }


    public Result<Checkin[]> recentCheckins(String ll, int limit, long afterTime) throws FoursquareApiException {
        return oAuthService.getApi().checkinsRecent(ll, limit, afterTime);
    }

    public Result<Category[]> venuesCategories() throws FoursquareApiException {
        return oAuthService.getApi().venuesCategories();
    }

    public Result<CompactVenue[]> venuesTrending(String ll, Integer limit, Integer radius) throws FoursquareApiException {
       return   oAuthService.getApi().venuesTrending(ll, limit, radius);
    }

    public Result<CompactUser[]> usersRequests() throws FoursquareApiException {
       return oAuthService.getApi().usersRequests();
    }

    public Result<VenuesSearchResult> venuesSearch(Map param) throws FoursquareApiException {
        return oAuthService.getApi().venuesSearch(param);
    }
    public Result<Recommended> venuesExplore(String ll, Integer limit) throws FoursquareApiException {

        double llAcc = 10000;
        double alt = 0;
        int radius = 250;
        if (limit == null) {
            limit = 10;
        }
        String section = "food";
        String query="";
        String basic="";
        Double altacc=0d;
        return oAuthService.getApi().venuesExplore(ll, llAcc, alt, altacc, radius, section, query, limit, basic);
    }
    public Result<SpecialGroup> specialSearch(String ll, Integer limit) throws FoursquareApiException {

        double llAcc = 10000;
        double alt = 0;
        if (limit == null) {
            limit = 10;
        }
        Double altacc=0d;
        return oAuthService.getApi().specialsSearch(ll, llAcc, alt, altacc, limit);
    }

    public Result<Badges> usersBadges() throws FoursquareApiException {
       return oAuthService.getApi().usersBadges(null);
    }

}
