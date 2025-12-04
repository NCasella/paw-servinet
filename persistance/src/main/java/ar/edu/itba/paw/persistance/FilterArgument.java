package ar.edu.itba.paw.persistance;


import ar.edu.itba.paw.model.Categories;
import ar.edu.itba.paw.model.Neighbourhoods;
import ar.edu.itba.paw.model.ServicesOrderFilters;

import javax.persistence.Query;
import java.util.*;

public class FilterArgument {

    private final Map<FilterTypes, Object> filters = new EnumMap<>(FilterTypes.class);//mapa <columna a filtrar,valor del "?">

    private final Map<ServicesOrderFilters,String> orderQueryMap=Map.of(
            ServicesOrderFilters.RATE_ASC,"group by s.id order by coalesce(round(avg(r.rating), 2), 0) asc, s.id ",
            ServicesOrderFilters.RATE_DESC,"group by s.id order by coalesce(round(avg(r.rating), 2), 0) desc, s.id "
    );

    private final Map<ServicesOrderFilters,String > orderJqlToReturn=Map.of(ServicesOrderFilters.RATE_ASC,"order by s.ratingAvg asc ",
            ServicesOrderFilters.RATE_DESC,"order by s.ratingAvg desc ");

    private  ServicesOrderFilters servicesOrderFilters;
    private int page=-1;
    private int pageSize=10;
    public FilterArgument addCategory(Categories category) {
        if(category!=null){
            return addParameter(FilterTypes.CATEGORY,category.getValue());
        }
        return this;
    }

    public FilterArgument addRating(int rating) {
        return addParameter(FilterTypes.RATING, rating);
    }

    private FilterArgument addParameter(FilterTypes type,String value){
        if(value!=null && !value.isEmpty()){
            filters.put(type,value);
        }
        return this;
    }
    public String getOrderFilterQuery(){
        return servicesOrderFilters != null ? orderJqlToReturn.getOrDefault(servicesOrderFilters,""):"";
    }

    public FilterArgument addHomeServiceFilter(Boolean homeServiceFilter){
        if(homeServiceFilter!=null){
            filters.put(FilterTypes.HOME_SERVICE,homeServiceFilter);
        }
        return this;
    }
    public FilterArgument addOrder(ServicesOrderFilters servicesOrderFilters){
        this.servicesOrderFilters=servicesOrderFilters;
        return this;
    }

    private FilterArgument addParameter(FilterTypes type, int value){
        if(value>0 && value<=5){
            filters.put(type,value);
        }
        return this;
    }

    public FilterArgument addPage(int page){
        this.page = page;
        return this;
    }

    public int getPageSize(){return pageSize;}

    public FilterArgument addLocation(Neighbourhoods[] neighbourhoods) {
        if(neighbourhoods!=null && neighbourhoods.length!=0) {
            List<String> values = Arrays.stream(neighbourhoods)
                    .map(Neighbourhoods::getValue)
                    .toList();
            filters.put(FilterTypes.NEIGHBOURHOOD, List.of(values));
        }
        return this;
    }

    public FilterArgument addBusinessId(Long businessId){
        if(businessId!=null && businessId>0){
            filters.put(FilterTypes.BUSINESS_ID, businessId);
        }
        return this;
    }

    public FilterArgument addSearch(String search) {
        if(search!=null && !search.isEmpty()){
            filters.put(FilterTypes.SERVICE_SEARCH,search.replace("%","\\%").replace("_","\\_"));
        }
        return this;
    }

    public String formSqlSentence(){
        return formSqlSentence("");
    }
    public String formSqlSentence(String subquery) {
        StringBuilder sql = new StringBuilder(subquery);
        sql.append("2=2 ");//(p ^ 1) === p
        for (Map.Entry<FilterTypes, Object> entries : filters.entrySet()) {
                sql.append("and ").append(entries.getKey()).append(" ");
        }
        if(servicesOrderFilters!=null){
            sql.append(orderQueryMap.get(servicesOrderFilters));
        }
        return sql.toString();
    }
    public void setQueryParams(Query query){
        for (Map.Entry<FilterTypes, Object> entries : filters.entrySet()) {
            query.setParameter(entries.getKey().param,entries.getValue());
        }
    }

        private enum FilterTypes {
            CATEGORY("category = :cat ","cat"),
            NEIGHBOURHOOD("s.id in (select serviceid from nbservices where neighbourhood in :loc ) ","loc"),
            RATING("s.id IN (SELECT serviceid FROM ratings GROUP BY serviceid HAVING AVG(rating) >= :rate)","rate"),
            SERVICE_SEARCH("( lower(servicename) like concat('%',lower( :search ),'%') or lower(servicedescription) like concat('%',lower( :search ),'%') )","search"),
            HOME_SERVICE("homeservice = :homeserv ","homeserv"),
            BUSINESS_ID("s.businessid = :businessId ","businessId");

            private final String value;
            private final String param;//valores a ser filtrados/buscados en SQL

            FilterTypes(String value,String param) {
                this.value = value;
                this.param=param;
            }

            @Override
            public String toString() {
                return value;
            }

        }
    }
