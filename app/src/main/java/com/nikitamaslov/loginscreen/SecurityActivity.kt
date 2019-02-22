package com.nikitamaslov.loginscreen

import android.content.Intent
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
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_security.*

class SecurityActivity : AppCompatActivity(), View.OnClickListener,
    ShareDialog.OnLoginViaShared {

    companion object {
        private const val PASSWORD = "password"
        const val LOG_IN_DELAY: Long = 1000
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
        setContentView(R.layout.activity_security)
        setTitle(R.string.title_log_in)

        //input listener
        login_input.addOnTextChangedListener()
        password_input.addOnTextChangedListener()

        //drawable extensions
        login_input.addOnDrawableClickListener()
        password_input.addOnDrawableClickListener()

        //item icons
        login_icon.setOnClickListener(this)
        password_icon.setOnClickListener(this)

        //buttons
        log_in.setOnClickListener(this)
        sign_up.setOnClickListener(this)
        forgot_password.setOnClickListener(this)

        //bottom icons
        facebook.setOnClickListener()
        twitter.setOnClickListener()
        vk.setOnClickListener()
        google.setOnClickListener()

    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        login_input.setText("")
        password_input.setText("")
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
    }

    override fun onClick(view: View?) {
        when (view?.id){
            R.id.login_icon -> login_input.requestFocus()
            R.id.password_icon -> password_input.requestFocus()
            R.id.log_in -> {
                validate()
            }
            R.id.sign_up -> {
                Intent(this, RegisterActivity::class.java).apply {
                    startActivityForResult(this,
                        RegisterActivity.REQUEST_CODE_FOR_SIGN_UP
                    )
                }
            }
            R.id.forgot_password -> {
                Intent(this, RestoreActivity::class.java).apply {
                    startActivityForResult(this,
                        RestoreActivity.REQUEST_CODE_FOR_FORGOT_PASSWORD
                    )
                }
            }
        }
    }

    private fun ImageButton.setOnClickListener(){
        this.setOnClickListener {view ->
            val type: Int? = when (view.id){
                R.id.facebook -> ShareDialog.FACEBOOK
                R.id.twitter -> ShareDialog.TWITTER
                R.id.vk -> ShareDialog.VK
                R.id.google -> ShareDialog.GOOGLE
                else -> null
            }
            type?.let {
                val dialog = ShareDialog.instance(it)
                dialog.show(supportFragmentManager,null)
            }
        }
    }

    override fun onLoginViaShared(login: String) {
        login_input.setText(login)
        password_input.setText(PASSWORD)
        validate()
    }

    private fun validate(){
        val login = login_input.text.toString()
        val password = password_input.text.toString()
        if (login.isEmpty()){
            login_input.requestFocus()
            return
        }
        if (password.isEmpty()){
            password_input.requestFocus()
            return
        }
        if (isSuit(login,password)){
            updateMessage(true)
            handler.postDelayed({
                Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(MainActivity.KEY_LOGIN,login)
                    putExtra(MainActivity.KEY_PASSWORD,password)
                    startActivity(this)
                }
            }, LOG_IN_DELAY)
        } else {
            updateMessage(false)
        }
    }

    private fun updateMessage(success: Boolean){
        log_in_message.run {
            visibility = View.VISIBLE
            text = if (success){
                setTextColor(ContextCompat.getColor(context, R.color.message_text_color_success))
                getString(R.string.message_success)
            } else {
                setTextColor(ContextCompat.getColor(context, R.color.message_text_color_failure))
                getString(R.string.message_error)
            }
        }
    }

    /**
     * pair in sharedPreferences: login is key, password is value
     */
    private fun isSuit(login: String, password: String) =
            (password == PASSWORD && LOGINS.contains(login))
                    || (prefs.getString(login,null) ?: false) == password

    private fun EditText.addOnTextChangedListener(){

        val DRAWABLE_LEFT = 0
        val DRAWABLE_TOP = 1
        val DRAWABLE_RIGHT = 2
        val DRAWABLE_BOTTOM = 3

        val ALPHA_INVISIBLE = 0
        val ALPHA_VISIBLE = 255

        if (id == R.id.login_input || id == R.id.password_input){
            this.addTextChangedListener(object: TextWatcher{
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(line: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (compoundDrawables[DRAWABLE_RIGHT] == null){
                        when (id){
                            R.id.login_input ->
                                setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.remove_black_16dp,0)
                            R.id.password_input ->
                                setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.visible_black_20dp,0)
                        }
                    }
                    log_in_message.run {
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
            R.id.login_input -> {
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
            R.id.password_input -> {
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
