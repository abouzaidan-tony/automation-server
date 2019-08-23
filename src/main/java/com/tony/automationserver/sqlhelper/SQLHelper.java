package com.tony.automationserver.sqlhelper;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;

import com.tony.automationserver.settings.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SQLHelper {

    private static SQLHelper instance;

    private SQLHelper(){}

    public enum SQLTypes{
        Integer,
        String,
        Date,
        Time,
        DateTime,
        Boolean,
        JSONObject,
        JSONArray
    }

    public static SQLHelper GetInstance() {
        if(instance == null)
            instance = new SQLHelper();
        return instance;
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException, JSONException {
        Settings s = Settings.getInstance();
        s.init();
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://"+s.host()+":"+s.port()+"/" + s.database() + "?autoReconnect=true&useSSL=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8", s.user(), s.password());
    }

    public Object ExecuteNonQuery(String query, Object[] parameters)
    {
        try {
            Connection connection = getConnection();
            Object id = ExecuteNonQuery(connection, query, parameters);
            connection.close();
            return id;
        }catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }

    public Object ExecuteNonQuery(Connection connection, String query, Object[] parameters)
    {
        try {
            PreparedStatement preparedStmt = preparedStatement(connection, query, parameters);
            preparedStmt.executeUpdate();
            ResultSet generatedKeys = preparedStmt.getGeneratedKeys();
            Object id = 0;
            if(generatedKeys.next())
                id = generatedKeys.getObject(1);
            return id;
        }catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }

    private PreparedStatement preparedStatement(Connection connection, String query, Object[] parameters) throws Exception
    {
        PreparedStatement preparedStmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        
        if(parameters == null)
            return preparedStmt;

        for(int i=0; i<parameters.length; i++)
        {
        	if(parameters[i] instanceof Integer)
                preparedStmt.setInt(i+1, (Integer) parameters[i]);
            else if (parameters[i] instanceof String)
            	preparedStmt.setString(i+1, (String)parameters[i]);
            else if (parameters[i] instanceof java.util.Date)
            	preparedStmt.setTimestamp(i+1, new java.sql.Timestamp(((java.util.Date) parameters[i]).getTime()));
            else if(parameters[i] instanceof Boolean)
                preparedStmt.setBoolean(i+1, (boolean) parameters[i]);
            else if (parameters[i] == null)
            	preparedStmt.setString(i+1, null);
            else
                preparedStmt.setString(i+1, parameters[i].toString());
        }
        return preparedStmt;
    }

    private LinkedList<HashMap<String, Object>> getDataFromResultSet(ResultSet rs, SQLTypes[] resultTypes) throws Exception
    {
    	LinkedList<HashMap<String, Object>> results = new LinkedList<>();
    	final ResultSetMetaData meta = rs.getMetaData();
    	final int columnCount = resultTypes.length;
        
    	while (rs.next())
        {
    		HashMap<String, Object> row = new HashMap<String, Object>();
            for(int i=0; i<columnCount; i++)
            {
            	Object obj = null;
                switch (resultTypes[i]){
                    case Integer:
                    	obj = rs.getInt(i+1);
                        break;
                    case String:
                    	obj = rs.getString(i+1);
                        break;
                    case Date:
                    	obj = rs.getDate(i+1);
                        break;
                    case Time:
                    	obj = rs.getTime(i+1);
                        break;
                    case DateTime:
                        obj = rs.getTimestamp(i+1);
                        break;
                    case Boolean:
                    	obj = rs.getBoolean(i+1);
                        break;
                    case JSONObject:
                    	obj = rs.getString(i+1);
                    	if(obj != null)
                    		obj = new JSONObject((String)obj);
                    	break;
                    case JSONArray:
                    	obj = rs.getString(i+1);
                    	if(obj != null)
                    		obj = new JSONArray((String)obj);
                    	break;
                    default:
                    	obj = rs.getString(i+1);
                }

                if(rs.wasNull())
                	obj = null;
                row.put(meta.getColumnName(i+1), obj);
            }
            results.add(row);
        }
        return results;
    }

    public LinkedList<HashMap<String, Object>> ExecuteQuery(String query, Object[] parameters, SQLTypes[] resultTypes)
    {
    	LinkedList<HashMap<String, Object>> results = null;
        try{
            Connection connection = getConnection();
            results = ExecuteQuery(connection, query, parameters, resultTypes);
            connection.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return results;
    }

    public LinkedList<HashMap<String, Object>> ExecuteQuery(Connection connection, String query, Object[] parameters, SQLTypes[] resultTypes)
    {
    	LinkedList<HashMap<String, Object>> results = new LinkedList<HashMap<String, Object>>();
        try{
            PreparedStatement preparedStmt = preparedStatement(connection, query, parameters);
            ResultSet rs = preparedStmt.executeQuery();
            results = getDataFromResultSet(rs, resultTypes);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return results;
    }

    public int ExecuteScalar(String query, Object[] parameters)
    {
        try{
            Connection connection = getConnection();
            int scalar = ExecuteScalar(connection, query, parameters);
            connection.close();
            return scalar;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return -1;
    }

    public int ExecuteScalar(Connection connection, String query, Object[] parameters)
    {
        try{
            PreparedStatement preparedStmt = preparedStatement(connection, query, parameters);
            ResultSet rs = preparedStmt.executeQuery();
            LinkedList<HashMap<String, Object>> res = getDataFromResultSet(rs, new SQLTypes[] {SQLTypes.Integer});
            HashMap<String, Object> first_elem = res.get(0);
            return (Integer) first_elem.get(first_elem.keySet().toArray()[0]);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return -1;
    }
}