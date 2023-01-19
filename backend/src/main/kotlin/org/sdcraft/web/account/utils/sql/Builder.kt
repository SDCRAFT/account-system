package org.sdcraft.web.account.utils.sql

import org.apache.ibatis.annotations.Param
import org.apache.ibatis.jdbc.SQL
import org.sdcraft.web.account.utils.Config

object Builder {
    fun buildGetUserByUUID(@Param("uuid") uuid:String): String {
        return SQL().SELECT("*").FROM("${Config.JDBC.DataBase.tablePrefix}users").WHERE("UUID = $uuid").toString()
    }
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