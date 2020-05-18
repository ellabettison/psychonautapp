package com.example.psychapp.api;

public class QueryBuilder {
    
    StringBuilder queryHead = new StringBuilder("{\"operationName\":null,\"variables\":{},\"query\":\"{substances");
    StringBuilder queryTail = new StringBuilder("}\"}");
    
    public QueryBuilder queryByName(String substance){
        queryHead.append("(query: \\\"").append(substance).append("\\\")");
        return this;
    }
    
    public QueryBuilder queryByClass(String className){
        queryHead.append("(psychoactiveClass: \\\"").append(className).append("\\\"," +
                "limit: 200)");
        return this;
    }
    
    public QueryBuilder withName(){
        queryHead.append("{name");
        queryTail.insert(0,"}");
        return this;
    }
    
    public QueryBuilder withEffects(){
        queryHead.append(" effects { name} ");
        return this;
    }
    
    public QueryBuilder withRoas(){
        queryHead.append(" roas { name} ");
        return this;
    }
    
    public String getQuery(){
        String query = queryHead.append(queryTail).toString();
        return query;
    }
    
}
