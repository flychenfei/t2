package com.britesnow.samplesocial.web;


import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.mail.MailInfo;
import com.britesnow.samplesocial.service.GMailService;
import com.britesnow.snow.util.Pair;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.mail.Folder;
import javax.mail.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class GoogleEmailHandlers {
    @Inject
    GMailService gMailService;

    @WebGet("/gmail/folders")
    public WebResponse listFolders(@WebUser User user) throws Exception {
        Folder[] folders = gMailService.listFolders(user);
        List list = new ArrayList();
        for (Folder folder : folders) {
            Map map = new HashMap();
            map.put("name", folder.getName());
            map.put("fullName", folder.getFullName());
            list.add(map);
        }
        return WebResponse.success(list);
    }

    @WebPost("/gmail/folder/delete")
    public WebResponse deleteFolder(@WebUser User user, @WebParam("folderName") String folderName, RequestContext rc) throws Exception {
        boolean result = gMailService.deleteFolder(user, folderName);
        if (result) {
            return WebResponse.success(result);
        }else {
            return WebResponse.fail(String.format("Delete Folder %s fail", folderName));
        }
    }

    @WebGet("/gmail/list")
    public WebResponse listEmails(@WebUser User user,
                           @WebParam("folderName") String folderName,
                           @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex) throws Exception {
        Pair<Integer, Message[]> pair = gMailService.listMails(user, "inbox", pageSize*pageIndex+1, pageSize);
        List<MailInfo> mailInfos = new ArrayList<MailInfo>();

        for (Message message : pair.getSecond()) {
            MailInfo info = gMailService.buildMailInfo(message);
            mailInfos.add(0, info);
        }
        return WebResponse.success(mailInfos).set("result_count", pair.getFirst());
    }

    @WebGet("/gmail/get")
    public WebResponse getEmail(@WebUser User user, @WebParam("id") Integer id) throws Exception {
        MailInfo info = gMailService.getEmail(user, id);
        return WebResponse.success(info);
    }



    @WebPost("/gmail/delete")
    public WebResponse deleteEmail(@WebUser User user,
                              @WebParam("id") Integer id, RequestContext rc) throws Exception {
        gMailService.deleteEmail(user, id);
        return WebResponse.success(true);
    }


    @WebPost("/gmail/send")
    public WebResponse sendMail(@WebUser User user,
                           @WebModel Map m, @WebParam("subject") String subject,
                           @WebParam("content") String content, @WebParam("to") String to, RequestContext rc) throws Exception {
        gMailService.sendMail(user, subject, content, to);
        return WebResponse.success();
    }

    @WebGet("/gmail/search")
    public WebResponse search(@WebUser User user,
                        @WebParam("subject") String subject, @WebParam("from") String from,
                        @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex) throws Exception {

        Pair<List<MailInfo>, Integer> pair = gMailService.search(user, subject, from, pageSize, pageIndex);
        return WebResponse.success(pair.getFirst()).setResultCount(pair.getSecond());
    }

}
