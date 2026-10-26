package com.bizpulse.app.util
import java.time.*
import java.time.format.DateTimeFormatter
object DateUtils {
    fun today() = LocalDate.now().toString()
    fun time() = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    fun since(days: Int) = LocalDate.now().minusDays((days-1).toLong()).toString()
    fun stamp(ms: Long) = Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMM d, yyyy • HH:mm"))
}
