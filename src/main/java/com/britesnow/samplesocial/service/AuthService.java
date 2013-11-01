package com.britesnow.samplesocial.service;

import com.britesnow.samplesocial.model.SocialIdEntity;


public interface AuthService {
    
    SocialIdEntity getSocialIdEntity();
    String getAuthorizationUrl();
}
