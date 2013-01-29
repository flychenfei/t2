package com.britesnow.samplesocial.service;

import com.britesnow.samplesocial.entity.SocialIdEntity;


public interface AuthService {
    
    SocialIdEntity getSocialIdEntity(Long userId);
}
