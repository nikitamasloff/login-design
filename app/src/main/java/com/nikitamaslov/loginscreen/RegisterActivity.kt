package com.nikitamaslov.loginscreen

import android.content.SharedPreferences
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val SIGN_UP_DELAY: Long = 1250
        const val REQUEST_CODE_FOR_SIGN_UP = 13
    }

    private val LOGIN_LOGIN by lazy { "login" }
    private val LOGIN_FACEBOOK by lazy { getString(R.string.facebook) }
    private val LOGIN_TWITTER by lazy { getString(R.string.twitter) }
    private val LOGIN_VK by lazy { getString(R.string.vk) }
    private val LOGIN_GOOGLE by lazy { getString(R.string.google) }
    private val LOGINS by lazy { listOf( LOGIN_LOGIN, LOGIN_FACEBOOK, LOGIN_TWITTER, LOGIN_VK, LOGIN_GOOGLE ) }

    private val prefs: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setTitle(R.string.title_sign_up)

        //input listener
        register_login_input.addOnTextChangedListener()
        register_password_input.addOnTextChangedListener()
        register_repeat_password_input.addOnTextChangedListener()
        register_email_input.addOnTextChangedListener()

        //drawable extensions
        register_login_input.addOnDrawableClickListener()
        register_password_input.addOnDrawableClickListener()
        register_repeat_password_input.addOnDrawableClickListener()
        register_email_input.addOnDrawableClickListener()

        //item icons
        register_login_icon.setOnClickListener(this)
        register_password_icon.setOnClickListener(this)
        register_repeat_password_icon.setOnClickListener(this)
        register_email_icon.setOnClickListener(this)

        //buttons
        register_button.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
    }

    override fun onClick(view: View?) {
        when (view?.id){
            R.id.register_login_icon -> register_login_input.requestFocus()
            R.id.register_password_icon -> register_password_input.requestFocus()
            R.id.register_repeat_password_icon -> register_repeat_password_input.requestFocus()
            R.id.register_email_input -> register_email_input.requestFocus()
            R.id.register_button -> validate()
        }
    }

    private fun validate(){
        val login = register_login_input.text.toString()
        val password = register_password_input.text.toString()
        val copyPassword = register_repeat_password_input.text.toString()
        //val email = register_email_input.text.toString()
        if (login.isEmpty()){
            register_login_input.requestFocus()
            return
        }
        if (password.isEmpty()){
            register_password_input.requestFocus()
            return
        }
        if (copyPassword.isEmpty()){
            register_repeat_password_input.requestFocus()
            return
        }
        when {
            isExist(login) -> {
                updateMessage(false,true,null)
                register_login_input.requestFocus()
            }
            password != copyPassword -> {
                updateMessage(false,null,false)
                register_password_input.requestFocus()
            }
            else -> {
                updateMessage(true,false,false)
                createProfile(login,password)
                handler.postDelayed({
                    finish()
                }, SIGN_UP_DELAY)
            }
        }
    }

    private fun updateMessage(success: Boolean, loginExist: Boolean?, passwordsEquals: Boolean?){
        register_message.run {
            visibility = View.VISIBLE
            if (success){
                setTextColor(ContextCompat.getColor(context, R.color.message_text_color_success))
                setText(R.string.message_success)
                return
            } else {
                setTextColor(ContextCompat.getColor(context, R.color.message_text_color_failure))
            }
            if (loginExist == true){
                setText(R.string.message_login_already_exist)
                return
            }
            if (passwordsEquals == false){
                setText(R.string.message_different_passwords)
                return
            }
        }
    }

    private fun createProfile(login: String, password: String){
        prefs.edit().putString(login,password).apply()
    }

    /**
     * pair in sharedPreferences: login is key, password is value
     */
    private fun isExist(login: String) =
            LOGINS.contains(login) || prefs.getString(login,null) != null

    private fun EditText.addOnTextChangedListener(){

        val DRAWABLE_LEFT = 0
        val DRAWABLE_TOP = 1
        val DRAWABLE_RIGHT = 2
        val DRAWABLE_BOTTOM = 3

        val ALPHA_INVISIBLE = 0
        val ALPHA_VISIBLE = 255

        if (id == R.id.register_login_input || id == R.id.register_password_input || id == R.id.register_repeat_password_input || id == R.id.register_email_input){
            this.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(line: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (compoundDrawables[DRAWABLE_RIGHT] == null){
                        when (id){
                            R.id.register_login_input, R.id.register_email_input ->
                                setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.remove_black_16dp,0)
                            R.id.register_password_input, R.id.register_repeat_password_input ->
                                setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.visible_black_20dp,0)
                        }
                    }
                    register_message.run {
                        if (visibility == View.VISIBLE){
                            visibility = View.INVISIBLE
                        }
                    }
                    compoundDrawables[DRAWABLE_RIGHT]?.run {
                        alpha = if (line?.isEmpty() != false){
                            ALPHA_INVISIBLE
                        } else {
                            ALPHA_VISIBLE
                        }
                    }
                }
            })
        }
    }

    private fun EditText.addOnDrawableClickListener(){

        val DRAWABLE_LEFT = 0
        val DRAWABLE_TOP = 1
        val DRAWABLE_RIGHT = 2
        val DRAWABLE_BOTTOM = 3

        val PASSWORD_INVISIBLE = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        val PASSWORD_VISIBLE = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_CLASS_TEXT

        val TYPEFACE = Typeface.MONOSPACE

        val setPasswordVisibility: EditText.(Boolean) -> Unit = { visible ->
            inputType = if (visible){
                isCursorVisible = false
                PASSWORD_VISIBLE
            } else {
                isCursorVisible = true
                PASSWORD_INVISIBLE
            }
            typeface = TYPEFACE
        }

        when (this.id){
            R.id.register_login_input, R.id.register_email_input -> {
                setOnTouchListener { view, motionEvent ->
                    if (compoundDrawables[DRAWABLE_RIGHT] != null){
                        when {
                            motionEvent.action == MotionEvent.ACTION_DOWN -> {
                                if (motionEvent.rawX in right - compoundDrawables[DRAWABLE_RIGHT].bounds.width()..right) {
                                    tag = true
                                    return@setOnTouchListener true
                                }
                            }
                            motionEvent.action == MotionEvent.ACTION_UP ->
                                if (tag == true) {
                                    setText("")
                                    tag = null
                                }

                        }
                    }
                    return@setOnTouchListener false
                }
            }
            R.id.register_password_input, R.id.register_repeat_password_input -> {
                setOnTouchListener { view, motionEvent ->
                    if (compoundDrawables[DRAWABLE_RIGHT] != null){
                        when {
                            motionEvent.action == MotionEvent.ACTION_DOWN ->
                                if (motionEvent.rawX in right - compoundDrawables[DRAWABLE_RIGHT].bounds.width()..right) {
                                    setPasswordVisibility(true)
                                    return@setOnTouchListener true
                                }
                            motionEvent.action == MotionEvent.ACTION_UP -> {
                                setPasswordVisibility(false)
                            }
                        }
                    }
                    return@setOnTouchListener false
                }
            }
        }
    }

}
