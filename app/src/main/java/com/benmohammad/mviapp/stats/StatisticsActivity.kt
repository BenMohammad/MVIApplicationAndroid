package com.benmohammad.mviapp.stats

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.benmohammad.mviapp.R
import com.benmohammad.mviapp.util.addFragmentToActivity
import com.google.android.material.navigation.NavigationView

class StatisticsActivity: AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stats_act)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.run {
            setTitle(R.string.statistics_title)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark)

        findViewById<NavigationView>(R.id.nav_view)?.let {setUpDrawerConetnt(it)}

        if(supportFragmentManager.findFragmentById(R.id.contentFrame) == null) {
            addFragmentToActivity(supportFragmentManager, StatisticsFragment(), R.id.contentFrame)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home  -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setUpDrawerConetnt(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener {
            menuItem -> when(menuItem.itemId) {
            R.id.list_navigation_menu_item -> NavUtils.navigateUpFromSameTask(this@StatisticsActivity)
            R.id.statistics_navigation_menu_item -> {}
        }

            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }
}