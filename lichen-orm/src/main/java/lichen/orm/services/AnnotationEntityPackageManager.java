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

import java.util.Collection;

/**
 * 针对带有Annotation的包管理
 * @author jcai
 * @version 0.1
 */
public interface AnnotationEntityPackageManager {
    /**
     * 返回所有包含了实体的包
     * @return 所有包的集合
     */
    public Collection<String> getPackages();
}
