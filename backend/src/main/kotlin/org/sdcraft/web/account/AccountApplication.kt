package org.sdcraft.web.account

import org.sdcraft.web.account.utils.Config
import org.sdcraft.web.account.utils.LibrariesLoader
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.io.File
import java.io.IOException
import java.util.*

@EnableConfigurationProperties(Config::class)
@SpringBootApplication
@EnableJpaRepositories(basePackages = [])
@EntityScan(basePackages = [])
class AccountApplication

fun main(args: Array<String>) {
    runApplication<AccountApplication>(*args)
    val list: MutableList<Array<String>> = LinkedList()
    //list.add(arrayOf("org.slf4j", "slf4j-simple", "1.7.25", ""))
    try {
        for (strs in list) {
            LibrariesLoader().loadLibraryClassMaven(
                strs[0], strs[1], strs[2], strs[3], Config.libRepourl, File(System.getProperty("user.dir"), "libs")
            )
        }
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
    println("aa--${Config.libRepourl}--aa")
}