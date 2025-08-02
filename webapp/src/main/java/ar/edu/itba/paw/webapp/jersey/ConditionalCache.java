package ar.edu.itba.paw.webapp.jersey;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

public class ConditionalCache {

    public static <T> Response.ResponseBuilder cacheResponse(Request request,T dto){
        EntityTag eTag=new EntityTag(Integer.toString(dto.hashCode()));
        CacheControl cc=new CacheControl();
        cc.setMaxAge(60);
        cc.setNoCache(true);
        Response.ResponseBuilder builder=request.evaluatePreconditions(eTag);
        if(builder==null){
            return Response.ok(dto).tag(eTag).cacheControl(cc);
        }
        return Response.notModified(eTag);
    }
}
