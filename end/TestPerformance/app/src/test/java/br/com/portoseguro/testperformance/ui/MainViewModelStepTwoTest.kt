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
class MainViewModelStepTwoTest {

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

    private val slot = slot<Map<String, String>>()

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        viewModel = MainViewModel(business, analytics, calendarHolder)

        val cal = Calendar.getInstance()
        cal.set(2021, Calendar.JUNE, 9)
        every { calendarHolder.getCalendar() } returns cal
    }

    @Test
    fun testLoadData() {
        every { business.fetchData() } returns Result.Success(resultMock)

        viewModel.fetchData()

        assertTrue(viewModel.state.value is ListState.Success)
        val state = viewModel.state.value as ListState.Success

        assertEquals("X-Salda", state.names[0])
        assertEquals("Coca-Cola", state.names[1])
        assertEquals(2, state.names.size)

        verify(exactly = 1) { analytics.trackState(capture(slot)) }
        val data = slot.captured
        assertEquals("sucesso", data["status"])
        assertEquals("2", data["quantidade"])
        assertEquals(2, data.size)
    }

    @Test
    fun testEmptyResult() {
        every { business.fetchData() } returns Result.Success(listOf())
        val cal = Calendar.getInstance()
        cal.set(2021, Calendar.JUNE, 9)
        every { calendarHolder.getCalendar() } returns cal

        viewModel.fetchData()

        assertTrue(viewModel.state.value is ListState.Empty)

        verify(exactly = 1) { analytics.trackState(capture(slot)) }
        val data = slot.captured
        assertEquals("sucesso", data["status"])
        assertEquals("0", data["quantidade"])
        assertEquals(2, data.size)
    }

    @Test
    fun testApiError() {
        every { business.fetchData() } returns Result.Error
        val cal = Calendar.getInstance()
        cal.set(2021, Calendar.JUNE, 9)
        every { calendarHolder.getCalendar() } returns cal

        viewModel.fetchData()

        assertTrue(viewModel.state.value is ListState.Error)

        verify(exactly = 1) { analytics.trackState(capture(slot)) }
        val data = slot.captured
        assertEquals("erro", data["status"])
        assertEquals(1, data.size)
    }

    @Test
    fun checkIsClosed() {
        every { business.fetchData() } returns Result.Error
        val cal = Calendar.getInstance()
        cal.set(2021, Calendar.JUNE, 6)
        every { calendarHolder.getCalendar() } returns cal

        viewModel.fetchData()

        assertTrue(viewModel.state.value is ListState.Close)
    }

    @Test
    fun testOpenInfo() {
        val cal = Calendar.getInstance()
        cal.set(2021, Calendar.JUNE, 9)
        every { calendarHolder.getCalendar() } returns cal

        val result = viewModel.getOpeningInfo()

        assertEquals("Aberto das 8:00h às 18:00h", result)
    }

    @Test
    fun testCloseMiddayInfo() {
        val cal = Calendar.getInstance()
        cal.set(2021, Calendar.JUNE, 12)
        every { calendarHolder.getCalendar() } returns cal

        val result = viewModel.getOpeningInfo()

        assertEquals("Aberto das 8:00h às 12:00h", result)
    }

    @Test
    fun testClosedInfo() {
        val cal = Calendar.getInstance()
        cal.set(2021, Calendar.JUNE, 6)
        every { calendarHolder.getCalendar() } returns cal

        val result = viewModel.getOpeningInfo()

        // Assert
        assertEquals("Estamos fechados", result)
    }

    private val resultMock = listOf(
        "X-Salda",
        "Coca-Cola"
    )
}