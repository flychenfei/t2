package com.britesnow.samplesocial.dao;

import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.oauth.ServiceType;


public class SocialIdEntityDao extends BaseHibernateDao<SocialIdEntity> {

    public SocialIdEntity getSocialdentity(Long user_id,ServiceType service) {
        SocialIdEntity socialdentity = (SocialIdEntity) daoHelper.findFirst("from SocialIdEntity u where u.service = ? and u.user_id = ? ", service,user_id);
        return socialdentity;
    }
}
