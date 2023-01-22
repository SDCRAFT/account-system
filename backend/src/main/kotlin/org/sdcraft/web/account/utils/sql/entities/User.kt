package org.sdcraft.web.account.utils.sql.entities

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import java.util.*

@Entity
@Table(name = "\${Config.JDBC.DataBase.tablePrefix}users")
data class User (
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(name = "uuid",unique = true)
    var uuid: String = UUID.randomUUID().toString(),
    @Column(name = "username",unique = true,nullable = false)
    var username: String,
    @Column(name = "email",unique = true,nullable = false)
    var email: String,
    @Column(name = "password",nullable = false)
    var passwordEncrypted: String,
    @Column(name = "level",nullable = false)
    var permissionLevel: Int = 0,
    @Column(name = "activeStats",nullable = false)
    var activeStats: Boolean = false,
    @Column(name = "createdAt",nullable = false)
    var createdTime: Long = Instant.now().epochSecond,
    @Column(name = "updatedAt",nullable = false)
    var updatedTime: Long = Instant.now().epochSecond
)