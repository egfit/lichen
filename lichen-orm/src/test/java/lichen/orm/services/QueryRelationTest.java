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

package lichen.orm.services;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import lichen.orm.entity.UserEntity;

import org.apache.tapestry5.func.Flow;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

public class QueryRelationTest {

	@Test
	public void testProduceWhereCondition() {
		QueryRelation qr = createQueryRelation();
		StringBuilder sb = new StringBuilder();
		qr.produceWhereCondition(sb);
		assertEquals("",sb.toString());

        qr = createQueryRelation();
		qr.where("name='jcai'");
		qr.produceWhereCondition(sb);
		assertEquals("where 1=1 and name='jcai'", sb.toString());
		assertEquals(0,qr.parameters().count());

        qr = createQueryRelation();
		sb.setLength(0);
		qr.where("password=? and age=?","mypassword",17);
		qr.produceWhereCondition(sb);
		assertEquals("where 1=1 and password=? and age=?", sb.toString());
		assertEquals(2,qr.parameters().count());
		assertEquals("mypassword",qr.parameters().first());
        assertEquals(17,qr.parameters().toList().get(1));

	}
	@Test
	public void testProduceWhereConditionMap() {
		QueryRelation qr = createQueryRelation();
		StringBuilder sb = new StringBuilder();
		Map<String,Object> conditions = new HashMap<String,Object>();
		conditions.put("name", "jcai");
		qr.where(conditions);
		qr.produceWhereCondition(sb);
		assertEquals("where 1=1 and name=?", sb.toString());
		assertEquals(1,qr.parameters().count());
		assertEquals("jcai",qr.parameters().first());
	}
    @Test
    public void testCount(){
        EntityOperations operations = EasyMock.createMock(EntityOperations.class);
        QueryRelation qr = new QueryRelation(UserEntity.class,operations);

        EasyMock.expect(operations.count(EasyMock.isA(Class.class),EasyMock.eq("where 1=1 and name=?"),EasyMock.isA(Flow.class))).andReturn(1);

        EasyMock.replay(operations);

        Map<String,Object> conditions = new HashMap<String,Object>();
        conditions.put("name", "jcai");
        Assert.assertEquals(1,qr.where(conditions).count());

        EasyMock.verify(operations);
    }
	private QueryRelation createQueryRelation(){
		return new QueryRelation(UserEntity.class, EasyMock.createMock(EntityOperations.class));
	}
}
