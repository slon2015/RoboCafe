package com.robocafe.all.events.dispatching

@Target(AnnotationTarget.CLASS)
annotation class EventRouting

@EventRouting
annotation class SendToHall(val path: String)
@EventRouting
annotation class SendToChat(val path: String)
@EventRouting
annotation class SendToParty(val path: String)
@EventRouting
annotation class SendToTable(val path: String)
@EventRouting
annotation class SendToPerson(val path: String)