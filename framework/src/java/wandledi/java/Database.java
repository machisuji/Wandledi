package wandledi.java;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.RollbackException;

/**A class for dealing with JPA-based persistence.
 * It only supports one global persistence unit,
 * which has to be set before use (Bootstrap).
 *
 * @TODO It's probably not such a good idea *not* to throw Exceptions if something goes wrong.
 * @IDEA Don't use checked exceptions, though. Maybe unchecked exceptions which will be caught
 *       by the Switchboard. Provide the user with a unified way to deal with unexpected exceptions.
 *
 * @author Markus Kahl
 */
public class Database {

    private static String persistenceUnit;
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private EntityTransaction entityTransaction;

    public Database() {

        this(false);
    }

    public Database(boolean createEntityManagerLater) {

        if (!createEntityManagerLater) {
            this.entityManager = getEntityManagerFactory().createEntityManager();
        }
    }

    public EntityTransaction getTransaction() {

        if (entityTransaction == null) {
            entityTransaction = getEntityManager().getTransaction();
        }
        return entityTransaction;
    }

    public void beginTransaction() {

        getTransaction().begin();
    }

    /**Returns this Database object with an active transaction.
     * This method's sole purpose is to cut lines of code.
     *
     * @return This Database instance with an active transaction.
     */
    public Database withTransaction() {

        beginTransaction();
        return this;
    }

    /**Commits the current transaction.
     *
     * @return True if it worked.
     */
    public boolean commitTransaction() {

        if (entityTransaction != null) {
            try {
                entityTransaction.commit();
                return true;
            } catch (RollbackException e) {
                java.util.logging.Logger.getLogger(getClass().getName()).log(
                    java.util.logging.Level.SEVERE, "Transaction failed: ", e);
            }
        }
        return false;
    }

    public void rollbackTransaction() {

        if (entityTransaction != null) {
            entityTransaction.rollback();
        }
    }

    public boolean isTransaction() {

        return entityTransaction != null ? entityTransaction.isActive() : false;
    }

    /**You should really call this if you're not using this instance anymore.
     */
    public void close() {

        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }

