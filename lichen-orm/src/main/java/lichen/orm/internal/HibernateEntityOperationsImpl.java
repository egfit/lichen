package lichen.orm.internal;

import lichen.orm.services.EntityOperations;
import org.apache.tapestry5.func.Flow;
import org.springframework.orm.hibernate3.HibernateOperations;

/**
 * Hibernate Entity Operations
 * @author jcai
 * @version 0.1
 */
public class HibernateEntityOperationsImpl implements EntityOperations{
    private HibernateOperations hibernateOperations;

    public HibernateEntityOperationsImpl(HibernateOperations hibernateOperations){
        this.hibernateOperations = hibernateOperations;
    }
    public int count(Class<?> entityClass, String condition, Flow<Object> parameters) {
        return ((Long)hibernateOperations.find(
                "select count(*) from "+entityClass.getName() +" "+condition,
                parameters.toArray(Object.class)).get(0)).intValue();
    }
}
