package com.robocafe.all.application.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class SecurityService @Autowired constructor(
        private val objectRepository: ObjectRepository,
        private val jwtProvider: JwtProvider
) {

    private fun registerSO(domainId: String, role: String): String {
        val soId = UUID.randomUUID().toString()
        val so = Object(
                soId,
                domainId,
                role
        )
        objectRepository.save(so)
        return jwtProvider.generateToken(SecurityObjectInfo(soId))
    }

    fun registerTable(tableId: String) = registerSO(tableId, "table")
    fun registerParty(partyId: String) = registerSO(partyId, "party")
    fun registerPerson(personId: String) = registerSO(personId, "person")
    fun registerWorker(workerId: String) = registerSO(workerId, "worker")

    fun invalidateSO(soId: String) {
        val so = findSecurityObject(soId)
        so.invalidated = true
        objectRepository.save(so)
    }

    fun findSecurityObject(soId: String): Object {
        val so = objectRepository.findById(soId).orElseThrow { SecurityObjectNotFound() }
        if (so.invalidated) {
            throw SecurityObjectInvalidated()
        }
        return so
    }
}