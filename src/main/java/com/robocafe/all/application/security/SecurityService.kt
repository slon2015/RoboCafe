package com.robocafe.all.application.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

fun Object.toSecurityObjectInfo(): SecurityObjectInfo {
    return SecurityObjectInfo(this.id)
}

@Service
class SecurityService @Autowired constructor(
        private val objectRepository: ObjectRepository,
        private val jwtProvider: JwtProvider
) {

    companion object {
        const val TABLE_ROLE = "table"
        const val PARTY_ROLE = "party"
        const val PERSON_ROLE = "person"
        const val WORKER_ROLE = "worker"
    }

    private fun registerSO(domainId: String, role: String): String {
        val soId = UUID.randomUUID().toString()
        val so = Object(
                soId,
                domainId,
                role
        )
        objectRepository.save(so)
        return jwtProvider.generateToken(so.toSecurityObjectInfo())
    }

    fun registerTable(tableId: String) = registerSO(tableId, TABLE_ROLE)
    fun registerParty(partyId: String) = registerSO(partyId, PARTY_ROLE)
    fun registerPerson(personId: String) = registerSO(personId, PERSON_ROLE)
    fun registerWorker(workerId: String) = registerSO(workerId, WORKER_ROLE)

    fun invalidateSOFor(domainId: String, role: String) {
        val so = findSecurityObjectFor(domainId, role)
        so.invalidated = true
        objectRepository.save(so)
    }

    fun invalidateSO(soId: String) {
        val so = findSecurityObject(soId)
        so.invalidated = true
        objectRepository.save(so)
    }

    fun findSecurityObjectFor(domainId: String, role: String): Object {
        val so = objectRepository.findByDomainIdAndRoleName(domainId, role) ?: throw SecurityObjectNotFound()
        if (so.invalidated) {
            throw SecurityObjectInvalidated()
        }
        return so
    }

    fun findSecurityObject(soId: String): Object {
        val so = objectRepository.findById(soId).orElseThrow { SecurityObjectNotFound() }
        if (so.invalidated) {
            throw SecurityObjectInvalidated()
        }
        return so
    }

    fun findToken(soId: String): String {
        return jwtProvider.generateToken(findSecurityObject(soId).toSecurityObjectInfo())
    }

    fun findTokenFor(domainId: String, role: String): String {
        return jwtProvider.generateToken(findSecurityObjectFor(domainId, role).toSecurityObjectInfo())
    }
}