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
import com.restfb.Facebook;
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
        // if (limit == null || limit <= 0) {
        // limit = 10;
        // }
        // if (offset == null || offset < 0) {
        // offset = 0;
        // }
        // Connection<User> myFriends = null;
        // if (StringUtils.isNotBlank(query)) {
        // myFriends = new DefaultFacebookClient(accessToken).fetchConnection("me/friends", User.class,
        // Parameter.with("q", query), Parameter.with("limit", limit), Parameter.with("offset", offset));
        // } else {
        // myFriends = new DefaultFacebookClient(accessToken).fetchConnection("me/friends", User.class,
        // Parameter.with("limit", limit), Parameter.with("offset", offset));
        // }
        // return myFriends.getData();
        String fql = "SELECT name,uid,email FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me())";
        if (StringUtils.isNotBlank(query)) {
            fql += " and name='" + query + "'";
        }
        List<FqlUser> users = new DefaultFacebookClient(accessToken).executeQuery(fql, FqlUser.class);
        return users;

    }

    public String publish(String accessToken, String userId, String message) {
        FacebookType publishMessageResponse = new DefaultFacebookClient(accessToken).publish(userId + "/feed", FacebookType.class, Parameter.with("message", message));
        return publishMessageResponse.getId();
    }

    public String publishPhoto(String accessToken, String userId, String message, InputStream is) {
        FacebookType publishPhotoResponse = new DefaultFacebookClient(accessToken).publish(userId + "/photos", FacebookType.class, BinaryAttachment.with("userId", is), Parameter.with("message", message));
        return publishPhotoResponse.getId();
    }

    public List getFeedList(String accessToken, String userId, String type, Integer limit, Integer offset) {
        Connection<JsonObject> myFeed = new DefaultFacebookClient(accessToken).fetchConnection(userId + "/feed", JsonObject.class, Parameter.with("type", type), Parameter.with("limit", 25), Parameter.with("offset", 0));
        List ls = myFeed.getData();
        List ls2 = new ArrayList();
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

    public static class FqlUser {
        @Facebook
        String uid;

        @Facebook
        String name;
        @Facebook
        String email;
        @Facebook
        String hometownname;

        @Override
        public String toString() {
            return name + "  " + uid;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getHometownname() {
            return hometownname;
        }

        public void setHometownname(String hometownname) {
            this.hometownname = hometownname;
        }
    }
}
