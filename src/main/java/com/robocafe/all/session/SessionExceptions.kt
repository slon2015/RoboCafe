package com.robocafe.all.session

import com.robocafe.all.application.services.DomainException
import com.robocafe.all.application.services.EntityNotFound
import com.robocafe.all.application.services.OrderInfo

class IncorrectTableStatus: DomainException()

class PartyAlreadyFull: DomainException()
class PersonNotInParty: DomainException()
class PartyHasOpenOrders(val orders: Set<OrderInfo>): DomainException()
class PersonHasOpenOrders(val orders: Set<OrderInfo>): DomainException()

class PaymentTargetBalanceLowerThanAmount: DomainException()
class BalanceForPartyNotPayed: DomainException()
class BalanceForPersonNotPayed: DomainException()

class MembersPartyEnded: DomainException()
class InvalidPersonId: DomainException()
class MemberAlreadyInChat: DomainException()
class MemberNotInChat: DomainException()