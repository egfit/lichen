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
