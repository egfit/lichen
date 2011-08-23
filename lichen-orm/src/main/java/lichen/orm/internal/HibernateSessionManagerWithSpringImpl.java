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

import java.util.List;

import javax.sql.DataSource;

import lichen.orm.LichenSymbols;
import lichen.orm.services.HibernateConfiger;
import lichen.orm.services.HibernateSessionManager;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

/**
 * 用Spring来管理hibernate的SessionManager
 * @author jcai
 * @version 0.1
 */
public class HibernateSessionManagerWithSpringImpl implements HibernateSessionManager,RegistryShutdownListener {
    private SessionFactory sessionFactory;
    private Configuration configuration;
    private AnnotationSessionFactoryBean sessionFactoryBean;

    public HibernateSessionManagerWithSpringImpl(final List<HibernateConfiger> hibernateConfigurers,
                                                 final DataSource ds,
                                                 final LobHandler lobHandler,
                                                 final @Symbol(LichenSymbols.HIBERNATE_CFG_FILE) String cfgFile){
        sessionFactoryBean = new AnnotationSessionFactoryBean() {
            @SuppressWarnings({"deprecation"})
            protected void postProcessAnnotationConfiguration(AnnotationConfiguration hibernateConfig)
                    throws HibernateException {
                for(HibernateConfiger configer:hibernateConfigurers){
                   configer.config(hibernateConfig);
                }
            }
        };

        //set hibernate cfg file
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        sessionFactoryBean.setConfigLocation(resourceLoader.getResource(cfgFile));

        //datasource
        sessionFactoryBean.setDataSource(ds);
        sessionFactoryBean.setLobHandler(lobHandler);
        sessionFactoryBean.setSchemaUpdate(false);

        try {
            //build SessionFactory Bean
            sessionFactoryBean.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sessionFactory = (SessionFactory) sessionFactoryBean.getObject();
        configuration = sessionFactoryBean.getConfiguration();
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void registryDidShutdown() {
        sessionFactoryBean.destroy();
    }
}
