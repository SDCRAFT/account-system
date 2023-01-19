package org.sdcraft.web.account.utils.sql

import com.alibaba.druid.pool.DruidDataSource
import org.sdcraft.web.account.utils.Config
import org.sdcraft.web.account.utils.sql.Builder.JDBCUrlBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import javax.sql.DataSource

@Configuration
class Source {
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
        return source
    }
}