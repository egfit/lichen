package com.egfit.lichen.orm.model;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

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
	private QueryRelation createQueryRelation(){
		return new QueryRelation();
	}
}
