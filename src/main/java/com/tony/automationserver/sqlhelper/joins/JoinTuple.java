package com.tony.automationserver.sqlhelper.joins;

public class JoinTuple {
    

    protected String joinType;
    protected String tableName;
    protected String joinCondition;

    public JoinTuple(String joinType, String tableName, String joinCondition){
        this.joinType = joinType;
        this.tableName = tableName;
        this.joinCondition = joinCondition;
    }
    
    @Override
    public String toString()
    {
		return joinType + " " + tableName + " ON " + joinCondition;
    }
}