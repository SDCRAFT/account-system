package org.sdcraft.web.account.utils

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files

@Component
@ConfigurationProperties(value = "")
object Config {
    private val configDir=File(System.getProperty("user.dir"), "config")
    private val configYml=File(configDir, "config.yml")

    fun getDBPrefix(): String {
        return JDBC.DataBase.tablePrefix
    }

    @Bean
    @JvmStatic
    @PostConstruct
    fun properties(): PropertySourcesPlaceholderConfigurer {
        val con = PropertySourcesPlaceholderConfigurer()
        val yaml = YamlPropertiesFactoryBean()
        try {
            yaml.setResources(FileSystemResource(configYml))
            yaml.`object`?.let { con.setProperties(it) }
        } catch (e:IllegalStateException){
            if (!configDir.exists() && !configDir.mkdirs()) throw RuntimeException("Failed to create " + configDir.path)
            ResReader.getResourceAsInputStream("/config.yml").use { `is` -> `is`?.let { Files.copy(it, configYml.toPath()) } }
            yaml.setResources(FileSystemResource(configYml))
            yaml.`object`?.let { con.setProperties(it) }
        }
        return con
    }
    @Component
    @ConfigurationProperties(value = "jdbc-config")
    object JDBC {
        var host:String = ""
        var port:Int = 0
        var driveClassname:String = ""
        @Component
        @ConfigurationProperties(value = "jdbc-config.database")
         object DataBase {
            var user:String = ""
            var password:String = ""
            var name:String = ""
            var tablePrefix = ""
            var params:LinkedHashMap<String, String> = LinkedHashMap()
        }
    }
    var libRepourl=""

}