    /**Find the entity with the given primary key.
     *
     * @param entityClass Entity class
     * @param primaryKey Primary key
     * @param <T> Entity type
     *
     * @return The entity or null if none was found.
     */
    public <T> T find(Class<T> entityClass, Object primaryKey) {

        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, primaryKey);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(
                    java.util.logging.Level.SEVERE, "Query failed: ", e);
        }
        return null;
    }

    /**Refreshes the state of the given instance from the database.
     * Any uncommitted, local changes will be overwritten.
     *
     * @param entity
     * @return True if the refreshing worked.
     */
    public boolean refresh(Object entity) {

        EntityManager em = getEntityManager();
        try {
            em.refresh(entity);
            return true;
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(
                    java.util.logging.Level.SEVERE, "Refresh failed: ", e);
        }
        return false;
    }

    /**Executes the given update or delete query with the respective parameters.
     *
     * @param queryString Query to be performed.
     * @param params Parameters in order of occurrence within the query.
     *
     * @return Number of affected records or -1 if there was an error.
     */
    public int update(String queryString, Object... params) {

        int ret = 0;
        int add = 0;
        EntityManager em = getEntityManager();
        Query query = em.createQuery(queryString);

        for (int i = 0; i < params.length + add; ++i) {
            Class clazz = params[i].getClass();
            if (clazz.isArray()) {
                Object[] array = (Object[]) params[i];
                for (int j = 0; j < array.length; ++j) {
                    query.setParameter(i + j + 1, array[j]);
                }
                i += array.length - 1;
                add += array.length - 1;
            } else {
                query.setParameter(i + 1, params[i]);
            }
        }
        try {
            if (entityTransaction == null) em.getTransaction().begin();
            ret = query.executeUpdate();
            if (entityTransaction == null) em.getTransaction().commit();

            return ret;
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(
                    java.util.logging.Level.SEVERE, "Update failed: ", e);
        }
        return -1;
    }

    /**Executes a query which is supposed to have a single result.
     *
     * @param entityClass Entity class. You can also give Long.class and the like here for COUNT queries and such.
     * @param queryString Query to be performed.
     * @param params Parameters in order of occurrence within the query.
     * @param <T> Entity type
     *
     * @return The result which is of the specified entityClass or null if there is no result.
     */
    public <T> T querySingle(Class<T> entityClass, String queryString, Object... params) {
        
        return entityClass.cast(querySingle(queryString, params));
    }

    /**Executes a query which is supposed to have a single result.
     *
     * @param queryString Query to be executed. Only indexed parameters are allowed (starting at 1).
     * @param params Parameters in order of occurrence within the query.
     *
     * @return The result (entity or number in case of COUNT etc.) or null if there is none.
     */
    public Object querySingle(String queryString, Object... params) {

        EntityManager em = getEntityManager();
        Query query = em.createQuery(queryString);
        setParameters(params, query);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(
                    java.util.logging.Level.SEVERE, "Query failed: ", e);
        }
        return null;
    }

    /**Executes the given query with the respective parameters.
     *
     * @param entityClass Entity class. You can also give Long.class and the like here for COUNT queries and such.
     * @param queryString Query to be executed. Only indexed parameters are allowed (starting at 1).
     * @param params Parameters in order of occurrence within the query.
     * @param <T> Entity type
     *
     * @return The result list containing *all* results, which also can be none, or null if there was an error.
     */
    public <T> List<T> query(Class<T> entityClass, String queryString, Object... params) {

        return query(entityClass, queryString, -1, -1, params);
    }

    /**Convenience variant of the original method which returns a plain List.
     * <p>
     *   // You have to cast normally.
     *   List<String> strings = (List<String>) db.query("SELECT u.name FROM User", 0, 10)
     *   // This way you only have to provide the class of entries.
     *   List<String> strings = db.query(String.class, "SELECT u.name FROM User", 0, 10)
     * </p>
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> query(Class<T> entityClass, String queryString, int start, int rows, Object... params) {

        return query(queryString, start, rows, params);
    }

    /**Executes the given query with the respective parameters.
     *
     * @param queryString Query to be executed. Only indexed parameters are allowed (starting at 1).
     * @param start Start index of the result set.
     * @param rows Max number of results.
     * @param params Parameters in order of occurrence within the query.
     *
     * @return The result list, which can be empty, or null if there was an error.
     */
    public List query(String queryString, int start, int rows,
            Object... params) {

        EntityManager em = getEntityManager();
        Query query = em.createQuery(queryString);
        if (start != -1) {
            query.setFirstResult(start);
        }
        if (rows != -1) {
            query.setMaxResults(rows);
        }
        setParameters(params, query);
        try {
            return query.getResultList();
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(
                    java.util.logging.Level.SEVERE, "Update failed: ", e);
        }
        return null;
    }

    private void setParameters(Object[] param, Query query) {
        
        int add = 0;
        for (int i = 0; i < param.length + add; ++i) {
            Class clazz = param[i].getClass();
            if (clazz.isArray()) {
                Object[] array = (Object[]) param[i];
                for (int j = 0; j < array.length; ++j) {
                    query.setParameter(i + j + 1, array[j]);
                }
                i += array.length - 1;
                add += array.length - 1;
            } else {
                query.setParameter(i + 1, param[i]);
            }
        }
    }

    /**Selects all entities of the given class.
     *
     * @param entityClass Entity class
     * @param <T> Entity type
     * @return The result list, which can be empty, or null if there was an error.
     */
    public <T> List<T> findAll(Class<T> entityClass) {

        return query(entityClass, "SELECT o FROM " + getEntityName(entityClass) + " o", 0, -1);
    }

    public static String getEntityName(Class<?> entityClass) {

        Entity type = entityClass.getAnnotation(Entity.class);
        String name = type.name();
        return "".equals(name) ? entityClass.getSimpleName() : name;
    }

    public <T> T merge(T entity) {

        return (T) mergeObject(entity);
    }

    public <T> T merge(T entity, boolean commit) {

        return (T) mergeObject(entity, commit);
    }

    public boolean persist(Object object) {

        EntityManager em = getEntityManager();
        try {
            if (entityTransaction == null) em.getTransaction().begin();
            em.persist(object);
            if (entityTransaction == null) em.getTransaction().commit();

            return true;
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(
                    java.util.logging.Level.SEVERE, "Coult not persist: ", e);
        }
        return false;
    }

    public boolean remove(Object object) {

        EntityManager em = getEntityManager();
        try {
            if (entityTransaction == null) em.getTransaction().begin();
            em.remove(object);
            if (entityTransaction == null) em.getTransaction().commit();

            return true;
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(
                    java.util.logging.Level.SEVERE, "Coult not remove: ", e);
        }
        return false;
    }

    public <T> boolean remove(Class<T> entityClass, Object primaryKey) {

        EntityManager em = getEntityManager();
        int ret = -1;
        try {
            if (entityTransaction == null) em.getTransaction().begin();
            Query query = em.createQuery("DELETE FROM " + getEntityName(entityClass)
                    + " AS o WHERE o.id = :id");
            query.setParameter("id", primaryKey);
            ret = query.executeUpdate();
            if (entityTransaction == null) em.getTransaction().commit();
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(
                    java.util.logging.Level.SEVERE, "Query failed: ", e);
        }
        return ret == 1;
    }

    protected Object mergeObject(Object object) {

        return mergeObject(object, true);
    }

    protected Object mergeObject(Object object, boolean commit) {

        Object ret;
        EntityManager em = getEntityManager();
        try {
            if (commit && entityTransaction == null) em.getTransaction().begin();
            ret = em.merge(object);
            if (commit && entityTransaction == null) em.getTransaction().commit();

            return ret;
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(
                    java.util.logging.Level.SEVERE, "Coult not merge: ", e);
        }
        return null;
    }

    public EntityManager getEntityManager() {

        if (entityManager == null) {
            entityManager = getEntityManagerFactory().createEntityManager();
        }
        return entityManager;
    }

    /**
     * @return the entityManagerFactory
     */
    public static EntityManagerFactory getEntityManagerFactory() {

        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory(getPersistenceUnit());
        }
        return entityManagerFactory;
    }

    /**Returns the name of the persistence unit which is used by all Database instances.
     *
     * @return Name of the persistence unit
     */
    public static String getPersistenceUnit() {
        return persistenceUnit;
    }

    /**Sets the persistence unit to be used. Has to be set before the Database can be used.
     *
     * @param aPersistenceUnit name of the persistence unit to use
     */
    public static void setPersistenceUnit(String aPersistenceUnit) {
        persistenceUnit = aPersistenceUnit;
    }
}
