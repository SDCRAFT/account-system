package org.sdcraft.web.account.utils.sql.typeHandler

import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Boolean

class Boolean: BaseTypeHandler<Boolean>() {
    override fun setNonNullParameter(ps: PreparedStatement?, i: Int, parameter: Boolean?, jdbcType: JdbcType?) {
        if (parameter == true) ps?.setInt(i,1)
        else ps?.setInt(i,0)
    }

    override fun getNullableResult(rs: ResultSet?, columnName: String?): Boolean {
        val man = rs!!.getInt(columnName)
        return man == 1
    }

    override fun getNullableResult(rs: ResultSet?, columnIndex: Int): Boolean {
        val man = rs!!.getInt(columnIndex)
        return man == 1
    }

    override fun getNullableResult(cs: CallableStatement?, columnIndex: Int): Boolean {
        val man = cs!!.getInt(columnIndex)
        return man == 1
    }
}