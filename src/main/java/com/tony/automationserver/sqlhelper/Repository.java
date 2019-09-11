package com.tony.automationserver.sqlhelper;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.tony.automationserver.sqlhelper.SQLHelper;
import com.tony.automationserver.sqlhelper.SQLObject;
import com.tony.automationserver.sqlhelper.SchemaHelper;
import com.tony.automationserver.sqlhelper.SchemaHelper.PropertyMap;
import com.tony.automationserver.sqlhelper.filter.FilterTuple;
import com.tony.automationserver.sqlhelper.joins.JoinTuple;

public class Repository<T extends SQLObject> {

    private static HashMap<Class<? extends SQLObject>, HashMap<Object, SQLObject>> cachedObjects;

    static {
        cachedObjects = new HashMap<>();
    }

    private LinkedList<FilterTuple> filters;
    private LinkedList<JoinTuple> joins;
    private int limit;
    private int offset;
    private Class<T> clazz;

    private Repository(Class<T> clazz){
        this.clazz = clazz;
        this.limit = 0;
        this.offset = 0;
        this.filters = new LinkedList<>();
        this.joins = new LinkedList<>();
    }

    static <C extends SQLObject> Repository<C> GetRepository(Class<C> clazz) {
        Repository<C> instance = new Repository<C>(clazz);
        if (cachedObjects.get(clazz) == null)
            cachedObjects.put(clazz, new HashMap<>());
        return instance;
    }

    private String getFetchQuery() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");

        List<PropertyMap> properties = SchemaHelper.getCombinedColumns(clazz);

        for (PropertyMap prop : properties) {
            builder.append(prop.columnName);
            builder.append(',');
            builder.append(' ');
        }
        builder.append("1 ");

        builder.append("FROM ");
        builder.append(SchemaHelper.getTableName(clazz));

        for(JoinTuple var : joins){
            builder.append(' ');
            builder.append(var.toString());
            builder.append(' ');
        }

        if (filters.size() != 0) {
            builder.append(" WHERE ");
            for (FilterTuple var : filters) {
                builder.append(var.toString());
                builder.append(" AND ");
            }

            builder.append("1=1");
        }

        if (limit != 0) {
            builder.append(" limit ");
            builder.append(limit);
        }

        if (offset != 0) {
            builder.append(" offset ");
            builder.append(offset);
        }

        String f_query = builder.toString();
        return f_query;
    }

    public Repository<T> Join(JoinTuple joinTuple)
    {
        joins.add(joinTuple);
        return this;
    }

    public LinkedList<T> findAll() {
        filters.clear();
        return Fetch();
    }

    public LinkedList<T> findAll(int limit) {
        filters.clear();
        this.limit = limit;
        LinkedList<T> temp = Fetch();
        this.limit = 0;
        return temp;
    }

    public LinkedList<T> findAll(int limit, int offset) {
        filters.clear();
        this.limit = limit;
        this.offset = offset;
        LinkedList<T> temp = Fetch();
        this.limit = 0;
        this.offset = 0;
        return temp;
    }

    public T findOneBy(List<FilterTuple> filters) {
        LinkedList<T> list = findBy(filters);
        return list.size() == 0 ? null : list.getFirst();
    }

    public T findOneBy(FilterTuple... filters) {
        LinkedList<T> list = findBy(Arrays.asList(filters));
        return list.size() == 0 ? null : list.getFirst();
    }

    @SuppressWarnings("unchecked")
    public T find(Object primaryKeyValue) {
        T temp = (T) cachedObjects.get(this.clazz).get((Object)primaryKeyValue);
        if(temp != null)
            return temp;
        PropertyMap key = SchemaHelper.getPrimaryKey(clazz);
        LinkedList<T> list = findBy(Arrays.asList(new FilterTuple(key.columnName, primaryKeyValue)));
        return list.size() == 0 ? null : list.getFirst();
    }

    public LinkedList<T> findBy(List<FilterTuple> filters) {
        this.filters.clear();
        this.filters.addAll(filters);
        return Fetch();
    }

    @SuppressWarnings("unchecked")
    public final LinkedList<T> Fetch() {
        LinkedList<HashMap<String, Object>> results = null;
        LinkedList<T> objs = new LinkedList<>();
        int length = filters.size();
        LinkedList<Object> filterParams = new LinkedList<>();

        for (int i = 0; i < length; i++)
            filterParams.add(filters.get(i).Value());
        Object[] filterParamsArray = filterParams.toArray();
        try {
            results = SQLHelper.GetInstance().ExecuteQuery(getFetchQuery(), filterParamsArray,
                    SchemaHelper.getCombinedColumnsType(clazz));

            for (HashMap<String, Object> entry : results) {
                Constructor<? extends SQLObject> constructor = clazz.getConstructor(HashMap.class);
                SQLObject tmp = constructor.newInstance(entry);
                T instance = (T) (tmp);
                cachedObjects.get(clazz).put(instance.getKeyValue(), instance);
                objs.add(instance);
                fillOneToManyReferences(instance);
                fillManyToOneReferences(instance);
                fillManyToManyReferences(instance);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return objs;
    }

    private void fillOneToManyReferences(T obj) {
        List<PropertyMap> properties = SchemaHelper.getOneToManyColumns(clazz);
        for (PropertyMap p : properties) {
            try {
                obj.setPropertyValue(p.fieldName,
                        Repository.GetRepository(p.clazz).findBy(Arrays.asList(new FilterTuple(p.columnName,
                                obj.getPropertyValue(SchemaHelper.getPrimaryKey(obj.getClass()).fieldName)))));
            } catch (IllegalArgumentException | IllegalAccessException e) {
            }
        }
    }

    private void fillManyToOneReferences(T obj) {
        List<PropertyMap> properties = SchemaHelper.getManyToOneColumns(clazz);
        for (PropertyMap p : properties) {
            try {
                obj.setPropertyValue(p.fieldName.substring(11), Repository.GetRepository(p.clazz).find(obj.getMapField(p.columnName)));
            } catch (IllegalArgumentException | IllegalAccessException e) {
            }
        }
    }

    private void fillManyToManyReferences(T obj) {
        List<PropertyMap> properties = SchemaHelper.getManyToManyColumns(clazz);
        String myPrimaryKey = SchemaHelper.getPrimaryKey(obj.getClass()).columnName;
        String myTableName = SchemaHelper.getTableName(obj.getClass());
        for (PropertyMap p : properties) {
            try {
                String targetPrimaryKey = SchemaHelper.getPrimaryKey(p.clazz).columnName;
                String tableName = SchemaHelper.getTableName(p.clazz);
                LinkedList<? extends SQLObject> l = Repository.GetRepository(p.clazz).Join(new JoinTuple("INNER JOIN", p.tableName, tableName+"."+targetPrimaryKey +" = " + myTableName + "." + myPrimaryKey)).findBy(Arrays.asList(new FilterTuple(p.columnName, obj.getKeyValue())));
                obj.setPropertyValue(p.fieldName, l);
            } catch (IllegalArgumentException | IllegalAccessException e) {
            }
        }
    }
}