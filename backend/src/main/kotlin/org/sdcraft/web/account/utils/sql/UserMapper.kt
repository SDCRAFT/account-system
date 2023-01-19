package org.sdcraft.web.account.utils.sql

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.SelectProvider
import org.springframework.stereotype.Repository

@Mapper
@Repository
abstract class UserMapper {
    @SelectProvider(type = Builder::class, method = "buildGetUserByUUID")
    abstract fun getUserByUUID(@Param("uuid") uuid:String)

    //@InsertProvider()
    //@Options(use)
}