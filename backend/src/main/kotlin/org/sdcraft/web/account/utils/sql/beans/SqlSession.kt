package org.sdcraft.web.account.utils.sql.beans

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean
import org.apache.ibatis.session.SqlSessionFactory
import org.sdcraft.web.account.utils.sql.typeHandler.Boolean
import org.sdcraft.web.account.utils.sql.typeHandler.Time
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import javax.sql.DataSource

@Configuration
class SqlSession {
    private var mappersLocation="classpath*:org/sdcraft/web/account/**/*.xml"
    @DependsOn("DataSource")
    @Bean
    fun setSqlSessionFactory(source: DataSource): SqlSessionFactory? {
        val factoryBean = MybatisSqlSessionFactoryBean()
        factoryBean.setDataSource(source)
        factoryBean.setTypeHandlers(Boolean(), Time())
        //factoryBean.setTypeHandlers(Time())
        factoryBean.setMapperLocations(*PathMatchingResourcePatternResolver().getResources(mappersLocation))
        return factoryBean.`object`
    }
}