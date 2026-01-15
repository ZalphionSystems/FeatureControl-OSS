package com.zalphion.featurecontrol.events

import com.zalphion.featurecontrol.members.MemberDetails
import com.zalphion.featurecontrol.teams.TeamId
import dev.forkhandles.values.UUIDValue
import dev.forkhandles.values.UUIDValueFactory

import java.time.Instant
import java.util.UUID

class EventId(value: UUID): UUIDValue(value) {
    companion object: UUIDValueFactory<EventId>(::EventId)
}

interface Event {
    val eventId: EventId
    val teamId: TeamId
    val time: Instant
}

class MemberCreatedEvent(
    override val eventId: EventId,
    override val teamId: TeamId,
    override val time: Instant,
    val member: MemberDetails
): Event