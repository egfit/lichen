package com.egfit.lichen.orm.model;

import static org.junit.Assert.*;

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
	}
	private QueryRelation createQueryRelation(){
		return new QueryRelation();
	}
}
