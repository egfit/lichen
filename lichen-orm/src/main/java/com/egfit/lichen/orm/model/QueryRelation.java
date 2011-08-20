package com.egfit.lichen.orm.model;


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
	public QueryRelation where(String ... conditions){
		if(conditions.length==0){
			return this;
		}
		whereQl=whereQl.append(new WhereQlFrame(conditions));
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
		private Flow<String> sqlParameters;
		WhereQlFrame(String ... conditions){
			ql = conditions[0];
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
