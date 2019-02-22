package com.nikitamaslov.loginscreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.share_dialog.*
import kotlinx.android.synthetic.main.share_dialog.view.*

class ShareDialog: DialogFragment(), View.OnClickListener {

    interface OnLoginViaShared{
        fun onLoginViaShared(login: String)
    }

    companion object {
        const val LOADING_DELAY: Long = 3000
        const val CLOSING_DELAY: Long = 1500

        const val DIM_COEFFICIENT: Float = 0.6f

        const val FACEBOOK = 5
        const val TWITTER = 6
        const val VK = 7
        const val GOOGLE = 8
        private const val TYPE_KEY = "type_key"
        fun instance(type: Int): ShareDialog {
            val fragment = ShareDialog()
            val args = Bundle()
            args.putInt(TYPE_KEY, type)
            fragment.arguments = args
            return fragment
        }
    }

    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private lateinit var callback: OnLoginViaShared

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE,0)
        dialog?.window?.attributes?.dimAmount =
                DIM_COEFFICIENT
        callback = context as OnLoginViaShared
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.share_dialog,container,false)
        when (arguments?.getInt(TYPE_KEY)){
            FACEBOOK -> {
                view.dialog_icon.setImageResource(R.drawable.facebook_black_24dp)
                view.dialog_button.text = getString(R.string.dialog_button_text, getString(R.string.facebook))
            }
            TWITTER -> {
                view.dialog_icon.setImageResource(R.drawable.twitter_black_24dp)
                view.dialog_button.text = getString(R.string.dialog_button_text,getString(R.string.twitter))
            }
            VK -> {
                view.dialog_icon.setImageResource(R.drawable.vk_black_24dp)
                view.dialog_button.text = getString(R.string.dialog_button_text,getString(R.string.vk))
            }
            GOOGLE -> {
                view.dialog_icon.setImageResource(R.drawable.google_black_24dp)
                view.dialog_button.text = getString(R.string.dialog_button_text,getString(R.string.google))
            }
        }
        view.dialog_button.setOnClickListener(this)
        return view
    }

    override fun onClick(view: View?) {
        when (view?.id){
            R.id.dialog_button -> {
                dialog_progress_bar.visibility = View.VISIBLE
                dialog_button.setOnClickListener(null)
                handler.postDelayed({
                    dialog_progress_bar.visibility = View.INVISIBLE
                    dialog_message.visibility = View.VISIBLE
                    handler.postDelayed({
                        callback.onLoginViaShared(
                                when (arguments?.getInt(TYPE_KEY)){
                                    FACEBOOK -> getString(R.string.facebook)
                                    TWITTER -> getString(R.string.twitter)
                                    VK -> getString(R.string.vk)
                                    GOOGLE -> getString(R.string.google)
                                    else -> ""
                                }
                        )
                        dismiss()
                    }, CLOSING_DELAY)
                }, LOADING_DELAY)
            }
        }
    }

}