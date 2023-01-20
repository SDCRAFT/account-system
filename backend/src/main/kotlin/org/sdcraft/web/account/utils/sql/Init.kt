package org.sdcraft.web.account.utils.sql

import jakarta.annotation.PostConstruct
import org.apache.ibatis.jdbc.ScriptRunner
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.io.IoBuilder
import org.sdcraft.web.account.utils.Config
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.io.StringReader
import javax.sql.DataSource

@Repository
class Init {
    companion object

    @Autowired
    private lateinit var dataSource: DataSource
    private var logger: Logger = LogManager.getLogger("SQL Init")


    @PostConstruct
    fun init() {
        val conn = dataSource?.connection
        val alt = "ALTER TABLE `${Config.JDBC.DataBase.name}`.`${Config.JDBC.DataBase.tablePrefix}users`"

        //Init with MYSQL
        ScriptRunner(conn).apply {
            setStopOnError(false)
            setAutoCommit(true)
            setSendFullScript(false)
            setDelimiter(";")
            setFullLineDelimiter(false)
            setLogWriter(null)
            setErrorLogWriter(IoBuilder.forLogger(logger).setLevel(Level.WARN).buildPrintWriter())
            runScript(
                StringReader(
                    "CREATE TABLE IF NOT EXISTS `${Config.JDBC.DataBase.tablePrefix}users`(`UUID` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;" +
                            "\n$alt ADD COLUMN `Email` VARCHAR(255) NOT NULL;" +
                            "\n$alt ADD COLUMN `Username` VARCHAR(20) NOT NULL;" +
                            "\n$alt ADD COLUMN `Password` VARCHAR(255) NOT NULL;" +
                            "\n$alt ADD COLUMN `EmailVerify` TINYINT(1) DEFAULT '0';" +
                            "\n$alt ADD COLUMN `Permission` TINYINT(1) DEFAULT '0';" +
                            "\n$alt ADD COLUMN `createdAt` DATETIME NOT NULL;" +
                            "\n$alt ADD COLUMN `updatedAt` DATETIME NOT NULL;" +
                            "\n$alt ADD PRIMARY KEY (`email`);" +
                            "\n$alt ADD UNIQUE INDEX `email_UNIQUE` (`Email` ASC) VISIBLE;"
                )
            )
        }

    }
}