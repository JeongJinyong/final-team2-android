package com.def.team2.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.def.team2.R
import com.def.team2.util.throttleClicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment(), ProfileContract.View {

    override lateinit var lifeCycleOwner: LifecycleOwner
    override lateinit var presenter: ProfileContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifeCycleOwner = this
        presenter = ProfilePresenter(this).apply {
            start()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun clickImageEdit() = profile_img_edit.throttleClicks()

    override fun clickSchool() = profile_school_btn.throttleClicks()

    override fun clickIdol() = profile_idol_btn.throttleClicks()

    override fun clickSetting() = profile_setting_btn.throttleClicks()

    override fun changeFragment(status: ProfileContract.View.Status) {

    }

}