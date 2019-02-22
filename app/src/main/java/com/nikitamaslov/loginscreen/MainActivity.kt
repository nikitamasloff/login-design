package com.nikitamaslov.loginscreen

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val KEY_LOGIN = "key_login"
        const val KEY_PASSWORD = "key_password"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_login.text = getString(R.string.login_template,intent.getStringExtra(KEY_LOGIN))
        main_password.text = getString(R.string.password_template,intent.getStringExtra(KEY_PASSWORD))

        main_button.setOnClickListener {
            Intent(this, SecurityActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(this)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
    }

}
