package com.robocafe.all.events.dispatching

import com.robocafe.all.domain.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

@Service
class AnnotatedEventsDispatcher @Autowired constructor(
        private val messagingTemplate: SimpMessagingTemplate
) {

    private inline fun <reified T : Annotation,
            reified E : DomainEvent> checkRoutingType(event: DomainEvent,
                                                      dispatcher: (event: E, annotation: T) -> Unit
    ) {
        if (event::class.hasAnnotation<T>() && event is E) {
            dispatcher(event, event::class.findAnnotation<T>()!!)
        }
    }

    private val commonPrefix = "/app/tables"
    private val hallPrefix = "$commonPrefix/hall"
    private val chatPrefix = "$commonPrefix/chat"
    private val partyPrefix = "$commonPrefix/party"
    private val tablePrefix = "$commonPrefix/table"
    private val personPrefix = "$commonPrefix/person"

    private fun sendEventIntoBroker(
            event: Any,
            pathPrefix: String,
            path: String,
            domainId: String? = null
    ) {
        val domainInfix = if (domainId != null) "/$domainId" else  ""
        val destination = pathPrefix + domainInfix + path
        messagingTemplate.convertAndSend(destination, event)
    }

    @EventListener
    fun listen(event: DomainEvent) {
        checkRoutingType(event, this::sendToHall)
        checkRoutingType(event, this::sendToChat)
        checkRoutingType(event, this::sendToParty)
        checkRoutingType(event, this::sendToTable)
        checkRoutingType(event, this::sendToPerson)
    }

    fun sendToHall(event: DomainEvent, annotation: SendToHall) = sendEventIntoBroker(
            event.convertForHall(),
            hallPrefix,
            annotation.path
    )

    fun sendToChat(event: ChatDomainEvent, annotation: SendToChat) = sendEventIntoBroker(
            event.convertForChat(),
            chatPrefix,
            annotation.path,
            event.chatId
    )

    fun sendToParty(event: PartyDomainEvent, annotation: SendToParty) = sendEventIntoBroker(
            event.convertForParty(),
            partyPrefix,
            annotation.path,
            event.partyId
    )

    fun sendToTable(event: TableDomainEvent, annotation: SendToTable) = sendEventIntoBroker(
            event.convertForTable(),
            tablePrefix,
            annotation.path,
            event.tableId
    )

    fun sendToPerson(event: PersonDomainEvent, annotation: SendToPerson) = sendEventIntoBroker(
            event.convertForPerson(),
            personPrefix,
            annotation.path,
            event.personId
    )
}