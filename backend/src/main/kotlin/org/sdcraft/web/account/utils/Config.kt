package org.sdcraft.web.account.utils

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.util.*

@Component("Config")
@ConfigurationProperties(value = "")
object Config {
    private val configDir=File(System.getProperty("user.dir"), "config")
    private val configYml=File(configDir, "config.yml")

    fun getDBPrefix(): String {
        return JDBC.DataBase.tablePrefix
    }

    @Bean
    @JvmStatic
    fun properties(): PropertySourcesPlaceholderConfigurer {
        println("Config init")
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
        println(Properties(yaml.`object`))
        return con
    }
    @Component
    @ConfigurationProperties(value = "jdbc-config")
    object JDBC {
        var host:String = ""
        var port:Int = 0
        var driveClassname:String = ""
        var debugger:Boolean = false
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