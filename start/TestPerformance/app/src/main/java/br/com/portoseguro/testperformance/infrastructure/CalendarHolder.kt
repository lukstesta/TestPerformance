package br.com.portoseguro.testperformance.infrastructure

import java.util.Calendar
import java.util.Date

class CalendarHolder {

    fun getCalendar(): Calendar = Calendar.getInstance()

    fun getDate(): Date = getCalendar().time
}