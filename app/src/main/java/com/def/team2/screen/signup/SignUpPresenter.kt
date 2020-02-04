package com.def.team2.screen.signup

import android.util.Log
import com.def.team2.network.model.Location
import com.def.team2.network.model.School
import com.def.team2.util.isEmail
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import retrofit2.HttpException

class SignUpPresenter(
    val view: SignUpContract.View,
    private val signUpInteractor: SignUpInteractor
) : SignUpContract.Presenter {

    private var idolId: Long? = 7 // 7: 방탄, 17: 에이비식스, 18: 워너원

    override fun start() {
        subscribeNickName()
        subscribeEmail()
        subscribePassword()
        subscribeSchool()
        subscribeIdol()
        subscribeSignUp()
        subscribePreference()
        subscribeBack()
    }

    override val disposables: CompositeDisposable = CompositeDisposable()

    override fun subscribeNickName() {
        view.nicknameNextClick
            .map { view.nickname }
            .filter {
                if (it.isNotEmpty()) {
                    return@filter true
                }
                view.showToast("Invalid Nickname")
                return@filter false
            }
            .subscribe {
                view.showEmailUI()
            }.bindUntilClear()
    }

    override fun subscribeEmail() {
        view.emailNextClick
            .map { view.email }
            .filter { email ->
                if (email.isEmail()) {
                    return@filter true
                }
                view.showToast("Invalid Email")
                return@filter false
            }
//            .switchMapSingle { signUpInteractor.checkEmailValidate(it.toString()) }
            .observeOn(AndroidSchedulers.mainThread())
            .retry { _, e ->
                if (e is HttpException) {
                    if (e.code() == 403) {
                        view.showToast("Email is Duplicated")
                    } else {
                        view.showToast("Unknown Error")
                    }
                }
                true
            }
            .subscribe { view.showPasswordUI() }
            .bindUntilClear()
    }

    override fun subscribePassword() {
        view.passwordNextClick
            .map { view.password }
            .filter {password ->
                if (password.isNotEmpty()) {
                    return@filter true
                }
                view.showToast("Invalid Password")
                return@filter false
            }
            .subscribe {
                view.showSchoolUI()
            }.bindUntilClear()
    }

    override fun subscribeSchool() {

        subscribeSchoolChanges()
        view.schoolSelect.onNext(School(0, "", "", Location(0.0,0.0), School.Level.ELEMENT, ""))

        view.schoolNextClick
            .map { Pair(view.school, signUpInteractor.schoolId) }
            .filter {
                if (it.first.isNotEmpty() && it.second != null) {
                    return@filter true
                }
                view.showToast("Invalid School")
                return@filter false
            }
            .subscribe { view.showMyIdolUI() }
            .bindUntilClear()

    }

    private fun subscribeSchoolChanges() {

        Observable.merge(view.schoolChanges, view.schoolSelect)
            .doOnNext {
                if (it is School) {
                    view.setSchoolListVisible(false)
                    view.setSchoolText(it.name)
                    signUpInteractor.schoolId = it.id
                }
            }.filter { it is CharSequence }
            .withLatestFrom(
                view.schoolSelect,
                BiFunction { t1: Any, t2: School ->
                    Pair(t1.toString(), t2.name)
                }
            ).filter { it.first != it.second }
            .map { it.first }
            .switchMapSingle {
                if (it.length >= 2) {
                    signUpInteractor.getSchoolList(it)
                } else {
                    Single.just(listOf())
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isEmpty()) {
                    view.setSchoolListVisible(false)
                } else {
                    view.setSchoolListVisible(true)
                }
                signUpInteractor.schoolId = null
                view.addSchoolList(it)
            }
            .bindUntilClear()
    }

    override fun subscribeIdol() {

        subscribeIdolChanges()
//        view.idolSelect.onNext(Pair(0, ""))
        view.setIdolListVisible(false)
    }

    private fun subscribeIdolChanges() {
//        view.idolChanges
//            .withLatestFrom(
//                view.idolSelect,
//                BiFunction { t1: CharSequence, t2: Pair<Int, String> ->
//                    Pair(t1, t2.toString())
//                }
//            )
//            .filter {
//                it.first != it.second
//            }
//            .map {
//                it.first
//            }.filter {
//                it.length >= 2
//            }.observeOn(Schedulers.io())
//            .flatMap {
//                // Todo api 요청
//
//                // FixMe 더미 데이터 제거
//                when (it) {
//                    "abcd" -> return@flatMap Observable.just(listOf("풍동", "풍동고등학교", "풍동중학교", "풍동 뭐시기"))
//                    "defg" -> return@flatMap Observable.just(listOf("대치", "대치고등학교", "대치중학교", "대치대치", "음...대치?"))
//                    "qwer" -> return@flatMap Observable.just(listOf("강남", "강남고등학교", "강남중학교", "강남해커스", "강남대성"))
//                    else -> return@flatMap Observable.just(listOf<String>())
//                }
//            }
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnNext {
//                if (it.isEmpty()) {
//                    view.setIdolListVisible(false)
//                } else {
//                    view.setIdolListVisible(true)
//                }
//            }
//            .doOnError {
//                // 에러 발생 처리
//            }
//            .subscribe {
////                view.addIdolList(it)
//            }
//            .bindUntilClear()
    }

    override fun subscribeSignUp() {
        view.signUpClick
            .filter { signUpInteractor.hasSchoolIdAndIdolId() }
            .switchMapSingle {
                signUpInteractor.signUp(
                    view.email.toString(),
                    view.nickname.toString(),
                    view.password.toString(),
                    signUpInteractor.schoolId!!,
                    idolId!!
                )
            }.observeOn(AndroidSchedulers.mainThread())
            .retry { _, e ->
                Log.e("error", "error, message: ${e.message}")
                true
            }
            .subscribe {
                signUpInteractor.saveToken(it)
            }.bindUntilClear()
    }

    override fun subscribePreference() {
        view.preferenceChanges
            .filter { it.isNotEmpty() }
            .flatMapSingle {
                // MyInfo 가져온다.
                return@flatMapSingle Single.just("myInfo 올꺼임")
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                view.showMainUI()
            }
            .bindUntilClear()
    }

    override fun subscribeBack() {
        view.backButtonsClick
            .subscribe {
                view.deleteUI()
            }.bindUntilClear()
    }
}