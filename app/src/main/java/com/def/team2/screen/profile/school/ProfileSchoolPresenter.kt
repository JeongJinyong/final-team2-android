package com.def.team2.screen.profile.school

import com.def.team2.base.UserData
import io.reactivex.disposables.CompositeDisposable

class ProfileSchoolPresenter(private val view: ProfileSchoolContract.View) : ProfileSchoolContract.Presenter {

    override val disposables: CompositeDisposable = CompositeDisposable()
    override fun start() {
        subscribeSchoolInfo()
    }

    override fun subscribeSchoolInfo() {
//        view.setSchoolInfo() //TODO School Data Class 만들기
    }

    fun subscribeSearch() {
        val search = "B"
        Single.zip(listOf(view.getApiProvider().searchIdolList(search), view.getApiProvider().searchSchoolList(search, 5)))
        {
            val list = mutableListOf<String>()
            it.forEachIndexed { index, any ->
                e("$index : $any")
                (any as List<*>).forEachIndexed { index, any ->
                    e("$index : ${any.toString()}")
                    when (any) {
                        is Idol -> list.add(any.name)
                        is School -> list.add(any.name)
                    }
                }
    }
}