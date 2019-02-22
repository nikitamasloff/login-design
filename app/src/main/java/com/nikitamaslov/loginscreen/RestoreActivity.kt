package com.nikitamaslov.loginscreen

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_restore.*

class RestoreActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val RESTORE_DELAY: Long = 1250
        const val REQUEST_CODE_FOR_FORGOT_PASSWORD = 98
    }

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore)
        setTitle(R.string.title_restore)

        restore_button.setOnClickListener(this)

        restore_input.addOnTextChangedListener()
        restore_input.addOnDrawableClickListener()

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
            R.id.restore_icon -> restore_input.requestFocus()
            R.id.restore_button -> {
                validate()
            }
        }
    }

    private fun validate(){
        val email = restore_input?.text?.toString()
        if (email?.isEmpty() != false){
            restore_button.requestFocus()
            return
        }
        restore_message.visibility = View.VISIBLE
        handler.postDelayed({
            finish()
        }, RESTORE_DELAY)
    }

    private fun EditText.addOnTextChangedListener(){

        val DRAWABLE_LEFT = 0
        val DRAWABLE_TOP = 1
        val DRAWABLE_RIGHT = 2
        val DRAWABLE_BOTTOM = 3

        val ALPHA_INVISIBLE = 0
        val ALPHA_VISIBLE = 255

        if (id == R.id.restore_input){
            this.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(line: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (compoundDrawables[DRAWABLE_RIGHT] == null){
                        when (id){
                            R.id.restore_input ->
                                setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.remove_black_16dp,0)
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

        val TYPEFACE = Typeface.MONOSPACE

        when (this.id){
            R.id.restore_input -> {
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
        }
    }

}
