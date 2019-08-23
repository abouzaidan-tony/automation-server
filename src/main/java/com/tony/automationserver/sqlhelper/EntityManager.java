package com.tony.automationserver.sqlhelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import com.tony.automationserver.sqlhelper.SchemaHelper.PropertyMap;

public class EntityManager{
    private SQLHelper helper;

    public static class QueueEntry {
        public final SQLObject object;
        public final Consumer<SQLObject> operation;

        public QueueEntry(SQLObject object, Consumer<SQLObject> operation) {
            this.object = object;
            this.operation = operation;
        }
    }

    private static Queue<QueueEntry> queue;
    private static EntityManager instance;

    static {
        queue = new LinkedList<>();
    }

    public EntityManager() {
        helper = SQLHelper.GetInstance();
    }

    public <C extends SQLObject> Repository<C> GetRepository(Class<C> clazz) {
        return Repository.GetRepository(clazz);
    }

    public static EntityManager GetInstance()
    {
        if(instance == null)
            instance = new EntityManager();
        return instance;
    }

    private String getTableName(Class<? extends SQLObject> clazz) {
        return SchemaHelper.getTableName(clazz);
    }

    private String getUpdateQuery(Class<? extends SQLObject> clazz) {
        return "UPDATE " + getTableName(clazz) + " SET @vals WHERE @cond";
    }

    private String getDeleteQuery(Class<? extends SQLObject> clazz) {
        return "DELETE FROM " + getTableName(clazz) + " WHERE @cond";
    }

    private final String getInsertQuery(Class<? extends SQLObject> clazz) {
        return "INSERT INTO " + getTableName(clazz) + "  (@columnsName) VALUES (@vals)";
    }

    public void Insert(SQLObject object){
        queue.add(new QueueEntry(object, s -> insert(s)));
    }

    public void Update(SQLObject object) {
        queue.add(new QueueEntry(object, s -> update(s)));
    }

    public void Delete(SQLObject object) {
        queue.add(new QueueEntry(object, s -> delete(s)));
    }

    public synchronized void flush(){
        for (QueueEntry entry : queue) {
            entry.operation.accept(entry.object);
        }
        queue.clear();
    }

    private void insert(SQLObject object){
        object.PreInsert();
        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();

        Class<? extends SQLObject> clazz = object.getClass();
        boolean first = true;

        List<PropertyMap> properties = SchemaHelper.getColumns(clazz);

        for (PropertyMap prop : properties) {

            if (first)
                first = false;
            else {
                builder1.append(',');
                builder2.append(',');
            }

            builder1.append(prop.columnName);
            builder2.append('?');
        }

        String query = getInsertQuery(clazz).replace("@columnsName", builder1.toString()).replace("@vals",
                builder2.toString());

        Object key = helper.ExecuteNonQuery(query, object.getPropertyArray(false, false));

        try {
            object.setPropertyValue(SchemaHelper.getPrimaryKey(clazz).fieldName, key);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        object.PostInsert();
    }

    private void update(SQLObject object){
        object.PreUpdate();
       
        StringBuilder builder = new StringBuilder();

        boolean first = true;

        Class<? extends SQLObject> clazz = object.getClass();

        List<PropertyMap> properties = SchemaHelper.getColumns(clazz);

        for (PropertyMap prop : properties) {

            if(prop.isPrimary)
                continue;

            if (first)
                first = false;
            else 
                builder.append(", ");

            builder.append(prop.columnName);
            builder.append("= ?");
        }

        PropertyMap key = SchemaHelper.getPrimaryKey(clazz);

        String query = getUpdateQuery(clazz).replace("@vals", builder.toString())
                        .replace("@cond", key.columnName + "= ?");

        helper.ExecuteNonQuery(query, object.getPropertyArray(true, false));
    
        object.PostUpdate();
    }

    private void delete(SQLObject object) {
        object.PreDelete();
        Class<? extends SQLObject> clazz = object.getClass();
        PropertyMap key = SchemaHelper.getPrimaryKey(clazz);
        String query = getDeleteQuery(clazz).replace("@cond", key.columnName + "= ?");
        Object id = object.getPropertyValue(key.fieldName);
        Object[] params = new Object[] { id };
        helper.ExecuteNonQuery(query, params);
        try {
            object.setPropertyValue(SchemaHelper.getPrimaryKey(clazz).fieldName, null);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            try { object.setPropertyValue(SchemaHelper.getPrimaryKey(clazz).fieldName, 0);}
            catch (Exception ex) {}
        }
        
        object.PostDelete();
    }
}