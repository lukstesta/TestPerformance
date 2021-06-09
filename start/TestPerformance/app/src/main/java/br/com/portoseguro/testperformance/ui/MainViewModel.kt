package br.com.portoseguro.testperformance.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.portoseguro.testperformance.business.Business
import br.com.portoseguro.testperformance.infrastructure.Analytics
import br.com.portoseguro.testperformance.infrastructure.CalendarHolder
import br.com.portoseguro.testperformance.infrastructure.onError
import br.com.portoseguro.testperformance.infrastructure.onSuccess
import java.util.Calendar

class MainViewModel(
    private val business: Business,
    private val analytics: Analytics,
    private val calendarHolder: CalendarHolder
) : ViewModel() {

    private val _state = MutableLiveData<ListState>()
    val state: LiveData<ListState>
        get() = _state

    fun fetchData() {
        if (checkOpenDay()) {
            _state.value = ListState.Close
            return
        }

        _state.value = ListState.Loading

        business.fetchData()
            .onSuccess {
                handleSuccess(it)
                trackSuccess(it.size)
            }
            .onError {
                _state.value = ListState.Error
                trackError()
            }
    }

    private fun checkOpenDay() = getDayOfWeek() == Calendar.SUNDAY

    private fun getDayOfWeek(): Int {
        val cal = calendarHolder.getCalendar()
        return cal.get(Calendar.DAY_OF_WEEK)
    }

    private fun handleSuccess(it: List<String>) {
        _state.value = if (it.isEmpty()) {
            ListState.Empty
        } else {
            ListState.Success(it)
        }
    }

    fun getOpeningInfo() = when (getDayOfWeek()) {
        Calendar.SUNDAY -> "Estamos fechados"
        Calendar.SATURDAY -> "Aberto das 8:00h às 12:00h"
        else -> "Aberto das 8:00h às 18:00h"
    }

    private fun trackSuccess(quantity: Int) {
        val contentData = mapOf(
            "status" to "sucesso",
            "user" to "luke skywalker",
            "quantidade" to quantity.toString()
        )
        analytics.trackState(contentData)
    }

    private fun trackError() {
        val contentData = mapOf(
            "status" to "erro"
        )
        analytics.trackState(contentData)
    }
}

sealed class ListState {
    data class Success(val names: List<String>) : ListState()
    object Close : ListState()
    object Empty : ListState()
    object Error : ListState()
    object Loading : ListState()
}