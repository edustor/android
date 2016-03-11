package ru.wutiarn.edustor.android.activity

import android.os.Bundle
import android.view.View
import com.hannesdorfmann.mosby.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_base.*
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.fragment.LessonDetailsFragment
import ru.wutiarn.edustor.android.presenter.LessonDetailsActivityPresenter
import ru.wutiarn.edustor.android.view.LessonDetailsActivityView

/**
 * Created by wutiarn on 11.03.16.
 */
class LessonDetailsActivity : MvpActivity<LessonDetailsActivityView, LessonDetailsActivityPresenter>(), LessonDetailsActivityView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_base)
        setSupportActionBar(toolbar)

        scan_exists.visibility = View.GONE

        val lessonDetailsFragment = LessonDetailsFragment()
        val lessonBundle = Bundle()

        val uuid = intent.getStringExtra("uuid")
        lessonBundle.putString("uuid", uuid)

        val id = intent.getStringExtra("id")
        lessonBundle.putString("id", id)
        lessonDetailsFragment.arguments = lessonBundle


        supportFragmentManager.beginTransaction()
                .add(R.id.main_container, lessonDetailsFragment)
                .commit()

        //        TODO: Scan new fab
    }

    override fun createPresenter(): LessonDetailsActivityPresenter {
        return LessonDetailsActivityPresenter()
    }


}