package br.com.portoseguro.testperformance.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.portoseguro.testperformance.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: MainAdapter

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureRecyclerView()
        bindViews()

        binding.openingStatus.text = viewModel.getOpeningInfo()

        viewModel.fetchData()
    }

    private fun configureRecyclerView() {
        adapter = MainAdapter()
        binding.productsList.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        binding.productsList.addItemDecoration(dividerItemDecoration)
    }

    private fun bindViews() {
        viewModel.state.observe(this, Observer { state ->
            if (state is ListState.Success) {
                adapter.update(state.names)
            }
        })
    }
}