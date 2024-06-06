package ar.edu.itba.paw.persistance;


import javax.persistence.Query;
import java.util.*;

public class FilterArgument {

    private final Map<FilterTypes, Object> filters = new EnumMap<>(FilterTypes.class);//mapa <columna a filtrar,valor del "?">
    private int page=-1;
    private int pageSize=10;
    public FilterArgument addCategory(String category) {
        return addParameter(FilterTypes.CATEGORY,category);

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

    public FilterArgument addLocation(String[] location) {
        if(location!=null && location.length!=0)
            filters.put(FilterTypes.LOCATION,Arrays.asList(location));
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
        return sql.toString();
    }
    public void setQueryParams(Query query){
        for (Map.Entry<FilterTypes, Object> entries : filters.entrySet()) {
            query.setParameter(entries.getKey().param,entries.getValue());
        }
    }

    public List<Object> getValues() {
        List<Object> values= new ArrayList<>(filters.values());
        if(page!=-1){
            values.add(page*10);
        }
        return values;
    }

        private enum FilterTypes {
            CATEGORY("category = :cat ","cat"),
            LOCATION("s.id in (select serviceid from nbservices where neighbourhood in :loc ) ","loc"),
            RATING("s.id IN (SELECT serviceid FROM ratings GROUP BY serviceid HAVING AVG(rating) >= :rate)","rate"),
            SERVICE_SEARCH("( lower(servicename) like concat('%',lower( :search ),'%') or lower(servicedescription) like concat('%',lower( :search ),'%') )","search");

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
