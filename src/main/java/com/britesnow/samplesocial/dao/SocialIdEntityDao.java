package com.britesnow.samplesocial.dao;

import com.britesnow.samplesocial.entity.Service;
import com.britesnow.samplesocial.entity.SocialIdEntity;


public class SocialIdEntityDao extends BaseHibernateDao<SocialIdEntity> {

    public SocialIdEntity getSocialdentity(Long user_id,Service service) {
        SocialIdEntity socialdentity = (SocialIdEntity) daoHelper.findFirst("from SocialIdEntity u where u.service = ? and u.user_id = ? ", service,user_id);
        return socialdentity;
    }
}
