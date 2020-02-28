package com.tony.automationserver.sqlhelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.tony.automationserver.sqlhelper.SQLObject;
import com.tony.automationserver.sqlhelper.SchemaHelper.PropertyMap;
import com.tony.automationserver.sqlhelper.annotation.ManyToMany;
import com.tony.automationserver.sqlhelper.annotation.ManyToOne;
import com.tony.automationserver.sqlhelper.annotation.OneToMany;
import com.tony.automationserver.sqlhelper.annotation.OneToOne;
import com.tony.automationserver.sqlhelper.filter.FilterTuple;
import com.tony.automationserver.sqlhelper.joins.JoinTuple;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

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

    private Repository(Class<T> clazz) {
        this.clazz = clazz;
        this.limit = 0;
        this.offset = 0;
        this.filters = new LinkedList<>();
        this.joins = new LinkedList<>();
    }

    public static HashMap<Class<? extends SQLObject>, HashMap<Object, SQLObject>> getCaches() {
        return cachedObjects;
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
        String tableName = SchemaHelper.getTableName(clazz);

        List<PropertyMap> properties = SchemaHelper.getCombinedColumns(clazz);

        for (PropertyMap prop : properties) {
            builder.append(tableName);
            builder.append('.');
            builder.append(prop.columnName);
            builder.append(',');
            builder.append(' ');
        }
        builder.append("1 ");

        builder.append("FROM ");
        builder.append(tableName);

        for (JoinTuple var : joins) {
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

    public Repository<T> Join(JoinTuple joinTuple) {
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
        limit = 1;
        LinkedList<T> list = findBy(filters);
        return list.size() == 0 ? null : list.getFirst();
    }

    public T findOneBy(FilterTuple... filters) {
        return findOneBy(Arrays.asList(filters));
    }

    @SuppressWarnings("unchecked")
    public T find(Object primaryKeyValue) {
        T temp = (T) cachedObjects.get(this.clazz).get((Object) primaryKeyValue);
        if (temp != null)
            return temp;
        PropertyMap key = SchemaHelper.getPrimaryKey(clazz);
        return findOneBy(Arrays.asList(new FilterTuple(key.columnName, primaryKeyValue)));
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
                    SchemaHelper.getColumnsType(clazz));

            for (HashMap<String, Object> entry : results) {
                ProxyFactory factory = new ProxyFactory();
                factory.setSuperclass(clazz);
                MethodHandler handler = new MethodHandler() {
                    @Override
                    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args)
                            throws Throwable {

                        if (!thisMethod.getName().startsWith("get")) {
                            return thisMethod.invoke(self, args);
                        }

                        Object ret = proceed.invoke(self, args);
                        if (ret != null)
                            return ret;

                        Field f = SchemaHelper.getRelatedField(thisMethod, clazz);

                        if (f == null) {
                            return ret;
                        }

                        Annotation[] annotations = f.getAnnotations();
                        for (Annotation annotation : annotations) {
                            if (annotation instanceof ManyToMany) {
                                fillManyToManyReferences((T) self, SchemaHelper.getManyToManyProperty(clazz, f));
                            } else if (annotation instanceof ManyToOne) {
                                fillManyToOneReferences((T) self, SchemaHelper.getManyToOneProperty(clazz, f));
                            } else if (annotation instanceof OneToMany) {
                                fillOneToManyReferences((T) self, SchemaHelper.getOneToManyProperty(clazz, f));
                            } else if (annotation instanceof OneToOne) {
                                fillOneToOneReferences((T) self, SchemaHelper.getOneToOneProperty(clazz, f));
                            }
                        }
                        return proceed.invoke(self, args);
                    }

                };

                T instance = (T) factory.create(new Class<?>[] { HashMap.class }, new Object[] { entry }, handler);
                cachedObjects.get(clazz).put(instance.getKeyValue(), instance);
                objs.add(instance);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return objs;
    }

    private void fillOneToManyReferences(T obj, PropertyMap p) {
        try {
            LinkedList<? extends SQLObject> list = Repository.GetRepository(p.clazz)
                    .findBy(Arrays.asList(new FilterTuple(p.columnName, obj.getKeyValue())));
            obj.setPropertyValue(p.field, list);
        } catch (IllegalArgumentException | IllegalAccessException e) {
        }
    }

    private void fillManyToOneReferences(T obj, PropertyMap p) {
        try {
            obj.setPropertyValue(p.field, Repository.GetRepository(p.clazz).find(obj.getMapField(p.columnName)));
        } catch (IllegalArgumentException | IllegalAccessException e) {
        }
    }

    private void fillOneToOneReferences(T obj, PropertyMap p) {
        try {
            Object res = null;
            if (!p.columnName.equals("")) {
                res = Repository.GetRepository(p.clazz).find(obj.getMapField(p.columnName));
            } else {
                res = Repository.GetRepository(p.clazz)
                        .findOneBy(Arrays.asList(new FilterTuple(p.inversedColumnName, obj.getKeyValue())));
            }
            obj.setPropertyValue(p.field, res);
        } catch (IllegalArgumentException | IllegalAccessException e) {
        }
    }

    private void fillManyToManyReferences(T obj, PropertyMap p) {
        try {
            String targetPrimaryKey = SchemaHelper.getPrimaryKey(p.clazz).columnName;
            String tableName = SchemaHelper.getTableName(p.clazz);
            LinkedList<? extends SQLObject> l = Repository.GetRepository(p.clazz)
                    .Join(new JoinTuple("INNER JOIN", p.tableName,
                            tableName + "." + targetPrimaryKey + " = " + p.tableName + "." + p.inversedColumnName))
                    .findBy(Arrays.asList(new FilterTuple(p.columnName, obj.getKeyValue())));
            obj.setPropertyValue(p.field, l);
        } catch (IllegalArgumentException | IllegalAccessException e) {
        }
    }
}