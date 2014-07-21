package com.britesnow.samplesocial.web;

import javax.inject.Singleton;

import org.apache.commons.fileupload.FileItem;

import com.britesnow.snow.util.AnnotationMap;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.resolver.annotation.WebParamResolver;

@Singleton
public class SampleSocialWebParamResolvers {

    
    @WebParamResolver(annotatedWith=WebParam.class)
    public FileItem resolveFileItem(AnnotationMap annotationMap, Class paramType, RequestContext rc) {
        WebParam webParam = annotationMap.get(WebParam.class);
        return rc.getParamAs(webParam.value(), FileItem.class);
    }
    
    @WebParamResolver
    public FileItem[] resolveFileItems(AnnotationMap annotationMap, Class paramType, RequestContext rc){
        WebParam webParam = annotationMap.get(WebParam.class);
        Object valueObject = rc.getParamMap().get(webParam.value());
        if(valueObject == null || valueObject instanceof String){
            return null;
        }else if(valueObject.getClass().isArray()){
            return (FileItem[]) valueObject;
        }else{
            return new FileItem[]{(FileItem) valueObject};
        }
    }

}
