package com.britesnow.samplesocial.web;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.snow.web.RequestContext;
import com.google.inject.Singleton;

@Singleton
public class WebUtil {
    public User getUser(RequestContext rc) {
        return rc.getUser(User.class);
    }
}