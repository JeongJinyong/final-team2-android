package com.def.team2.screen.signup

import android.content.Context
import android.content.SharedPreferences
import com.def.team2.base.UserData
import com.def.team2.network.Api
import com.def.team2.network.model.IdolGroup
import com.def.team2.network.model.School
import com.def.team2.network.model.SignUpRequest
import com.def.team2.util.KEY_TOKEN
import com.def.team2.util.idolKingdomApi
import com.def.team2.util.sharedPreferences
import com.f2prateek.rx.preferences2.RxSharedPreferences
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class SignUpInteractor(context: Context) {

    var schoolId: Long? = null
    var idolId: Long? = null

    private val idolKingdomApi: Api by lazy {
        context.idolKingdomApi
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.sharedPreferences()
    }

    fun hasSchoolIdAndIdolId(): Boolean {
        return schoolId != null && idolId != null
    }

    fun checkEmailValidate(email: String): Single<Unit> =
        idolKingdomApi
            .checkEmailDuplicated(mapOf(Pair("email", email)))
            .toSingleDefault(Unit)
            .subscribeOn(Schedulers.io())

    fun getSchoolList(keyword: String): Single<List<School>> =
        idolKingdomApi
            .searchSchoolList(keyword, 5)
            .onErrorResumeNext { Single.just(listOf()) }
            .subscribeOn(Schedulers.io())

    fun getIdolList(keyword: String): Single<List<IdolGroup>> =
        idolKingdomApi
            .searchIdolList(keyword)
            .map { it.idols }
            .onErrorResumeNext { Single.just(listOf()) }
            .subscribeOn(Schedulers.io())

    fun signUp(email: String, nickName: String, password: String, schoolId: Long, idolId: Long): Single<String> =
        idolKingdomApi
            .signUp(SignUpRequest(email, nickName, password, listOf(schoolId), listOf(idolId)))
            .map { it.token }
            .subscribeOn(Schedulers.io())

    fun setMyInfo() =
        idolKingdomApi
            .getMe()
            .doOnSuccess { UserData.user = it }
            .flatMap { user ->
                idolKingdomApi
                    .getSchool(user.schools)
                    .map { schoolList -> UserData.school = schoolList.first() }
                    .map { user }
            }
            .flatMap {
                Flowable.fromIterable(it.idols)
                    .flatMapSingle {id ->
                        idolKingdomApi
                            .getIdol(id)
                            .map { idol -> UserData.idolList.add(idol) }
                    }.toList()
            }
            .subscribeOn(Schedulers.io())

    fun saveToken(token: String) {
        sharedPreferences
            .edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun savedTokenChanges(): Observable<String> =
        RxSharedPreferences.create(sharedPreferences)
            .getString(KEY_TOKEN).asObservable()
            .filter { it.isNotEmpty() }
}