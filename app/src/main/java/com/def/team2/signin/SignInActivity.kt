package com.def.team2.signin

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.def.team2.MainActivity
import com.def.team2.R
import com.def.team2.base.BaseActivity
import com.def.team2.signup.SignUpFragment
import com.def.team2.util.sharedPreferences
import com.def.team2.util.throttleClicks
import com.def.team2.util.toast
import com.f2prateek.rx.preferences2.RxSharedPreferences
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity(), SignInContract.View {

    override lateinit var lifeCycleOwner: LifecycleOwner
    override lateinit var presenter: SignInContract.Presenter
    private val rxPreferences by lazy {
        RxSharedPreferences.create(this.sharedPreferences())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        lifeCycleOwner = this
        presenter = SignInPresenter(this@SignInActivity, SaveToken(this)).apply {
            start()
        }
    }

    override val isActive: Boolean
        get() = !isFinishing

    override fun getSignUpTapClicks(): Observable<Unit> = tv_signin_tab_signup.throttleClicks()

    override fun getSignInButtonClicks(): Observable<Unit> = btn_signin.throttleClicks()

    override fun getEmailText(): CharSequence = et_email.text

    override fun getPasswordText(): CharSequence = et_password.text

    override fun getPreference(): Observable<String> =
        rxPreferences.getString("token").asObservable()

    override fun setLoadingIndicator(active: Boolean) {

    }

    override fun showSignUpUI() {
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.signup_fragment_slide_from_bottom,
                R.anim.signup_fragment_slide_to_top,
                R.anim.signup_fragment_slide_from_top,
                R.anim.signup_fragment_slide_to_bottom
            )
        }.replace(R.id.fl_signup_content, SignUpFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    override fun showMainUI() {
        val intent = Intent(this@SignInActivity, MainActivity::class.java)
        startActivity(intent)
    }

    override fun showToast(msg: String) {
        this.toast(msg)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
