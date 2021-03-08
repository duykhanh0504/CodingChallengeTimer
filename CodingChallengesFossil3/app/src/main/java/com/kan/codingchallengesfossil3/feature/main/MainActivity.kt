package com.kan.codingchallengesfossil3.feature.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.kan.codingchallengesfossil3.R
import com.kan.codingchallengesfossil3.base.BaseActivity
import com.kan.codingchallengesfossil3.di.HasComponent
import com.kan.codingchallengesfossil3.extension.viewModel
import com.kan.codingchallengesfossil3.feature.navigation.Navigator
import javax.inject.Inject


class MainActivity : BaseActivity(), HasComponent<MainComponent> {


    override fun layoutId(): Int = R.layout.activity_main

    private val mainComponent: MainComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerMainComponent.builder()
            .applicationComponent(applicationComponent())
            .mainModule(MainModule()).build()
    }

    override fun inject() = getComponent().inject(this)

    override fun getComponent() = mainComponent

    private lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.timer_settings -> {
                navigator.showMenuActivity(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.app_short_name)
        navigator.showPickerFragment(this, R.id.flMainFragment)
    }

    private fun initViewModel() {
        mainViewModel = this.viewModel(factory = viewModelFactory) {
            Unit
        }
    }

}