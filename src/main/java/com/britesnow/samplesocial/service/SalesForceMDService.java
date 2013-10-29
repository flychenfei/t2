package com.britesnow.samplesocial.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.sf.json.JSONObject;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Singleton;
import com.sforce.soap.metadata.AsyncRequestState;
import com.sforce.soap.metadata.AsyncResult;
import com.sforce.soap.metadata.ConnectedApp;
import com.sforce.soap.metadata.DeployOptions;
import com.sforce.soap.metadata.DeployResult;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.PackageTypeMembers;
import com.sforce.soap.metadata.RetrieveMessage;
import com.sforce.soap.metadata.RetrieveRequest;
import com.sforce.soap.metadata.RetrieveResult;
import com.sforce.ws.ConnectorConfig;

@Singleton
public class SalesForceMDService {
    
    private  static final String VERSION = "29.0";
    private  static final String SF_URL = "/services/data/v"+VERSION;
    private String  metadataServerUrl = null;
    
    public void pushCanvasApp(String token,String instanceUrl, ConnectedApp connectedApp){
        String metadataServerUrl = getMetaDataServerUrl(token, instanceUrl);
        final ConnectorConfig config = new ConnectorConfig();
        config.setServiceEndpoint(metadataServerUrl);
        config.setSessionId(token);
        MetadataConnection metadataConnection = null;
        try {
            metadataConnection = new MetadataConnection(config);
            
            byte[] bs = getCustomAppsAndProfiles(metadataConnection);
            Map<String, String> apps = unZip(bs,"unpackaged/applications/");
            Map<String, String> profiles = unZip(bs,"unpackaged/profiles/");
            
            File file = new File("/Users/friping/Desktop/test1.zip");
            FileOutputStream fos = new FileOutputStream(file);
            ZipOutputStream out1 = new ZipOutputStream(fos);
            
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ZipOutputStream out = new ZipOutputStream(byteStream);
            
            StringBuilder builder = new StringBuilder();
            builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            builder.append("<Package xmlns=\"http://soap.sforce.com/2006/04/metadata\">\n");
            
            //generate cavnas app
            if(connectedApp != null){
                builder.append("<types>\n");
                builder.append("<members>"+connectedApp.getFullName()+"</members>\n");
                builder.append("<name>ConnectedApp</name>\n");
                builder.append("</types>\n");
                
                builder.append("<types>\n");
                builder.append("<members>"+connectedApp.getFullName()+"_page</members>\n");
                builder.append("<name>ApexPage</name>\n");
                builder.append("</types>\n");
                
                builder.append("<types>\n");
                builder.append("<members>"+connectedApp.getFullName()+"_tab</members>\n");
                builder.append("<name>CustomTab</name>\n");
                builder.append("</types>\n");
                
            }
            
            if(apps.size() > 0){
                builder.append("<types>\n");
                builder.append("<members>*</members>\n");
                builder.append("<name>CustomApplication</name>\n");
                builder.append("</types>\n");
            }
            
            if(profiles.size() > 0){
                builder.append("<types>\n");
                builder.append("<members>*</members>\n");
                builder.append("<name>Profile</name>\n");
                builder.append("</types>\n");
            }
            
            builder.append("<version>29.0</version>\n");
            builder.append("</Package>\n");
            
            String page = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<ApexPage xmlns=\"http://soap.sforce.com/2006/04/metadata\">"
                                    + "    <description>This is a sample Visualforce page.</description>"
                                    + "    <label>"+connectedApp.getLabel()+" page</label>"
                                    + "    <fullName>"+connectedApp.getFullName()+"_page</fullName>"
                                    + "</ApexPage>";
            String pageInfo = "<apex:page >" + "  <apex:canvasApp applicationName=\""+connectedApp.getFullName()+"\""
                                    + "        namespacePrefix=\"britesnow_jss\""
                                    + "        height=\"500px\" width=\"800px\"/>"
                                    + "</apex:page>";
            String tab = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<CustomTab xmlns=\"http://soap.sforce.com/2006/04/metadata\">"
                                    + "    <fullName>"+connectedApp.getFullName()+"_tab</fullName>"
                                    + "    <label>"+connectedApp.getLabel()+" tab</label>"
                                    + "    <motif>Custom53: Bell</motif>"
                                    + "    <page>"+connectedApp.getFullName()+"_page</page>"
                                    + "</CustomTab>";
            
            zipFile(out, "unpackaged/package.xml", builder.toString());
            zipFile(out, "unpackaged/pages/"+connectedApp.getFullName()+"_page.page-meta.xml", page);
            zipFile(out, "unpackaged/pages/"+connectedApp.getFullName()+"_page.page", pageInfo);
            zipFile(out, "unpackaged/tabs/"+connectedApp.getFullName()+"_tab.tab", tab);
            zipFile(out, "unpackaged/connectedApps/"+connectedApp.getFullName()+".connectedApp", generateAppXml(connectedApp));
            for(Iterator<String> ite = apps.keySet().iterator(); ite.hasNext();){
                String key = ite.next();
                String value = apps.get(key);
                String t = "</tab>";
                int endIndex = value.lastIndexOf(t) + t.length();
                value = value.substring(0,endIndex) + "\n\t<tab>"+connectedApp.getFullName()+"_tab</tab>\n" + value.substring(endIndex+1);
                zipFile(out, key, value);
            }
            for(Iterator<String> ite = profiles.keySet().iterator(); ite.hasNext();){
                String key = ite.next();
                String value = profiles.get(key);
                String t = "</applicationVisibilities>";
                int endIndex = value.lastIndexOf(t) + t.length();
                value = value.substring(0,endIndex) + "\n\t<tabVisibilities><tab>"+connectedApp.getFullName()+"_tab</tab><visibility>DefaultOn</visibility></tabVisibilities>\n" + value.substring(endIndex+1);
                zipFile(out, key, value);
            }
            
            out.close();
            
            
            
            zipFile(out1, "unpackaged/package.xml", builder.toString());
            zipFile(out1, "unpackaged/pages/"+connectedApp.getFullName()+"_page.page-meta.xml", page);
            zipFile(out1, "unpackaged/pages/"+connectedApp.getFullName()+"_page.page", pageInfo);
            zipFile(out1, "unpackaged/tabs/"+connectedApp.getFullName()+"_tab.tab", tab);
            zipFile(out1, "unpackaged/connectedApps/"+connectedApp.getFullName()+".connectedApp", generateAppXml(connectedApp));
            
            for(Iterator<String> ite = apps.keySet().iterator(); ite.hasNext();){
                String key = ite.next();
                String value = apps.get(key);
                String t = "</tab>";
                int endIndex = value.lastIndexOf(t) + t.length();
                value = value.substring(0,endIndex) + "\n\t<tab>"+connectedApp.getFullName()+"_tab</tab>\n" + value.substring(endIndex+1);
                zipFile(out1, key, value);
            }
            for(Iterator<String> ite = profiles.keySet().iterator(); ite.hasNext();){
                String key = ite.next();
                String value = profiles.get(key);
                String t = "</applicationVisibilities>";
                int endIndex = value.lastIndexOf(t) + t.length();
                value = value.substring(0,endIndex) + "\n\t<tabVisibilities><tab>"+connectedApp.getFullName()+"_tab</tab><visibility>DefaultOn</visibility></tabVisibilities>\n" + value.substring(endIndex+1);
                zipFile(out1, key, value);
            }
            
            out1.close();
            fos.close();
            
            
            byte[] zipBytes = byteStream.toByteArray(); 
            DeployOptions deployOptions = new DeployOptions();
            deployOptions.setPerformRetrieve(false);
            deployOptions.setRollbackOnError(true);
            AsyncResult asyncResult = metadataConnection.deploy(zipBytes, deployOptions);
            DeployResult deployResult = waitForDeployCompletion(asyncResult.getId(),metadataConnection);
            if (!deployResult.isSuccess()) {
                System.out.println("The files were not successfully deployed: "+deployResult.getErrorMessage());
            }
            
            byteStream.close();
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * get User info
     * @param token
     * @return
     */
    public Map<String,String> getloginInfo(String token, String instance_url) {
        Map<String,String> result = new HashMap<String,String>();
        //  Get User Info
        OAuthRequest oauth = new OAuthRequest(Verb.GET,instance_url+SF_URL);
        oauth.addHeader("Authorization", "Bearer "+token);
        oauth.addHeader("X-PrettyPrint", "1");
        Response res = oauth.send();
        String body = res.getBody();
        Map opts = JsonUtil.toMapAndList(body);
        
        // it contains all api url
        String identityUrl = opts.get("identity").toString();
        oauth = new OAuthRequest(Verb.GET,identityUrl);
        oauth.addHeader("Authorization", "Bearer "+token);
        oauth.addHeader("X-PrettyPrint", "1");
        result = JsonUtil.toMapAndList(oauth.send().getBody());
        return result;
    }
    
    private byte[] getCustomAppsAndProfiles(MetadataConnection metadataConnection){
        try{
            
            RetrieveRequest retrieveRequest = new RetrieveRequest();
            retrieveRequest.setApiVersion(new Double(VERSION));
            com.sforce.soap.metadata.Package p = new com.sforce.soap.metadata.Package();
            p.setVersion(VERSION);
            
            PackageTypeMembers ptm = new PackageTypeMembers();
            ptm.setName("CustomApplication");
            ptm.setMembers(new String[]{"*"});
            
            PackageTypeMembers ptm1 = new PackageTypeMembers();
            ptm1.setName("Profile");
            ptm1.setMembers(new String[]{"*"});
            
            p.setTypes(new PackageTypeMembers[]{ptm,ptm1});
            
            retrieveRequest.setUnpackaged(p);
            
            AsyncResult asyncResult = metadataConnection.retrieve(retrieveRequest);
            asyncResult = waitForRetrieveCompletion(asyncResult,metadataConnection);
            RetrieveResult retrieveResult =  metadataConnection.checkRetrieveStatus(asyncResult.getId());
            
            StringBuilder stringBuilder = new StringBuilder();
            if (retrieveResult.getMessages() != null) {
                for (RetrieveMessage rm : retrieveResult.getMessages()) {
                    stringBuilder.append(rm.getFileName() + " - " + rm.getProblem() + "\n");
                }
            }
            if (stringBuilder.length() > 0) {
                System.out.println("Retrieve warnings:\n" + stringBuilder);
            }
            byte[] bs = retrieveResult.getZipFile();
            return bs;
        }catch (Exception e){
            
        }
        return null;
        
    }
    
    private Map<String,String> unZip(byte[] data, String path) {
        Map<String, String> m = new HashMap();
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ZipInputStream zip = new ZipInputStream(bis);
            ZipEntry entry = null;
            while ((entry = zip.getNextEntry()) != null) {
                if(entry.getName().indexOf(path) == 0){
                    byte[] buf = new byte[1024];
                    int num = -1;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    while ((num = zip.read(buf, 0, buf.length)) != -1) {
                        baos.write(buf, 0, num);
                    }
                    m.put(entry.getName(), baos.toString());
                    baos.flush();
                    baos.close();
                    zip.closeEntry();
                }
            }
            zip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }
    
    private String getMetaDataServerUrl(String token, String instanceUrl){
        if(metadataServerUrl == null){
            Map map = getloginInfo(token, instanceUrl);
            JSONObject m = (JSONObject) map.get("urls");
            metadataServerUrl = m.getString("metadata");
            metadataServerUrl = metadataServerUrl.replace("{version}", VERSION);
        }
        return metadataServerUrl;
    }
    
    private DeployResult waitForDeployCompletion(String asyncResultId, MetadataConnection metadataConnection) throws Exception {
        int poll = 0;
        long waitTimeMilliSecs = 1000;
        DeployResult deployResult;
        boolean fetchDetails;
        do {
            Thread.sleep(waitTimeMilliSecs);
            // double the wait time for the next iteration
            waitTimeMilliSecs *= 2;
            if (poll++ > 50) {
                throw new Exception("Request timed out. If this is a large set of metadata components, " + "ensure that MAX_NUM_POLL_REQUESTS is sufficient.");
            }
            // Fetch in-progress details once for every 3 polls
            fetchDetails = (poll % 3 == 0);
            deployResult = metadataConnection.checkDeployStatus(asyncResultId, fetchDetails);
            System.out.println("Status is: " + deployResult.getStatus());
            if (!deployResult.isDone() && fetchDetails) {
            }
        } while (!deployResult.isDone());
        if (!deployResult.isSuccess() && deployResult.getErrorStatusCode() != null) {
            throw new Exception(deployResult.getErrorStatusCode() + " msg: " + deployResult.getErrorMessage());
        }
        if (!fetchDetails) {
            // Get the final result with details if we didn't do it in the last attempt.
            deployResult = metadataConnection.checkDeployStatus(asyncResultId, true);
        }
        return deployResult;
    }
    
    private AsyncResult waitForRetrieveCompletion(AsyncResult asyncResult,MetadataConnection metadataConnection) throws Exception {
        int poll = 0;
        long waitTimeMilliSecs = 1000;
        while (!asyncResult.isDone()) {
            Thread.sleep(waitTimeMilliSecs);
            // double the wait time for the next iteration
            waitTimeMilliSecs *= 2;
            if (poll++ > 50) {
                throw new Exception(
                    "Request timed out. If this is a large set of metadata components, " +
                    "ensure that MAX_NUM_POLL_REQUESTS is sufficient.");
            }
            asyncResult = metadataConnection.checkStatus(
                new String[]{asyncResult.getId()})[0];
            System.out.println("Status is: " + asyncResult.getState());
        }
        if (asyncResult.getState() != AsyncRequestState.Completed) {
            throw new Exception(asyncResult.getStatusCode() + " msg: " +
                asyncResult.getMessage());
        }
        return asyncResult;
    }
    
    private String generateAppXml(ConnectedApp app){
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<ConnectedApp xmlns=\"http://soap.sforce.com/2006/04/metadata\">\n");
        sb.append("  <fullName>"+app.getFullName()+"</fullName>\n");
        sb.append("  <contactEmail>"+app.getContactEmail()+"</contactEmail>\n");
        sb.append("  <label>"+app.getLabel()+"</label>\n");
        sb.append("  <oauthConfig>\n");
        // FIXME: use canvasUrl instead
        sb.append("    <callbackUrl>"+app.getCanvasConfig().getCanvasUrl()+"</callbackUrl>\n");
        sb.append("    <scopes>Basic</scopes>\n");
        sb.append("    <scopes>Api</scopes>\n");
        sb.append("    <scopes>Web</scopes>\n");
        sb.append("    <scopes>Full</scopes>\n");
        sb.append("    <scopes>Chatter</scopes>\n");
        sb.append("    <scopes>CustomApplications</scopes>\n");
        sb.append("    <scopes>RefreshToken</scopes>\n");
        sb.append("  </oauthConfig>\n");
        sb.append("  <canvasConfig>\n");
        sb.append("    <accessMethod>Post</accessMethod>\n");
        sb.append("    <canvasUrl>"+app.getCanvasConfig().getCanvasUrl()+"</canvasUrl>\n");
        sb.append("    <locations>Chatter</locations>\n");
        sb.append("    <locations>Visualforce</locations>\n");
        sb.append("  </canvasConfig>\n");
        sb.append("</ConnectedApp>");
        return sb.toString();
    }
    
    private void zipFile(ZipOutputStream out, String path, String value){
        try {
            out.putNextEntry(new ZipEntry(path));
            ByteArrayInputStream stream = new ByteArrayInputStream(value.getBytes());
            int i;  
            while ((i = stream.read()) != -1) {  
                out.write(i);  
            } 
            stream.close();
            out.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

}
