package com.example.adityabansal.popupapp

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.R.attr.gravity
import android.content.Context
import android.widget.ImageButton
import android.widget.ImageView
import android.R.id.closeButton
import android.R.attr.y
import android.R.attr.x
import android.util.Log
import android.view.MotionEvent






/**
 * Created by adityabansal on 11/18/17.
 */
class HeadService : Service() {

    var mWindowManager : WindowManager? = null
    var mHeadView : View? = null


    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override fun onCreate() {
        super.onCreate()
        mHeadView = LayoutInflater.from(this).inflate(R.layout.layout_chat_head, null) as View
        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = 0
        params.y = 100

        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager!!.addView(mHeadView, params);


        val closeButton = mHeadView?.findViewById<ImageView>(R.id.close_btn) as ImageView
        closeButton.setOnClickListener {
            //close the service and remove the chat head from the window
            stopSelf()
        }

        val headImage = mHeadView?.findViewById<ImageView>(R.id.chat_head_profile_iv) as ImageView
        headImage?.setOnTouchListener(object : View.OnTouchListener {
            private var lastAction: Int = 0
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.toFloat()
            private var initialTouchY: Float = 0.toFloat()

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        //remember the initial position.
                        initialX = params.x
                        initialY = params.y

                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        lastAction = event.action
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            //Open the chat conversation click.
                            val intent = Intent(this@HeadService, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                            //close the service and remove the chat heads
                            stopSelf()
                        }
                        lastAction = event.action
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()

                        //Update the layout with new X & Y coordinate
                        mWindowManager?.updateViewLayout(mHeadView, params)
                        lastAction = event.action
                        return true
                    }
                }
                return false
            }
        });
    }

    override fun onDestroy() {
        super.onDestroy()
        mWindowManager?.removeView(mHeadView);
    }
}