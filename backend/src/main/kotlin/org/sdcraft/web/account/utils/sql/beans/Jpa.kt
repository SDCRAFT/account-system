package org.sdcraft.web.account.utils.sql.beans

import org.sdcraft.web.account.utils.Config
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.Database
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


@EnableJpaRepositories(
    //basePackages = [""],
    repositoryImplementationPostfix = "Impl",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager")
@EnableTransactionManagement
@Component("Jpa")
class Jpa {
    private fun getDataBaseType(): Database? {
        if (Config.JDBC.driveClassname == "com.mysql.jdbc.Driver" || Config.JDBC.driveClassname == "com.mysql.cj.jdbc.Driver") {
            return Database.MYSQL
        } else if (Config.JDBC.driveClassname == "org.postgresql.Driver") {
            return Database.POSTGRESQL
        }
        return null
    }
    @Bean
    fun jpaVendorAdapter(): JpaVendorAdapter{
        val adapter = HibernateJpaVendorAdapter()
        getDataBaseType()?.let { adapter.setDatabase(it) }
        adapter.setShowSql(true)
        adapter.setGenerateDdl(false)
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect")
        return adapter
    }

    @Bean
    fun entityManagerFactory(
        dataSource: DataSource, jpaVendorAdapter: JpaVendorAdapter?
    ): LocalContainerEntityManagerFactoryBean? {
        val factory = LocalContainerEntityManagerFactoryBean()
        factory.dataSource = dataSource
        factory.jpaVendorAdapter = jpaVendorAdapter
        factory.setPackagesToScan("org.sdcraft.web.account.utils.sql.entities.*")
        return factory
    }

    /*@Bean
    fun transactionManager(emf: EntityManagerFactory): PlatformTransactionManager? {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = emf
        return transactionManager
    }*/
}