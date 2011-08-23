package lichen.orm.internal;

import lichen.orm.services.AnnotationEntityPackageManager;
import lichen.orm.services.HibernateConfiger;
import org.apache.tapestry5.ioc.services.ClassNameLocator;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import java.util.Collection;

/**
 * 针对注解的hibernate实体配置
 * @author jcai
 * @version 0.1
 */
public class AnnotationEntityPackageConfiger implements HibernateConfiger{
    private Collection<String> packages;
    private ClassNameLocator classNameLocator;

    public AnnotationEntityPackageConfiger(AnnotationEntityPackageManager manager,ClassNameLocator classNameLocator){
        this.packages = manager.getPackages();
        this.classNameLocator = classNameLocator;
    }
    public void config(Configuration cfg) throws HibernateException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        for (String packageName : packages)
        {
            cfg.addPackage(packageName);

            for (String className : classNameLocator.locateClassNames(packageName))
            {
                try
                {
                    Class entityClass = contextClassLoader.loadClass(className);

                    cfg.addAnnotatedClass(entityClass);
                }
                catch (ClassNotFoundException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
