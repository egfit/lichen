// Copyright 2011 The EGF IT Software Department
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


import org.apache.tapestry5.func.F;
import org.apache.tapestry5.func.Flow;
import org.apache.tapestry5.func.Reducer;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * 查询的封装,实现方便快捷的查询.
 *
 * <pre>
 *
 * 假设 User和Role实体，user有一个属性roles和Role关联
 *
 * 一、查询单一实体
 * 1.1	 User.find(10)
 * 如果未发现，则抛出异常
 * 1.2 User.first
 * 查找第一个，如果未发现，则返回null
 *
 * 二、多个实体
 * it=User.all
 * while(it.hasNext){}
 *
 * 三、条件
 * 3.1 纯字符条件
 * User.where("name='jack'")
 * 3.2 数组条件
 * User.where("name=?",name)
 * User.where("name=? and password=?",name,password)
 * 3.3 Map
 * map=new HashMap();
 * map.put("name","jcai");
 * User.where(map)
 * 3.3 范围查询
 * User.range("age",12,13)
 * 3.4 子集查询(in)
 * User.in("age",23,24,25,26)
 *
 * 四、排序
 * User.order("name")
 * User.order("name desc")
 * User.order("name asc")
 *
 * 五、选取特定字段
 * User.select("name,age")
 *
 * 六、分页
 * User.limit(5)
 * user.limit(5).offset(10)
 *
 * 七、分组
 * User.group("age").order("age")
 *
 * 八、两表联查
 * User.joins("roles","r").where("r.name=?",roleName)
 *
 * 九、存在
 * User.exists(1)
 * User.where("name=?","jcai").exists()
 *
 * 十、计算
 * User.count()
 * User.where("name=?","jcai").count()
 * User.where("name=?","jcai").average("age")
 * User.maximum("age")
 * User.minimum("age")
 * User.sum("age")
 * </pre>
 *
 * @author jcai
 */
public class QueryRelation {
    private Flow<QlFrame> whereQl;
    private Flow<Object> parameters;
    private final Reducer<StringBuilder, QlFrame> whereReducer = new WhereQlReducer();
    private int limit;
    private int offset;
    private Class<?> entityClass;
    private EntityOperations entityOperations;

    public QueryRelation(Class<?> entityClass, EntityOperations entityOperations) {
        this.entityClass = entityClass;
        this.entityOperations = entityOperations;
        this.whereQl = F.flow();
        parameters = F.flow();

    }

    Flow<Object> parameters() {
        return parameters;
    }

    /**
     * 统计出数量
     *
     * @return 数量
     */
    public int count() {
        StringBuilder sb = new StringBuilder();
        this.produceWhereCondition(sb);
        return entityOperations.count(entityClass, sb.toString(), parameters);
    }

    /**
     * 给定一个ql片段，加入到条件语句.
     * <p/>
     * <pre>
     * QueryRelation qr = ...
     * qr.where("name='jcai'");
     * qr.where("name=?","jcai");
     * qr.where("name=? or password=?","jcai","mypassword");
     * </pre>
     *
     * @param conditions 查询语句
     * @return 查询对象
     */
    public QueryRelation where(Object... conditions) {
        if (conditions.length == 0) {
            return this;
        }
        whereQl = whereQl.append(new WhereQlFrame(conditions));
        return this;
    }

    /**
     * 加入Map作为查询条件
     * <pre>
     * QueryRelation qr = ...
     * Map<String,Object> conditions = new HashMap<String,Object>();
     * conditions.put("name","jcai");
     * qr.where(conditions);
     * </pre>
     *
     * @param conditions Map的查询条件
     * @return 查询对象
     */
    public QueryRelation where(Map<String, Object> conditions) {
        Iterator<Entry<String, Object>> it = conditions.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Object> entry = it.next();
            where(entry.getKey() + "=?", entry.getValue());
        }
        return this;
    }

    /**
     * 限制记录
     *
     * @param limit 限制条数
     * @return 查询对象
     */
    public QueryRelation limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * 数据的起始位置
     *
     * @param offset 起始位置
     * @return 查询对象
     */
    public QueryRelation offset(int offset) {
        this.offset = offset;
        return this;
    }

    void produceWhereCondition(StringBuilder sb) {
        if (whereQl.count() > 0) {
            sb.append("where 1=1");
            whereQl.reduce(whereReducer, sb);
        }
    }

    interface QlFrame {
        String toSql();

        Flow<?> parameters();
    }

    class WhereQlReducer implements Reducer<StringBuilder, QlFrame> {
        public StringBuilder reduce(StringBuilder accumulator, QlFrame value) {
            parameters = parameters.concat(value.parameters());
            return accumulator.append(" and ").append(value.toSql());
        }
    }

    class WhereQlFrame implements QlFrame {
        private String ql;
        private Flow<Object> sqlParameters;

        WhereQlFrame(Object... conditions) {
            ql = conditions[0].toString();
            sqlParameters = F.flow(conditions);
        }

        public String toSql() {
            return ql;
        }

        public Flow<?> parameters() {
            return sqlParameters.drop(1);
        }
    }
}
