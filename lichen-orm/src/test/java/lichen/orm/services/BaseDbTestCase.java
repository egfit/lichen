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

import lichen.orm.LichenOrmModule;
import lichen.orm.entity.UserEntity;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.test.IOCTestCase;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.hibernate3.HibernateOperations;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.config.TxNamespaceHandler;
import sun.tools.jconsole.Plotter;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 基于数据库的测试
 * @author jcai
 * @version 0.1
 */
public class BaseDbTestCase{
    private Registry registry;
    @Before
    public void setupContainer(){
        registry = buildRegistry(LichenOrmModule.class,MemoryDatabaseModule.class);
        registry.performRegistryStartup();
    }
    protected <T> T getService(Class<T> serviceInterface){
        return registry.getService(serviceInterface);
    }
    @After
    public void shutdownContainer(){
       registry.shutdown();
    }
    public static class MemoryDatabaseModule{
        @Startup
        public static void initDb(DataSource ds,HibernateSessionManager sessionManager,
               HibernateOperations hibernateOperations,PlatformTransactionManager transaction) throws SQLException, HibernateException {
            JdbcTemplate jdbcTemplate=new JdbcTemplate(ds);
            Dialect dialect = Dialect.getDialect(sessionManager.getConfiguration().getProperties());
            DatabaseMetadata metadata= new DatabaseMetadata(jdbcTemplate.getDataSource().getConnection(), dialect);
            String [] sql = sessionManager.getConfiguration().generateDropSchemaScript(dialect);
            try{
                for(String s:sql)
                    jdbcTemplate.execute(s);
            }catch(Exception e){}
            sql=sessionManager.getConfiguration().generateSchemaCreationScript(dialect);
            try{
                for(String s:sql)
                    jdbcTemplate.execute(s);
            }catch(Exception e){}

            //init data
            TransactionStatus tx = transaction.getTransaction(null);

            UserEntity userEntity = new UserEntity();
            userEntity.setName("jcai");
            userEntity.setAge(30);
            hibernateOperations.save(userEntity);

            transaction.commit(tx);
        }
        public static void contributeAnnotationEntityPackageManager(Configuration<String> configuration){
            configuration.add("lichen.orm.entity");
        }
    }
    /**
     * Builds a Registry for the provided modules; caller should shutdown the Registry when done.
     */
    protected final Registry buildRegistry(Class... moduleClasses)
    {
        RegistryBuilder builder = new RegistryBuilder();

        builder.add(moduleClasses);

        return builder.build();
    }
}
