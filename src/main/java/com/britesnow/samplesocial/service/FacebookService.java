package com.britesnow.samplesocial.service;

import com.restfb.BinaryAttachment;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import com.restfb.types.Post;
import com.restfb.types.User;

import java.io.InputStream;
import java.util.List;

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

    public List getFriendsByPage(String accessToken, Integer limit, Integer offset) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        if (offset == null || offset < 0) {
            offset = 0;
        }
        Connection<User> myFriends = new DefaultFacebookClient(accessToken).fetchConnection("me/friends", User.class, Parameter.with("limit", limit), Parameter.with("offset", offset));
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
        Connection<Post> myFeed = new DefaultFacebookClient(accessToken).fetchConnection("me/feed", Post.class);
        return myFeed.getData();
    }
}
