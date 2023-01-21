package org.sdcraft.web.account.utils.sql

import com.alibaba.druid.pool.DruidDataSource
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.SqlSessionTemplate
import org.sdcraft.web.account.utils.Config
import org.sdcraft.web.account.utils.sql.Builder.JDBCUrlBuilder
import org.sdcraft.web.account.utils.sql.typeHandler.Boolean
import org.sdcraft.web.account.utils.sql.typeHandler.Time
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.util.*
import javax.sql.DataSource

@Configuration
class Beans {
    private var dataSource: DataSource = DruidDataSource()
    @Bean
    fun dataSource (): DataSource {
        val prop = Properties()
        for (param in Config.JDBC.DataBase.params) {
            prop.setProperty(param.key, param.value)
        }
        prop.setProperty("user", Config.JDBC.DataBase.user)
        prop.setProperty("password", Config.JDBC.DataBase.password)
        val source = DruidDataSource()
        source.driverClassName = Config.JDBC.driveClassname
        source.connectProperties = prop
        source.url = JDBCUrlBuilder(Config.JDBC.driveClassname)
        dataSource = source
        return source
    }

    private var mappersLocation="classpath*:org/sdcraft/web/account/**/*.xml"
    @DependsOn("dataSource")
    @Bean
    fun setSqlSessionFactory(): SqlSessionFactory? {
        val factoryBean = SqlSessionFactoryBean()
        factoryBean.setDataSource(dataSource)
        factoryBean.setTypeHandlers(Boolean(), Time())
        //factoryBean.setTypeHandlers(Time())
        factoryBean.setMapperLocations(*PathMatchingResourcePatternResolver().getResources(mappersLocation))
        return factoryBean.`object`
    }

    @DependsOn("setSqlSessionFactory")
    @Bean
    fun createSqlSessionTemplate(sqlSessionFactory: SqlSessionFactory): SqlSessionTemplate {
        return SqlSessionTemplate(sqlSessionFactory)
    }

}