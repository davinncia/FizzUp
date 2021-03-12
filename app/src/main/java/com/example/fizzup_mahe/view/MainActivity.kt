package com.example.fizzup_mahe.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fizzup_mahe.R
import com.example.fizzup_mahe.di.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private var dataSourceMessage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Disabling night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        supportActionBar?.title = getString(R.string.exercises_list)

        // View binding
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val networkImageView = findViewById<ImageView>(R.id.iv_network_main).apply {
            setOnClickListener {
                Toast.makeText(this@MainActivity, dataSourceMessage, Toast.LENGTH_SHORT).show()
            }
        }

        // Recycler view
        val adapter = ExerciseAdapter()
        initRecyclerView(adapter)

        // Observing view model
        val viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(applicationContext)
        )[MainViewModel::class.java]

        viewModel.dataFromServer.observe(this, { fromServer ->
            val imageResource = if (fromServer) {
                dataSourceMessage = getString(R.string.data_from_cloud)
                R.drawable.ic_cloud
            } else {
                dataSourceMessage = getString(R.string.data_from_cache)
                R.drawable.ic_folder
            }

            networkImageView.setImageResource(imageResource)
        })

        viewModel.exercises.observe(this, {
            progressBar.visibility = View.GONE
            adapter.updateData(it)
        })
    }

    private fun initRecyclerView(adapter: ExerciseAdapter) {
        findViewById<RecyclerView>(R.id.recycler_view).apply {
            this.adapter = adapter
            this.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

}
