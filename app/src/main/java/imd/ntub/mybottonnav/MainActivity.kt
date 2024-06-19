package imd.ntub.mybottonnav

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPagerAdapter = ViewPagerAdapter(this)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager2)
        val btmNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        viewPager.adapter = viewPagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                btmNav.selectedItemId = when (position) {
                    0 -> R.id.BtnLeft
                    1 -> R.id.BtnMid
                    else -> R.id.BtnRight
                }
            }
        })

        btmNav.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { it ->
            when (it.itemId) {
                R.id.BtnLeft -> {
                    viewPager.currentItem = 0
                    return@OnItemSelectedListener true
                }
                R.id.BtnMid -> {
                    viewPager.currentItem = 1
                    return@OnItemSelectedListener true
                }
                else -> {
                    viewPager.currentItem = 2
                    return@OnItemSelectedListener true
                }
            }
        })

        supportFragmentManager.setFragmentResultListener("editUserRequestKey", this) { _, bundle ->
            val user = bundle.getParcelable<User>("user")
            val position = bundle.getInt("position", -1)
            val editMode = bundle.getBoolean("editMode", false)

            if (editMode) {

                user?.let {
                    viewPager.currentItem = 1
                }
            } else {
                // 新增操作
                val secondFragment = SecondFragment.newInstance("", "", null, -1)
                supportFragmentManager.commit {
                    replace(R.id.viewPager2, secondFragment)
                    addToBackStack(null)
                }
            }
        }

        supportFragmentManager.setFragmentResultListener("editUserResultKey", this) { _, bundle ->
            val user = bundle.getParcelable<User>("user")
            val position = bundle.getInt("position", -1)
            user?.let {
                val firstFragment = supportFragmentManager.fragments.find { it is FirstFragment } as? FirstFragment
                firstFragment?.updateUser(it, position)
                viewPager.currentItem = 0
            }
        }

        supportFragmentManager.setFragmentResultListener("addUserRequestKey", this) { _, bundle ->
            val user = bundle.getParcelable<User>("user")
            user?.let {
                val firstFragment = supportFragmentManager.fragments.find { it is FirstFragment } as? FirstFragment
                firstFragment?.addUser(it)
                viewPager.currentItem = 0
            }
        }
    }
}

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int) = when (position) {
        0 -> FirstFragment.newInstance()
        1 -> SecondFragment.newInstance("", "", null, -1)
        2 -> ThirdFragment.newInstance("", "")
        else -> FirstFragment.newInstance()
    }
}
