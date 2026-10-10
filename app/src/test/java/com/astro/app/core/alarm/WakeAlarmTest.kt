package com.astro.app.core.alarm

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class WakeAlarmTest {

    private val zone = ZoneId.of("Europe/Paris")
    private val today = LocalDate.of(2026, 10, 10)
    private val tomorrow = today.plusDays(1)

    private fun at(date: LocalDate, hour: Int, minute: Int) =
        ZonedDateTime.of(date, LocalTime.of(hour, minute, 42), zone).toInstant()

    @Test
    fun `le prochain reveil tombe le jour demande`() {
        val alarm = WakeAlarm.on(tomorrow, today, at(tomorrow, 7, 15), zone)
        assertEquals(WakeAlarm.At(LocalTime.of(7, 15)), alarm)
    }

    @Test
    fun `un prochain reveil plus tardif signifie aucun reveil ce jour-la`() {
        assertEquals(WakeAlarm.None, WakeAlarm.on(tomorrow, today, at(tomorrow.plusDays(2), 7, 0), zone))
    }

    @Test
    fun `aucun reveil programme signifie aucun reveil a venir`() {
        assertEquals(WakeAlarm.None, WakeAlarm.on(tomorrow, today, null, zone))
    }

    @Test
    fun `apres le prochain reveil on ne sait rien`() {
        val later = tomorrow.plusDays(3)
        assertEquals(WakeAlarm.Unknown, WakeAlarm.on(later, today, at(tomorrow, 7, 0), zone))
    }

    @Test
    fun `aujourd'hui et le passe restent inconnus`() {
        assertEquals(WakeAlarm.Unknown, WakeAlarm.on(today, today, at(tomorrow, 7, 0), zone))
        assertEquals(WakeAlarm.Unknown, WakeAlarm.on(today.minusDays(1), today, null, zone))
    }

    @Test
    fun `le jour du reveil est celui du fuseau de l'appareil`() {
        // 23:30 à Paris est déjà le lendemain à Tokyo.
        val instant = ZonedDateTime.of(today, LocalTime.of(23, 30), zone).toInstant()
        val tokyo = ZoneId.of("Asia/Tokyo")
        assertEquals(WakeAlarm.At(LocalTime.of(6, 30)), WakeAlarm.on(tomorrow, today, instant, tokyo))
    }
}
