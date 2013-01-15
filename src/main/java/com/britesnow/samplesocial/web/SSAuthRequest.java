package com.britesnow.samplesocial.web;

import java.util.Map;

import com.britesnow.samplesocial.dao.SocialIdEntityDao;
import com.britesnow.samplesocial.dao.UserDao;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.FacebookAuthService;
import com.britesnow.snow.util.ObjectUtil;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.auth.AuthRequest;
import com.britesnow.snow.web.auth.AuthToken;
import com.britesnow.snow.web.handler.annotation.WebActionHandler;
import com.britesnow.snow.web.handler.annotation.WebModelHandler;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.google.common.base.Objects;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SSAuthRequest implements AuthRequest {
    @Inject
    private UserDao           userDao;
    @Inject
    private FacebookAuthService facebookAuthService;
    
    @Inject
    private SocialIdEntityDao socialIdEntityDao;
    
    @Override
    public AuthToken authRequest(RequestContext rc) {
        // Note: this is not the login logic, the login logic would be
        // @WebActionHandler that would generate the appropriate

        // Note: this is a simple stateless authentication scheme.
        // Security is medium-low, however, with little bit more logic
        // it can be as secure as statefull login while keeping it's scalability attributes

        // First, we get userId and userToken from cookie
        String userIdStr = rc.getCookie("userId");
        String userToken = rc.getCookie("userToken");

        if (userIdStr != null && userToken != null) {
            // get the User from the DAO
            Long userId = ObjectUtil.getValue(userIdStr, Long.class, null);
            User user = userDao.get(userId);
            if (user == null) {
                // if session timeout, need login again.
                return null;
            }

            // Build the expectedUserToken from the user info
            // For this example, simplistic userToken (sha1(username,password))
            String expectedUserToken = Hashing.sha1().hashString(user.getUsername() + user.getId()).toString();

            if (Objects.equal(expectedUserToken, userToken)) {
                // if valid, then, we create the AuthTocken with our User object
                AuthToken<User> authToken = new AuthToken<User>();
                authToken.setUser(user);
                return authToken;

            } else {
                // otherwise, we could throw an exception, or just return null
                // In this example (and snowStarter, we just return null)
                return null;
            }
        } else {
            return null;
        }
    }

    @WebModelHandler(startsWith = "/")
    public void pageIndex(@WebModel Map m, @WebUser User user, RequestContext rc) {
        // gameTestManager.init();
        m.put("user", user);
    }

    @WebModelHandler(startsWith = "/logout")
    public void logout(@WebModel Map m, @WebUser User user, RequestContext rc) {
        if (user != null) {
            //remove cookie
//            for(Cookie c : rc.getReq().getCookies()){
//                String userToken = "userToken";
//                String userId = "userId";
//                if(userToken.equals(c.getName()) || userId.equals(c.getName())){
//                    c.setPath("/");
//                    c.setMaxAge(0);
//                    rc.getRes().addCookie(c);
//                }
//            }
        }
    }

    @WebActionHandler
    public Object login(@WebParam("userId") Long userId, @WebParam("username") String username,
                            @WebParam("password") String password, RequestContext rc) {
        User user = userDao.getUser(username);

        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setPassword(password);
            userDao.save(user);
            return user;
        } else if (authentication(user, password)) {
            setUserToSession(rc, user);
            return user;
        }
        return "null";
    }

    // --------- Private Helpers --------- //
    // store the user in the session. If user == null, then, remove it.
    private void setUserToSession(RequestContext rc, User user) {
        // TODO: need to implement session less login (to easy loadbalancing)
        if (user != null) {
            String userToken = Hashing.sha1().hashString(user.getUsername() + user.getId()).toString();
            rc.setCookie("userToken", userToken);
            rc.setCookie("userId", user.getId());
            //
        }
    }

    private boolean authentication(User user, String password) {
        if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
            return true;
        } else {
            return false;
        }
    }
    // --------- /Private Helpers --------- //
}