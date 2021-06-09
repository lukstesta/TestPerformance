package br.com.portoseguro.testperformance.business

import br.com.portoseguro.testperformance.infrastructure.Result

class BusinessImpl : Business {
    private val response = listOf(
        "Hamburguer", "X-Salada", "X-Frango", "X-Bacon", "Coca-Cola", "Guaraná", "Suco"
    )

    override fun fetchData(): Result<List<String>> {
        return Result.Success(response)
    }
}

interface Business {
    fun fetchData(): Result<List<String>>
}