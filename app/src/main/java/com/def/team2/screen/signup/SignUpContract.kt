package com.def.team2.screen.signup

import com.def.team2.base.BaseRxPresenter
import com.def.team2.base.BaseRxView
import com.def.team2.network.Api
import com.def.team2.network.model.Idol
import com.def.team2.network.model.School
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface SignUpContract {

    interface View: BaseRxView<Presenter> {

        val isActive: Boolean

        val nickname: CharSequence

        val nicknameNextClick: Observable<Unit>

        val email: CharSequence

        val emailNextClick: Observable<Unit>

        val password: CharSequence

        val passwordNextClick: Observable<Unit>

        val school: CharSequence

        val schoolSelect: PublishSubject<School>

        val schoolChanges: Observable<CharSequence>

        val schoolNextClick: Observable<Unit>

        val idol: CharSequence

        val idolSelect: PublishSubject<Idol>

        val idolChanges: Observable<CharSequence>

        val signUpClick: Observable<Unit>

        val backButtonsClick: Observable<Unit>

        val preferenceChanges: Observable<String>

        fun getApiProvider(): Api

        fun showEmailUI()

        fun showPasswordUI()

        fun showSchoolUI()

        fun setSchoolText(school: CharSequence)

        fun addSchoolList(schools: List<School>)

        fun setSchoolListVisible(active: Boolean)

        fun showMyIdolUI()

        fun setIdolText(idol: CharSequence)

        fun addIdolList(idols: List<Idol>)

        fun setIdolListVisible(active: Boolean)

        fun showMainUI()

        fun deleteUI()

        fun showToast(msg: String)
    }

    interface Presenter: BaseRxPresenter {

        fun subscribeNickName()

        fun subscribeEmail()

        fun subscribePassword()

        fun subscribeSchool()

        fun subscribeIdol()

        fun subscribeSignUp()

        fun subscribePreference()

        fun subscribeBack()
    }
}