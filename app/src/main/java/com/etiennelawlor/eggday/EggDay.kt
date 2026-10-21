package com.etiennelawlor.eggday

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Formats a day of the year the way egg cartons print it: always three digits (001–366). */
fun formatDayOfYear(day: Int): String = "%03d".format(day)

/**
 * Resolves a carton's three-digit pack date to a calendar date.
 *
 * A pack date later than today's day of the year must be from last year (e.g. 350 seen in
 * January). Returns null when the number can't be a pack date: outside 1..366, or 366 in a
 * year that isn't a leap year.
 */
fun packDate(packDay: Int, today: LocalDate): LocalDate? {
    val year = if (packDay <= today.dayOfYear) today else today.minusYears(1)
    return if (packDay in 1..year.lengthOfYear()) year.withDayOfYear(packDay) else null
}

/** Days between the pack date and [today], or null when [packDay] isn't a valid pack date. */
fun daysSincePack(packDay: Int, today: LocalDate): Int? =
    packDate(packDay, today)?.let { ChronoUnit.DAYS.between(it, today).toInt() }
