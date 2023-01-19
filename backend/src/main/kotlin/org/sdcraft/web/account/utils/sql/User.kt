package org.sdcraft.web.account.utils.sql
import java.util.*


class User {
    private var uuid = UUID.randomUUID().toString()
    private var username = ""
    private var email = ""
    private var passwordEncrypted = ""
    private var permissionLevel = 0
    private var activeStatus = false
}