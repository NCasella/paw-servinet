package ar.edu.itba.paw.webapp.jersey;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

public class ConditionalCache {

    private ConditionalCache(){}

    public static <T> Response.ResponseBuilder cacheResponse(Request request,T dto){
        return cacheResponseFromHashCode(request,dto,dto.hashCode());
    }

    public static <T> Response.ResponseBuilder cacheResponseFromHashCode(Request request, T dto, int hashCode){
        EntityTag eTag=new EntityTag(Integer.toString(hashCode));
        CacheControl cc=generateCacheControl();
        Response.ResponseBuilder builder=request.evaluatePreconditions(eTag);
        if(builder==null){
            return Response.ok(dto).tag(eTag).cacheControl(cc);
        }
        return Response.notModified(eTag);
    }

    public static CacheControl generateCacheControl(){
        CacheControl cc= new CacheControl();
        cc.setMaxAge(60*5);
        cc.setNoCache(true);
        return cc;
    }
}
