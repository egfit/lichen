package lichen.orm.internal;

import lichen.orm.services.EntityOperations;
import lichen.orm.services.EntityService;
import lichen.orm.services.QueryRelation;
import org.springframework.orm.hibernate3.HibernateOperations;

/**
 * hibernated entity service
 * @author jcai
 * @version 0.1
 */
public class HibernateEntityServiceImpl implements EntityService{
    private EntityOperations entityOperations;

    public HibernateEntityServiceImpl(EntityOperations entityOperations){
        this.entityOperations = entityOperations;
    }
    public <T> QueryRelation<T> query(Class<T> clazz) {
        return new QueryRelation<T>(clazz,this.entityOperations);
    }
}
