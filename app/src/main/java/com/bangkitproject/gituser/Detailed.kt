package com.bangkitproject.gituser


//import com.bangkitproject.gituser.FollowersFragment.Companion.EXTRA_DATA
//import com.bangkitproject.gituser.FollowingFragment.Companion.EXTRA_DATA
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.hdodenhof.circleimageview.CircleImageView


class Detailed : AppCompatActivity(){



    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
        const val EXTRA_DATAS = "this is the place"
        val TAG = Detailed::class.java.simpleName
        private var list: ArrayList<Account> = arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_layout)

        val name: TextView = findViewById(R.id.detailNama)
        val username: TextView = findViewById(R.id.detailUsername)
        val company: TextView = findViewById(R.id.detailCompany)
        val repository: TextView = findViewById(R.id.detailRepository)
        val following: TextView = findViewById(R.id.detailFollowing)
        val followers: TextView = findViewById(R.id.detailFollowers)
        var url_followers : String = ""
        var url_followings : String = ""

        val imgPhoto: CircleImageView = findViewById(R.id.detailAvatar)

        val listdata: Account? =intent.getParcelableExtra(EXTRA_DATAS)
        if (listdata != null) {
                name.text = listdata.name
                username.text = listdata.username
                company.text = "${listdata.company} , ${listdata.location}"
                repository.text = "${listdata.repository}  total repo"
                following.text = "${listdata.following} following"
                followers.text = "${listdata. followers}  followers"
            ShowViewPager(listdata.username.toString())
                this.title = listdata.username


            Glide.with(this)
                .load(listdata.avatar)
                .into(imgPhoto)

        }



    }

    private fun ShowViewPager(name: String) {
        val sectionsPagerAdapter = SectionsPagerAdapter(this,name)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabLayout)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f


    }

}