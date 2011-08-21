package com.egfit.lichen.orm.model;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import com.egfit.lichen.orm.entity.UserEntity;
import com.egfit.lichen.orm.services.EntityOperations;
import org.apache.tapestry5.func.Flow;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import javax.naming.OperationNotSupportedException;

public class QueryRelationTest {

	@Test
	public void testProduceWhereCondition() {
		QueryRelation qr = createQueryRelation();
		StringBuilder sb = new StringBuilder();
		qr.produceWhereCondition(sb);
		assertEquals("",sb.toString());
		qr.where("name='jcai'");
		qr.produceWhereCondition(sb);
		assertEquals("where 1=1 and name='jcai'", sb.toString());
		assertEquals(0,qr.parameters().count());
		
		sb.setLength(0);
		qr.where("password=?","mypassword");
		qr.produceWhereCondition(sb);
		assertEquals("where 1=1 and name='jcai' and password=?", sb.toString());
		assertEquals(1,qr.parameters().count());
		assertEquals("mypassword",qr.parameters().first());
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

        StringBuilder sb = new StringBuilder();
        Map<String,Object> conditions = new HashMap<String,Object>();
        conditions.put("name", "jcai");
        Assert.assertEquals(1,qr.where(conditions).count());

        EasyMock.verify(operations);
    }
	private QueryRelation createQueryRelation(){
		return new QueryRelation(UserEntity.class, EasyMock.createMock(EntityOperations.class));
	}
}
