package org.sdcraft.web.account.utils.sql

import org.sdcraft.web.account.utils.Config

object Builder {
    fun JDBCUrlBuilder(driveClassName: String): String? {
        val formatStr = "jdbc:%s://%s:%d/%s"
        if (driveClassName == "com.mysql.jdbc.Driver" || driveClassName == "com.mysql.cj.jdbc.Driver") {
            return formatStr.format("mysql", Config.JDBC.host, Config.JDBC.port, Config.JDBC.DataBase.name)
        } else if (driveClassName == "org.postgresql.Driver") {
            return formatStr.format("postgresql", Config.JDBC.host, Config.JDBC.port, Config.JDBC.DataBase.name)
        }
        return null
    }
}