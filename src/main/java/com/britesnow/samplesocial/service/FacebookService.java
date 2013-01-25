package com.britesnow.samplesocial.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.restfb.BinaryAttachment;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.JsonMapper;
import com.restfb.Parameter;
import com.restfb.json.JsonObject;
import com.restfb.types.FacebookType;
import com.restfb.types.User;

public class FacebookService {
    public User getMyInformation(String accessToken) {
        User user = new DefaultFacebookClient(accessToken).fetchObject("me", User.class);
        return user;
    }

    public User getFriendInformation(String accessToken, String userId) {
        User user = new DefaultFacebookClient(accessToken).fetchObject(userId, User.class);
        return user;
    }

    public List getAllFriends(String accessToken) {
        Connection<User> myFriends = new DefaultFacebookClient(accessToken).fetchConnection("me/friends", User.class);
        return myFriends.getData();
    }

    public List getFriendsByPage(String accessToken, String query, Integer limit, Integer offset) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        if (offset == null || offset < 0) {
            offset = 0;
        }
        Connection<User> myFriends = null;
        if (StringUtils.isNotBlank(query)) {
            myFriends = new DefaultFacebookClient(accessToken).fetchConnection("me/friends", User.class, Parameter.with("q", query), Parameter.with("limit", limit), Parameter.with("offset", offset));
        } else {
            myFriends = new DefaultFacebookClient(accessToken).fetchConnection("me/friends", User.class, Parameter.with("limit", limit), Parameter.with("offset", offset));
        }
        return myFriends.getData();
    }

    public String publish(String accessToken, String userId, String message) {
        FacebookType publishMessageResponse = new DefaultFacebookClient(accessToken).publish(userId + "/feed", FacebookType.class, Parameter.with("message", message));
        return publishMessageResponse.getId();
    }

    public String publishPhoto(String accessToken, String userId, String message, InputStream is) {
        FacebookType publishPhotoResponse = new DefaultFacebookClient(accessToken).publish(userId + "/photos", FacebookType.class, BinaryAttachment.with("userId", is), Parameter.with("message", message));
        return publishPhotoResponse.getId();
    }

    public List getFeedList(String accessToken, String userId) {
        Connection<JsonObject> myFeed = new DefaultFacebookClient(accessToken).fetchConnection("me/feed", JsonObject.class);
        List ls = myFeed.getData();
        List ls2 = new ArrayList();
        JsonMapper jsonMapper = new DefaultJsonMapper();
        for (int i = 0; i < ls.size(); i++) {
            JsonObject ob = (JsonObject) ls.get(i);
            Iterator it = ob.keys();
            Map m = new HashMap();
            while (it.hasNext()) {
                String key = (String) it.next();
                Object value = (Object) ob.get(key);
                if (value instanceof String) {
                    m.put(key, value);
                }
            }
            ls2.add(m);
        }
        return ls2;
    }
}
