package com.britesnow.samplesocial.web;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Folder;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.mail.MailInfo;
import com.britesnow.samplesocial.service.GmailImapService;
import com.britesnow.snow.util.Pair;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.handler.annotation.WebResourceHandler;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleEmailHandlers {
    @Inject
    GmailImapService gmailImapService;

    @WebGet("/gmail/folders")
    public WebResponse listFolders(@WebUser User user) throws Exception {
        Folder[] folders = gmailImapService.listFolders();
        List list = new ArrayList();
        for (Folder folder : folders) {
            Map map = new HashMap();
            map.put("name", folder.getName());
            map.put("fullName", folder.getFullName());
            list.add(map);
        }
        return WebResponse.success(list);
    }
    
    @WebPost("/gmail/folder/save")
    public WebResponse saveFolder(@WebUser User user, @WebParam("oldName") String oldFolderName, @WebParam("name") String folderName, RequestContext rc) throws Exception {
        boolean result = gmailImapService.saveFolder(oldFolderName, folderName);
        if (result) {
            return WebResponse.success(result);
        }else {
            return WebResponse.fail(String.format("Save Folder %s fail", folderName));
        }
    }

    @WebPost("/gmail/folder/delete")
    public WebResponse deleteFolder(@WebUser User user, @WebParam("folderName") String folderName, RequestContext rc) throws Exception {
        boolean result = gmailImapService.deleteFolder(folderName);
        if (result) {
            return WebResponse.success(result);
        }else {
            return WebResponse.fail(String.format("Delete Folder %s fail", folderName));
        }
    }

    @WebGet("/gmail/get")
    public WebResponse getEmail(@WebUser User user, @WebParam("id") Integer id) throws Exception {
        MailInfo info = gmailImapService.getEmail(id);
        return WebResponse.success(info);
    }



    @WebPost("/gmail/delete")
    public WebResponse deleteEmail(@WebUser User user,
                              @WebParam("id") Integer id, RequestContext rc) throws Exception {
        gmailImapService.deleteEmail(id);
        return WebResponse.success(true);
    }
    
    @WebPost("/gmail/trash")
    public WebResponse trashEmail(@WebUser User user,
                            @WebParam("id") Integer id, RequestContext rc) throws Exception {
        gmailImapService.trashEmail(id);
        return WebResponse.success(true);
    }


    @WebPost("/gmail/send")
    public WebResponse sendMail(@WebUser User user,
                           @WebModel Map m, @WebParam("subject") String subject,
                           @WebParam("content") String content, @WebParam("to") String to,@WebParam("cc") String cc, @WebParam("files") FileItem[] attachments, RequestContext rc) throws Exception {
        gmailImapService.sendMail(subject, content, to, cc, attachments);
        return WebResponse.success();
    }

    @WebGet("/gmail/search")
    public WebResponse search(@WebUser User user,
                        @WebParam("subject") String subject, @WebParam("from") String from,
                        @WebParam("to") String to,@WebParam("body") String body,
                        @WebParam("startDate") String startDate, @WebParam("endDate") String endDate,
                        @WebParam("startReceivedDate") String startReceivedDate, @WebParam("endReceivedDate") String endReceivedDate,
                        @WebParam("label") String label,@WebParam("hasAttachment") String hasAttachment,
                        @WebParam("attachmentName") String attachmentName,@WebParam("cc") String cc,
                        @WebParam("list") String list, @WebParam("hasCircle") String hasCircle , 
                        @WebParam("circle") String circle , @WebParam("chatContent") String chatContent ,
                        @WebParam("unread") String unread, @WebParam("category") String category , 
                        @WebParam("deliveredTo") String deliveredTo , @WebParam("rfc822msgid") String rfc822msgid ,
                        @WebParam("minSize") Integer minSize,@WebParam("maxSize") Integer maxSize,
                        @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex) throws Exception {
        
        if(minSize == null ){
        	minSize = 0;
        }
        if(maxSize == null){
        	maxSize = Integer.MAX_VALUE;
        }
        if(hasAttachment == null || hasAttachment.equalsIgnoreCase("false")){
        	hasAttachment = null;
        }
        if(unread == null || unread.equalsIgnoreCase("false")){
        	unread = null;
        }
        if(hasCircle == null || hasCircle.equalsIgnoreCase("false")){
        	hasCircle = null;
        }
        String regex = "^.*[\\s\\(\\){}\\|].*$";
        if(circle !=null){
	        circle = circle.replace("\"", "\\\"");
	        if( circle.matches(regex) ){
	        	circle = "\""+ circle + "\"";
	        }
        }
        
    	Pair<Integer, List<MailInfo>> pair = gmailImapService.search(subject, from, to, body,
    			startDate, endDate, startReceivedDate, endReceivedDate, label, hasAttachment,
    			attachmentName , cc , list,  hasCircle ,  circle ,  chatContent , unread,
    			category , deliveredTo , rfc822msgid , minSize, maxSize, pageSize * pageIndex + 1, pageSize);
        List<MailInfo> mailInfos = pair.getSecond();
        return WebResponse.success(mailInfos).set("result_count", pair.getFirst());
    }

    @WebResourceHandler(matches = "/gmail/attachment")
    public void getAttachment(@WebUser User user, @WebParam("messageId") Integer messageId, @WebParam("attachmentId") Integer attachmentId, @WebParam("name") String name, RequestContext rc) throws Exception {
        InputStream is = gmailImapService.getAttachment(messageId, attachmentId);
        HttpServletResponse res = rc.getRes();
        res.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes()));
        res.setContentType("application/octet-stream");
        OutputStream out = res.getOutputStream();
        
        byte[] buf = new byte[4096];
        int bytesRead;
        while((bytesRead = is.read(buf))!=-1) {
            out.write(buf, 0, bytesRead);
        }
        
        out.flush();
        out.close();
    }
}
