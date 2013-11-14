package com.britesnow.samplesocial.web;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Folder;

import com.britesnow.samplesocial.mail.MailInfo;
import com.britesnow.samplesocial.model.User;
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

@Singleton
public class GoogleEmailHandlers {
    @Inject
    GMailService gMailService;

    @WebGet("/gmail/folders")
    public WebResponse listFolders(@WebUser User user) throws Exception {
        Folder[] folders = gMailService.listFolders();
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
        boolean result = gMailService.deleteFolder(folderName);
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
        
    	Pair<Integer, List<MailInfo>> pair = gMailService.listMails("inbox", pageSize*pageIndex+1, pageSize);
        List<MailInfo> mailInfos = pair.getSecond();

        return WebResponse.success(mailInfos).set("result_count", pair.getFirst());
    }

    @WebGet("/gmail/get")
    public WebResponse getEmail(@WebUser User user, @WebParam("id") Integer id) throws Exception {
        MailInfo info = gMailService.getEmail(id);
        return WebResponse.success(info);
    }



    @WebPost("/gmail/delete")
    public WebResponse deleteEmail(@WebUser User user,
                              @WebParam("id") Integer id, RequestContext rc) throws Exception {
        gMailService.deleteEmail(id);
        return WebResponse.success(true);
    }


    @WebPost("/gmail/send")
    public WebResponse sendMail(@WebUser User user,
                           @WebModel Map m, @WebParam("subject") String subject,
                           @WebParam("content") String content, @WebParam("to") String to, RequestContext rc) throws Exception {
        gMailService.sendMail(subject, content, to);
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
                        @WebParam("unread") String unread, 
                        @WebParam("minSize") Integer minSize,@WebParam("maxSize") Integer maxSize,
                        @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex) throws Exception {
        
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date sDate,eDate,srDate,erDate;
//        if(startDate != null){
//           startDate = startDate + " 00:00:00";
//           sDate = format.parse(startDate);
//        }else{
//        	 sDate = null;
//        }
//        if(endDate !=null){
//            endDate = endDate + " 23:59:59";
//            eDate = format.parse(endDate);
//        }else{
//            eDate = null;
//        }
//        if(startReceivedDate !=null){
//        	startReceivedDate = startReceivedDate + " 00:00:00";
//        	srDate = format.parse(startReceivedDate);
//        }else{
//            srDate = null;
//        }
//        if(endReceivedDate !=null){
//        	endReceivedDate = endReceivedDate + " 23:59:59";
//            erDate = format.parse(endReceivedDate);
//        }else{
//        	erDate = null;
//        }
        if(minSize == null ){
        	minSize = 0;
        }
        if(maxSize == null){
        	maxSize = Integer.MAX_VALUE;
        }
        if(hasAttachment.equalsIgnoreCase("false")){
        	hasAttachment = null;
        }
        if(unread.equalsIgnoreCase("false")){
        	unread = null;
        }
        if(hasCircle.equalsIgnoreCase("false")){
        	hasCircle = null;
        }
        
        String regex = "^.*[\\s\\(\\){}\\|].*$";
        if(circle !=null){
	        circle = circle.replace("\"", "\\\"");
	        if( circle.matches(regex) ){
	        	circle = "\""+ circle + "\"";
	        }
        }
        
//        Pair<Integer, List<MailInfo>> pair = gMailService.search(subject, from, to, body,
//              sDate, eDate, srDate, erDate, minSize, maxSize, pageSize * pageIndex + 1, pageSize);
    	Pair<Integer, List<MailInfo>> pair = gMailService.gmailSearch(subject, from, to, body,
    			startDate, endDate, startReceivedDate, endReceivedDate, label, hasAttachment,
    			attachmentName , cc , list,  hasCircle ,  circle ,  chatContent , unread,
    			minSize, maxSize, pageSize * pageIndex + 1, pageSize);
        List<MailInfo> mailInfos = pair.getSecond();
        return WebResponse.success(mailInfos).set("result_count", pair.getFirst());
    }

}
