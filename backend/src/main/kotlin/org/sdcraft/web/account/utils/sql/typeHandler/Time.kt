package org.sdcraft.web.account.utils.sql.typeHandler

import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.MappedJdbcTypes
import org.apache.ibatis.type.MappedTypes
import java.sql.*
import java.sql.Time


@MappedTypes(value = [Long::class])
@MappedJdbcTypes(JdbcType.DATE, JdbcType.TIME, JdbcType.TIMESTAMP)
class Time: BaseTypeHandler<Long>() {
    @Throws(SQLException::class)
    override fun setNonNullParameter(preparedStatement: PreparedStatement, i: Int, aLong: Long?, jdbcType: JdbcType) {
        if (jdbcType == JdbcType.DATE) preparedStatement.setDate(i, aLong?.let { Date(it) })
        else if (jdbcType == JdbcType.TIME) preparedStatement.setTime(i, aLong?.let { Time(it) })
        else if (jdbcType == JdbcType.TIMESTAMP) preparedStatement.setTimestamp(i, aLong?.let { Timestamp(it) })
    }

    @Throws(SQLException::class)
    override fun getNullableResult(resultSet: ResultSet, s: String?): Long? {
        return parse2time(resultSet.getObject(s))
    }

    @Throws(SQLException::class)
    override fun getNullableResult(resultSet: ResultSet, i: Int): Long? {
        return parse2time(resultSet.getObject(i))
    }

    @Throws(SQLException::class)
    override fun getNullableResult(callableStatement: CallableStatement, i: Int): Long? {
        return parse2time(callableStatement.getObject(i))
    }

    private fun parse2time(value: Any): Long? {
        if (value is Date) return value.time
        else if (value is Time) return value.time
        else if (value is Timestamp) return value.time
        return null
    }
}