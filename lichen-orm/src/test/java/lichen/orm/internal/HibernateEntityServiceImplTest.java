package lichen.orm.internal;

import lichen.orm.entity.UserEntity;
import lichen.orm.services.BaseDbTestCase;
import lichen.orm.services.EntityService;
import lichen.orm.services.QueryRelation;
import org.junit.Assert;
import org.junit.Test;

/**
 * test hibernate entity service
 * @author jcai
 * @version 0.1
 */
public class HibernateEntityServiceImplTest extends BaseDbTestCase {

    @Test
    public void test_count(){
        EntityService entityService = getService(EntityService.class);
        QueryRelation qr = entityService.query(UserEntity.class);
        Assert.assertEquals(1,qr.where("id=?",1).count());
    }
}
