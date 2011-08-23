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
import lichen.orm.services.EntityService;
import lichen.orm.services.QueryRelation;

/**
 * hibernated entity service
 * @author jcai
 * @version 0.1
 */
public class HibernateEntityServiceImpl implements EntityService{
    private EntityOperations entityOperations;

    public HibernateEntityServiceImpl(EntityOperations entityOperations){
        this.entityOperations = entityOperations;
    }
    public <T> QueryRelation<T> query(Class<T> clazz) {
        return new QueryRelation<T>(clazz,this.entityOperations);
    }
}
