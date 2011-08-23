// Copyright 2011 the original author or authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package lichen.orm.internal;

import lichen.orm.entity.UserEntity;
import lichen.orm.services.BaseDbTestCase;
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
