package org.sdcraft.web.account.utils.sql
import org.apache.ibatis.annotations.AutomapConstructor
import java.time.Instant
import java.util.*

val timestamp = Instant.now().epochSecond

data class User @AutomapConstructor constructor(
    var uuid: String = UUID.randomUUID().toString(),
    var username: String,
    var email: String,
    var passwordEncrypted: String,
    var permissionLevel: Int = 0,
    var activeStatus: Boolean = false,
    var createTime: Long = timestamp,
    var updateTime: Long = timestamp
)