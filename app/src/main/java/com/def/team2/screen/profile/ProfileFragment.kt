package com.def.team2.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.def.team2.R
import com.def.team2.screen.profile.idol.ProfileIdolFragment
import com.def.team2.screen.profile.school.ProfileSchoolFragment
import com.def.team2.screen.profile.setting.ProfileSettingFragment
import com.def.team2.util.throttleClicks
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile_setting.*
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat


class ProfileFragment : Fragment(), ProfileContract.View {

    override lateinit var lifeCycleOwner: LifecycleOwner
    override lateinit var presenter: ProfileContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifeCycleOwner = this
        setLifecycle()
        presenter = ProfilePresenter(this).apply {
            start()
        }
    }

    override fun clickImageEdit() = profile_img_edit.throttleClicks()

    override fun clickSchool() = profile_school_btn_txt.throttleClicks()

    override fun clickIdol() = profile_idol_btn_txt.throttleClicks()

    override fun clickSetting() = profile_setting_btn_txt.throttleClicks()

    override fun changeFragment(status: ProfileContract.View.Status) {
        chnageView(status)
        when(status){
            ProfileContract.View.Status.SCHOOL -> replaceFragment(ProfileSchoolFragment())
            ProfileContract.View.Status.IDOL -> replaceFragment(ProfileIdolFragment())
            ProfileContract.View.Status.SETTING -> replaceFragment(ProfileSettingFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.profile_fragment, fragment).commit()
    }

    private fun chnageView(status: ProfileContract.View.Status){
        val bold = ResourcesCompat.getFont(requireContext(), R.font.spoqahansansbold)
        val light = ResourcesCompat.getFont(requireContext(), R.font.spoqahansanslight)

        when(status){
            ProfileContract.View.Status.SCHOOL -> {
                profile_school_btn_txt.typeface = bold
                profile_idol_btn_txt.typeface = light
                profile_setting_btn_txt.typeface = light
            }
            ProfileContract.View.Status.IDOL -> {
                profile_school_btn_txt.typeface = light
                profile_idol_btn_txt.typeface = bold
                profile_setting_btn_txt.typeface = light
            }
            ProfileContract.View.Status.SETTING -> {
                profile_school_btn_txt.typeface = light
                profile_idol_btn_txt.typeface = light
                profile_setting_btn_txt.typeface = bold
            }
        }
        profile_school_btn_line.isVisible = status ==ProfileContract.View.Status.SCHOOL
        profile_school_btn_txt.isSelected = status ==ProfileContract.View.Status.SCHOOL

        profile_idol_btn_line.isVisible = status ==ProfileContract.View.Status.IDOL
        profile_idol_btn_txt.isSelected = status ==ProfileContract.View.Status.IDOL

        profile_setting_btn_line.isVisible = status ==ProfileContract.View.Status.SETTING
        profile_setting_btn_txt.isSelected = status ==ProfileContract.View.Status.SETTING
    }

}