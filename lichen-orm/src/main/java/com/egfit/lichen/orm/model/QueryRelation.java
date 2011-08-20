package com.egfit.lichen.orm.model;


import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tapestry5.func.F;
import org.apache.tapestry5.func.Flow;
import org.apache.tapestry5.func.Reducer;

/**
 * 查询的封装
 * @author jcai
 */
public class QueryRelation {
	private Flow<QlFrame> whereQl;
	private Flow<Object> parameters;
	private final Reducer<StringBuilder,QlFrame> whereReducer = new WhereQlReducer();
	private int limit;
	private int offset;

	public QueryRelation(){
		this.whereQl= F.flow();
		parameters = F.flow();
	}
	Flow<Object> parameters(){
		return parameters;
	}
	/**
	 * 给定一个ql片段，加入到条件语句.
	 * 
	 * <pre>
	 * QueryRelation qr = ...
	 * qr.where("name='jcai'");
	 * qr.where("name=?","jcai");
	 * qr.where("name=? or password=?","jcai","mypassword");
	 * </pre>
	 * 
	 * @param ql 查询语句片段
	 * @return 查询对象
	 */
	public QueryRelation where(Object ... conditions){
		if(conditions.length==0){
			return this;
		}
		whereQl=whereQl.append(new WhereQlFrame(conditions));
		return this;
	}
	public QueryRelation where(Map<String,Object> conditions){
		Iterator<Entry<String, Object>> it = conditions.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Object> entry = it.next();
			where(entry.getKey()+"=?",entry.getValue());
		}
		return this;
	}
	/**
	 * 限制记录
	 * @param limit 限制条数
	 * @return 查询对象
	 */
	public QueryRelation limit(int limit){
		this.limit = limit;
		return this;
	}
	/**
	 * 数据的起始位置
	 * @param offset 起始位置
	 * @return 查询对象
	 */
	public QueryRelation offset(int offset){
		this.offset = offset;
		return this;
	}
	
	void produceWhereCondition(StringBuilder sb){
		if(whereQl.count() > 0 ){
			sb.append("where 1=1");
			whereQl.reduce(whereReducer, sb);
		}
	}
	interface QlFrame{
		String toSql();
		Flow<?> parameters();
	}
	class WhereQlReducer implements Reducer<StringBuilder,QlFrame>{
		public StringBuilder reduce(StringBuilder accumulator, QlFrame value) {
			parameters = parameters.concat(value.parameters());
			return accumulator.append(" and ").append(value.toSql());
		}
	}
	class WhereQlFrame implements QlFrame{
		private String ql;
		private Flow<Object> sqlParameters;
		WhereQlFrame(Object ... conditions){
			ql = conditions[0].toString();
			sqlParameters=F.flow(conditions);
		}
		public String toSql(){
			return ql;
		}
		public Flow<?> parameters(){
			return sqlParameters.drop(1);
		}
	}
}
