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
