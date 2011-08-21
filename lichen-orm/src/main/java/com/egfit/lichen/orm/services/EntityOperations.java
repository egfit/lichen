package com.egfit.lichen.orm.services;

import org.apache.tapestry5.func.Flow;

/**
 * 实体操作的服务类
 * @author jcai
 * @version 0.1
 */
public interface EntityOperations {
    /**
     * 查询实体的数量
     * @param entityClass 实体类名称
     * @param condition 条件
     * @param parameters 参数
     */
    public int count(Class<?> entityClass, String condition, Flow<Object> parameters);
}
