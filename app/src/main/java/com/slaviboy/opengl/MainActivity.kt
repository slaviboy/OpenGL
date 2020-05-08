package com.slaviboy.opengl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.slaviboy.opengl.bigdata.BigDataSurfaceView
import com.slaviboy.opengl.main.OpenGLSurfaceView

class MainActivity : AppCompatActivity() {

    lateinit var openGLView: OpenGLSurfaceView
    lateinit var bigDataView: BigDataSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

          openGLView = findViewById(R.id.open_gl_view)
        // bigDataView = findViewById(R.id.big_data_view)
    }

    override fun onResume() {
        super.onResume()

        if(::openGLView.isInitialized){
            openGLView.onResume()
        }

        if(::bigDataView.isInitialized){
            bigDataView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()

        if(::openGLView.isInitialized){
            openGLView.onPause()
        }

        if(::bigDataView.isInitialized){
            bigDataView.onPause()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        actionBar?.hide()
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}