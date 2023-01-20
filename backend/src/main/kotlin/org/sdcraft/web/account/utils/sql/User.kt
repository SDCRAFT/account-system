package org.sdcraft.web.account.utils.sql
import java.util.*


class User {
    var uuid = UUID.randomUUID().toString()
    var username = ""
    var email = ""
    var passwordEncrypted = ""
    var permissionLevel = 0
    var activeStatus = false
    var createTime = 0
    var updateTime = 0
}