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

import lichen.orm.services.EntityOperations;

import org.apache.tapestry5.func.Flow;
import org.springframework.orm.hibernate3.HibernateOperations;

/**
 * Hibernate Entity Operations
 * @author jcai
 * @version 0.1
 */
public class HibernateEntityOperationsImpl implements EntityOperations{
    private HibernateOperations hibernateOperations;

    public HibernateEntityOperationsImpl(HibernateOperations hibernateOperations){
        this.hibernateOperations = hibernateOperations;
    }
    public int count(Class<?> entityClass, String condition, Flow<Object> parameters) {
        return ((Long)hibernateOperations.find(
                "select count(*) from "+entityClass.getName() +" "+condition,
                parameters.toArray(Object.class)).get(0)).intValue();
    }
}
