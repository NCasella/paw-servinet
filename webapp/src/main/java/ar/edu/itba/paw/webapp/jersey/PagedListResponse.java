package ar.edu.itba.paw.webapp.jersey;

import javax.ws.rs.core.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagedListResponse {

    public static <T> Response generate(List<T> list, int page, int total, UriInfo uriInfo, Class<T> objClass, Request request, Map<String, Object> queryParamsForLink) {

        int prev = page-1 > 0 ? page-1 : page;
        int max =  total % 10 == 0 ? total / 10 : (total / 10) + 1;
        int next = page + 1 <= max ? page + 1 : page;
        Map<String,Integer>pageLinks=new HashMap<>();
        if(max>0){
            pageLinks.put("first",1);
            if(page>1){
                pageLinks.put("prev",prev);
            }
            if(page<max){
                pageLinks.put("next",next);
            }
            pageLinks.put("last",max);
        }

        Response.ResponseBuilder cachedResponse=ConditionalCache.cacheResponseFromHashCode(request, getListGenericEntity(list, objClass),list.hashCode())
                 .link(String.valueOf(total), "total");

        for(Map.Entry<String,Integer> pageLinksEntry:pageLinks.entrySet()) {
            UriBuilder pageLink=uriInfo.getAbsolutePathBuilder().queryParam("page",pageLinksEntry.getValue());
            for (Map.Entry<String, Object> queryParamEntry : queryParamsForLink.entrySet()) {
                if (queryParamEntry.getValue() != null) {
                    pageLink.queryParam(queryParamEntry.getKey(),queryParamEntry.getValue());
                }
            }
            cachedResponse.link(pageLink.build(),pageLinksEntry.getKey());
        }
        return cachedResponse.build();
     }

    private static <T> GenericEntity<List<T>> getListGenericEntity(List<T> list, Class<T> objClass) {
        final ParameterizedType type = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] {objClass};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };

        return new GenericEntity<>(list, type);
    }


}
