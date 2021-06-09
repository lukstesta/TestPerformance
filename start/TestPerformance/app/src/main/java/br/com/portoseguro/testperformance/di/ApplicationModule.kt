package br.com.portoseguro.testperformance.di

import br.com.portoseguro.testperformance.business.Business
import br.com.portoseguro.testperformance.business.BusinessImpl
import br.com.portoseguro.testperformance.infrastructure.Analytics
import br.com.portoseguro.testperformance.infrastructure.CalendarHolder
import br.com.portoseguro.testperformance.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
}

val businessModule = module {
    factory { BusinessImpl() } bind Business::class
}

val infrastructureModule = module {
    factory { CalendarHolder() }
    factory { Analytics() }
}

val appModule = listOf(
    viewModelModule,
    businessModule,
    infrastructureModule
)
