package com.slaviboy.openglexamples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.slaviboy.openglexamples.single.OpenGLSurfaceView

class MainActivity : AppCompatActivity() {

    lateinit var openGLView: OpenGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openGLView = findViewById(R.id.open_gl_view)
    }

    override fun onResume() {
        super.onResume()

        if (::openGLView.isInitialized) {
            openGLView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()

        if (::openGLView.isInitialized) {
            openGLView.onPause()
        }
    }

    /**
     * When the button is clicked change the coordinates and color for the shapes
     * with random values
     */
    fun random(view: View) {
        openGLView.openGLHelper.updateShapes()
    }

    /**
     * When the button is clicked change the style for the shapes FILL or STROKE
     */
    fun changeStyle(view: View) {
        openGLView.openGLHelper.strokeFillShapes()
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