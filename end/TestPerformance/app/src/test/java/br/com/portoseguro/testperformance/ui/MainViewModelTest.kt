package br.com.portoseguro.testperformance.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.portoseguro.testperformance.business.Business
import br.com.portoseguro.testperformance.infrastructure.Analytics
import br.com.portoseguro.testperformance.infrastructure.CalendarHolder
import br.com.portoseguro.testperformance.infrastructure.Result
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Calendar

@RunWith(JUnit4::class)
class MainViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var business: Business

    @MockK
    private lateinit var analytics: Analytics

    @MockK
    private lateinit var calendarHolder: CalendarHolder

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        viewModel = MainViewModel(business, analytics, calendarHolder)
    }

    @Test
    fun fetchData_onSuccessWithData_shouldReturnData() {
        // Arrange
        mockDataSuccessWithData()
        mockDayWeek(WEDNESDAY)

        // Act
        viewModel.fetchData()

        // Assert
        assertTrue(viewModel.state.value is ListState.Success)
        val data = viewModel.state.value as ListState.Success

        assertEquals("X-Salda", data.names[0])
        assertEquals("Coca-Cola", data.names[1])
        assertEquals(2, data.names.size)
    }

    @Test
    fun trackSuccess_withoutData_shouldSendZeroItemsQuantity() {
        // Arrange
        val slot = slot<Map<String, String>>()
        mockDataSuccessWithEmptyList()
        mockDayWeek(WEDNESDAY)

        // Act
        viewModel.fetchData()

        // Assert
        verify(exactly = 1) { analytics.trackState(capture(slot)) }

        val data = slot.captured
        assertEquals("sucesso", data["status"])
        assertEquals("0", data["quantidade"])
        assertEquals(2, data.size)
    }

    @Test
    fun fetchData_onSuccessWithoutData_shouldReturnEmptyState() {
        // Arrange
        mockDataSuccessWithEmptyList()
        mockDayWeek(WEDNESDAY)

        // Act
        viewModel.fetchData()

        // Assert
        assertTrue(viewModel.state.value is ListState.Empty)
    }

    @Test
    fun fetchData_onSuccess_shouldShowLoading() {
        // Arrange
        val states = mutableListOf<ListState>()
        viewModel.state.observeForever { states.add(it) }

        mockDataSuccessWithData()
        mockDayWeek(WEDNESDAY)

        // Act
        viewModel.fetchData()

        // Assert
        assertTrue(states[0] is ListState.Loading)
        assertTrue(states[1] is ListState.Success)

        assertEquals(2, states.size)
    }

    @Test
    fun fetchData_onError_shouldReturnErrorState() {
        // Arrange
        mockDataError()
        mockDayWeek(WEDNESDAY)

        // Act
        viewModel.fetchData()

        // Assert
        assertTrue(viewModel.state.value is ListState.Error)
    }

    @Test
    fun trackSuccess_withData_shouldSendCorrectItemsQuantity() {
        // Arrange
        val slot = slot<Map<String, String>>()
        mockDataSuccessWithData()
        mockDayWeek(WEDNESDAY)

        // Act
        viewModel.fetchData()

        // Assert
        verify(exactly = 1) { analytics.trackState(capture(slot)) }

        val data = slot.captured
        assertEquals("sucesso", data["status"])
        assertEquals("2", data["quantidade"])
        assertEquals(2, data.size)
    }

    @Test
    fun trackError_shouldSendErrorData() {
        // Arrange
        val slot = slot<Map<String, String>>()
        mockDataError()
        mockDayWeek(WEDNESDAY)

        // Act
        viewModel.fetchData()

        // Assert
        verify(exactly = 1) { analytics.trackState(capture(slot)) }

        val data = slot.captured
        assertEquals("erro", data["status"])
        assertEquals(1, data.size)
    }

    @Test
    fun checkOpenDay_onSunday_shouldReturnCloseState() {
        // Arrange
        mockDataError()
        mockDayWeek(SUNDAY)

        // Act
        viewModel.fetchData()

        // Assert
        assertTrue(viewModel.state.value is ListState.Close)
    }

    @Test
    fun getOpeningInfo_onMonday_shouldReturnOpenInfo() {
        // Arrange
        mockDayWeek(MONDAY)

        // Act
        val result = viewModel.getOpeningInfo()

        // Assert
        assertEquals("Aberto das 8:00h às 18:00h", result)
    }

    @Test
    fun getOpeningInfo_onTuesday_shouldReturnOpenInfo() {
        // Arrange
        mockDayWeek(TUESDAY)

        // Act
        val result = viewModel.getOpeningInfo()

        // Assert
        assertEquals("Aberto das 8:00h às 18:00h", result)
    }

    @Test
    fun getOpeningInfo_onWednesday_shouldReturnOpenInfo() {
        // Arrange
        mockDayWeek(WEDNESDAY)

        // Act
        val result = viewModel.getOpeningInfo()

        // Assert
        assertEquals("Aberto das 8:00h às 18:00h", result)
    }

    @Test
    fun getOpeningInfo_onThursday_shouldReturnOpenInfo() {
        // Arrange
        mockDayWeek(THURSDAY)

        // Act
        val result = viewModel.getOpeningInfo()

        // Assert
        assertEquals("Aberto das 8:00h às 18:00h", result)
    }

    @Test
    fun getOpeningInfo_onFriday_shouldReturnOpenInfo() {
        // Arrange
        mockDayWeek(FRIDAY)

        // Act
        val result = viewModel.getOpeningInfo()

        // Assert
        assertEquals("Aberto das 8:00h às 18:00h", result)
    }

    @Test
    fun getOpeningInfo_onWeekDays_shouldReturnOpenInfo() {
        // Arrange
        val days = listOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)

        days.forEach {
            mockDayWeek(it)

            // Act
            val result = viewModel.getOpeningInfo()

            // Assert
            assertEquals("Aberto das 8:00h às 18:00h", result)
        }
    }

    @Test
    fun getOpeningInfo_onSaturday_shouldReturnClosedInfo() {
        // Arrange
        mockDayWeek(SATURDAY)

        // Act
        val result = viewModel.getOpeningInfo()

        // Assert
        assertEquals("Aberto das 8:00h às 12:00h", result)
    }

    @Test
    fun getOpeningInfo_onSunday_shouldReturnClosedInfo() {
        // Arrange
        mockDayWeek(SUNDAY)

        // Act
        val result = viewModel.getOpeningInfo()

        // Assert
        assertEquals("Estamos fechados", result)
    }

    private fun mockDataSuccessWithData() {
        every { business.fetchData() } returns Result.Success(resultMock)
    }

    private fun mockDataSuccessWithEmptyList() {
        every { business.fetchData() } returns Result.Success(listOf())
    }

    private fun mockDataError() {
        every { business.fetchData() } returns Result.Error
    }

    private fun mockDayWeek(day: Int) {
        val cal = Calendar.getInstance()
        cal.set(YEAR_2021, Calendar.JUNE, day)
        every { calendarHolder.getCalendar() } returns cal
    }

    private val resultMock = listOf(
        "X-Salda",
        "Coca-Cola"
    )

    companion object {
        private const val YEAR_2021 = 2021

        private const val SUNDAY = 6
        private const val MONDAY = 7
        private const val TUESDAY = 8
        private const val WEDNESDAY = 9
        private const val THURSDAY = 10
        private const val FRIDAY = 11
        private const val SATURDAY = 12
    }
}