/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package  com.slaviboy.opengl.bigdata

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.MotionEvent
import com.slaviboy.opengl.R
import com.slaviboy.opengl.main.OpenGLHelper
import com.slaviboy.opengl.main.OpenGLHelper.enableAlpha
import com.slaviboy.opengl.main.OpenGLHelper.fragmentShaderCode
import com.slaviboy.opengl.main.OpenGLHelper.fragmentTextureShaderCode
import com.slaviboy.opengl.main.OpenGLHelper.matrixGestureDetector
import com.slaviboy.opengl.main.OpenGLHelper.readTextFileFromResource
import com.slaviboy.opengl.main.OpenGLHelper.vertexShaderCode
import com.slaviboy.opengl.main.OpenGLHelper.vertexTextureShaderCode
import com.slaviboy.opengl.shapes.fill.Image
import com.slaviboy.opengl.shapes.stroke.Line
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BigDataRenderer(private val context: Context) : GLSurfaceView.Renderer {

    // shapes
    private lateinit var lines: ArrayList<Line>
    private lateinit var images: ArrayList<Image>

    private var MVPMatrix: FloatArray                 // model view projection matrix
    private var projectionMatrix: FloatArray          // matrix with applied projection
    private var viewMatrix: FloatArray                // view matrix
    private val transformedMatrixOpenGL: FloatArray   // matrix with transformation applied by finger gestures

    init {

        // set default OpenGL matrix
        MVPMatrix = FloatArray(16)
        viewMatrix = FloatArray(16)
        projectionMatrix = FloatArray(16)
        transformedMatrixOpenGL = FloatArray(16)

        vertexShaderCode = readTextFileFromResource(context, R.raw.vertex_shader)
        fragmentShaderCode = readTextFileFromResource(context, R.raw.fragment_shader)
        vertexTextureShaderCode = readTextFileFromResource(context, R.raw.vertex_texture_shader)
        fragmentTextureShaderCode = readTextFileFromResource(context, R.raw.fragment_texture_shader)
    }

    fun onTouch(event: MotionEvent) {

        matrixGestureDetector.onTouchEvent(event)

        val action = event.actionMasked
        when (action) {

            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
            }
        }
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

        // set the background frame color
        GLES20.glClearColor(1f, 1f, 1f, 1.0f)
    }

    override fun onDrawFrame(unused: GL10) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        if (enableAlpha) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // get OpenGL matrix with the applied transformations, from finger gestures
        matrixGestureDetector.transform(MVPMatrix, transformedMatrixOpenGL)

        for (i in lines.indices) {
            lines[i].draw(transformedMatrixOpenGL)
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {

        // set ratio between sides
        val ratio = width.toFloat() / height

        // Adjust the viewport based on geometry changes, such as screen rotation
        GLES20.glViewport(0, 0, width, height)

        // this projection matrix is applied to object coordinates
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f / OpenGLHelper.NEAR, 7f)

        val w = width.toFloat()
        val h = height.toFloat()
        OpenGLHelper.width = w
        OpenGLHelper.height = h

        // set size and translate to center to match shape coordinates
        matrixGestureDetector.width = w
        matrixGestureDetector.height = h

        createShapes()
    }

    /**
     * Initialize shapes, and give them proper size
     */
    private fun createShapes() {

        val width = OpenGLHelper.width
        val height = OpenGLHelper.height

        val widthRange = 0 until width.toInt()
        val heightRange = 0 until height.toInt()
        lines = ArrayList()
        for (i in 0 until 500) {

            val x1 = widthRange.random().toFloat()
            val y1 = heightRange.random().toFloat()
            val x2 = widthRange.random().toFloat()
            val y2 = heightRange.random().toFloat()
            val line = Line(x1, y1, x2, y2)
            line.strokeWidth = 6f
            lines.add(line)
        }

    }
}