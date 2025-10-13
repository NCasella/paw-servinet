package ar.edu.itba.paw.webapp.jersey;

import ar.edu.itba.paw.model.PagedList;
import ar.edu.itba.paw.webapp.dto.UserDto;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;

public class PagedListResponse {

    public static <T> Response generate(List<T> list, int page, int total, UriInfo uriInfo, Class<T> objClass) {

        int prev = page-1 > 0 ? page-1 : page;
        int max =  total % 10 == 0 ? total / 10 : (total / 10) + 1;
        int next = page + 1 <= max ? page + 1 : page;
        return Response.ok( getListGenericEntity(list, objClass) )
                 .link(String.valueOf(total), "total")
                 .link(uriInfo.getAbsolutePathBuilder().queryParam("page", prev).build(),"prev")
                 .link(uriInfo.getAbsolutePathBuilder().queryParam("page", next).build(),"next")
                 .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(),"first")
                 .link(uriInfo.getAbsolutePathBuilder().queryParam("page", max).build(),"last")
                 .build();
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

    public static <T> Response generate(PagedList<T> pagedList,  int page, UriInfo uriInfo, Class<T> objClass) {
         return generate(pagedList.getList(), page, pagedList.getTotalElements(), uriInfo, objClass);
     }
}
