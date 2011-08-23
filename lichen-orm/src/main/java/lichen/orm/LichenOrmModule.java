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

package lichen.orm;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import javax.sql.DataSource;

import lichen.orm.internal.AnnotationEntityPackageConfiger;
import lichen.orm.internal.HibernateEntityOperationsImpl;
import lichen.orm.internal.HibernateEntityServiceImpl;
import lichen.orm.internal.HibernateSessionManagerWithSpringImpl;
import lichen.orm.services.AnnotationEntityPackageManager;
import lichen.orm.services.EntityOperations;
import lichen.orm.services.EntityService;
import lichen.orm.services.HibernateConfiger;
import lichen.orm.services.HibernateSessionManager;

import org.apache.tapestry5.func.F;
import org.apache.tapestry5.func.Flow;
import org.apache.tapestry5.func.Predicate;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.PropertyShadowBuilder;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;
import org.hibernate.SessionFactory;
import org.logicalcobwebs.proxool.ProxoolDataSource;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.logicalcobwebs.proxool.configuration.PropertyConfigurator;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.orm.hibernate3.HibernateOperations;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 针对ORM的封装模块
 * @author jcai
 */
public class LichenOrmModule {
    public static void bind(ServiceBinder binder){
        binder.bind(HibernateSessionManager.class, HibernateSessionManagerWithSpringImpl.class);
        binder.bind(EntityService.class, HibernateEntityServiceImpl.class);
        binder.bind(EntityOperations.class,HibernateEntityOperationsImpl.class);
    }
    public static SessionFactory buildSessionFactory(HibernateSessionManager hibernateSessionManager,PropertyShadowBuilder propertyShadowBuilder){
        return propertyShadowBuilder.build(hibernateSessionManager,"sessionFactory",SessionFactory.class);
    }
    public static HibernateOperations buildHibernateTemplate(SessionFactory sf){
        return new HibernateTemplate(sf);
    }
    public static PlatformTransactionManager buildPlatformTransactionManager(SessionFactory sf){
        return new HibernateTransactionManager(sf);
    }
    public static LobHandler buildLobHandler(){
        return new DefaultLobHandler();
    }
    public static void contributeFactoryDefaults(MappedConfiguration<String, Object> configuration){
        configuration.add(LichenSymbols.HIBERNATE_CFG_FILE,"classpath:/lichen.cfg.hibernate.xml");
        configuration.add(LichenSymbols.DATABASE_CFG_FILE,"classpath:lichen.cfg.db.properties");
    }
    public static AnnotationEntityPackageManager buildAnnotationEntityPackageManager(final Collection<String> packages){
        return new AnnotationEntityPackageManager() {
            public Collection<String> getPackages() {
                return packages;
            }
        };
    }
    //构建hibernate的配置
    @Contribute(HibernateSessionManager.class)
    public static void addHibernateConfigruation(OrderedConfiguration<HibernateConfiger> config)
    {
        config.addInstance("AnnotationPackageName", AnnotationEntityPackageConfiger.class);
    }

     //构建数据源
    public static DataSource buildDataSource(@Symbol(LichenSymbols.DATABASE_CFG_FILE) String dbCfgFile,
                                             RegistryShutdownHub shutdownHub)
            throws IOException, ProxoolException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(dbCfgFile);
        Properties info = new Properties();
        info.load(resource.getInputStream());
        PropertyConfigurator.configure(info);

        String poolNameKey = F.flow(info.keySet()).filter(new Predicate<Object>() {
            public boolean accept(Object element) {
                return element.toString().contains("alias");
            }
        }).first().toString();

        if(poolNameKey == null){
            throw new RuntimeException("未定义数据库对应的poolName");
        }
        final String poolName = info.getProperty(poolNameKey);

        //new datasource
        ProxoolDataSource ds = new ProxoolDataSource(poolName);
        //register to shutdown
        shutdownHub.addRegistryShutdownListener(new RegistryShutdownListener()
        {
            public void registryDidShutdown()
            {
                Flow<?> flow=F.flow(ProxoolFacade.getAliases()).filter(new Predicate<String>() {
                    public boolean accept(String element) {
                        return element.equals(poolName);
                    }
                });
                if(flow.count() == 1){
                    try {
                        ProxoolFacade.removeConnectionPool(poolName);
                    } catch (ProxoolException e) {
                        //do nothing
                    }
                }
            }
        });
        return ds;
    }
}
