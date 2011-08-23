package lichen.orm.internal;

import lichen.orm.entity.UserEntity;
import lichen.orm.services.BaseDbTestCase;
import lichen.orm.services.EntityOperations;
import lichen.orm.services.EntityService;
import lichen.orm.services.QueryRelation;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateOperations;

/**
 * test hibernate entity service
 * @author jcai
 * @version 0.1
 */
public class HibernateEntityServiceImplTest extends BaseDbTestCase {

    @Test
    public void test_count(){
        EntityService entityService = getService(EntityService.class);
        HibernateOperations entityOperations = getService(HibernateOperations.class);
        UserEntity ue= (UserEntity) entityOperations.get(UserEntity.class,1);

        QueryRelation qr = entityService.query(UserEntity.class);
        Assert.assertEquals(1,qr.where("id=?",1).count());
        qr = entityService.query(UserEntity.class);
        Assert.assertEquals(1,qr.where("name=?","jcai").count());
        qr = entityService.query(UserEntity.class);
        Assert.assertEquals(1,qr.where("name=? and age>?","jcai",20).count());
        qr = entityService.query(UserEntity.class);
        Assert.assertEquals(0,qr.where("name=? and age>?","jcai",40).count());
        qr = entityService.query(UserEntity.class);
        Assert.assertEquals(1,qr.where("name=?","jcai").where("age>?",20).count());
    }
}
