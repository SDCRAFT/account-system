package org.sdcraft.web.account.utils.sql

import org.apache.ibatis.annotations.Mapper

@Mapper
interface UserMapper {
    /**
     * @param uuid User's UUID string
     * @return User
     * @throws Exception
     */
    @Throws(Exception::class)
    fun findByUUID(uuid:String):User

    /**
     * @param email User's email
     * @return User
     * @throws Exception
     */
    fun findByEmail(email:String):User
}