package com.robocafe.all.application.security

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.persistence.*

@Entity
class Permission (
        @field:Id val name: String
)

@Entity
@Table(name = "security_role")
class Role (
        @field:Id val name: String
) {
        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "role_default_permissions",
                joinColumns =
                        [JoinColumn(name = "role_name", referencedColumnName = "name")],
                inverseJoinColumns =
                        [JoinColumn(name = "permission_name", referencedColumnName = "name")])
        val permission: MutableSet<Permission> = mutableSetOf()
}

@Entity
@Table(name = "security_object")
class Object (
        @field:Id val id: String,
        val domainId: String?,
        @field:ManyToOne(optional = false)
        @field:JoinColumn(name = "roleId")
        var role: Role,
        @field:ManyToMany(fetch = FetchType.EAGER)
        @field:JoinTable(name = "additional_permissions")
        val additionalPermissions: MutableSet<Permission>,
        var invalidated: Boolean = false
) {
        constructor(id: String, domainId: String?, roleId: String):
                this(id, domainId, Role(roleId), mutableSetOf())
}

@Entity
@Table(name = "authentication")
class Authentication(
        val login: String,
        val password: String,
        @field:Column(name = "so_id")
        val soId: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int? = null
}

@Repository
interface AuthenticationRepository: JpaRepository<Authentication, Int> {
    fun findByLogin(login: String): Authentication?
}

@Repository
interface ObjectRepository: JpaRepository<Object, String> {
    fun findByDomainIdAndRoleName(domainId: String, roleName: String): Object?
}