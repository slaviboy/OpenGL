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

package  com.slaviboy.opengl.main

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.MotionEvent
import com.slaviboy.opengl.shapes.fill.*
import com.slaviboy.opengl.shapes.stroke.Line
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLRenderer : GLSurfaceView.Renderer {

    // shapes
    private lateinit var triangle: Triangle
    private lateinit var rectangle: Rectangle
    private lateinit var circle: Circle
    private lateinit var line: Line

    private var MVPMatrix: FloatArray                          // model view projection matrix
    private var projectionMatrix: FloatArray                   // matrix with applied projection
    private var viewMatrix: FloatArray                         // view matrix
    private val transformedMatrixOpenGL: FloatArray            // matrix with transformation applied by finger gestures

    var ratio: Float                                           // ratio width/height
    var width: Float                                           // device width
    var height: Float                                          // device height

    init {

        // set default OpenGL matrix
        MVPMatrix = FloatArray(16)
        viewMatrix = FloatArray(16)
        projectionMatrix = FloatArray(16)
        transformedMatrixOpenGL = FloatArray(16)

        ratio = 1f
        width = 0f
        height = 0f
    }

    fun onTouch(event: MotionEvent) {

        matrixGestureDetector.onTouchEvent(event)

        val action = event.actionMasked
        when (action) {

            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {

                //circle.x = event.x
                //circle.y = event.y
            }
        }
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

        // set the background frame color
        GLES20.glClearColor(1.0f, 0.0f, 1.0f, 1.0f)
    }

    override fun onDrawFrame(unused: GL10) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        if (enableAlpha) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // get OpenGL matrix with the applied transformations
        matrixGestureDetector.transform(MVPMatrix, transformedMatrixOpenGL)


        // draw shapes with apply transformations from finger gestures
        rectangle.draw(transformedMatrixOpenGL)
        triangle.draw(transformedMatrixOpenGL)
        line.draw(transformedMatrixOpenGL)
        circle.draw(transformedMatrixOpenGL)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {

        ratio = width.toFloat() / height

        // Adjust the viewport based on geometry changes, such as screen rotation
        GLES20.glViewport(0, 0, width, height)

        // this projection matrix is applied to object coordinates
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f / NEAR, 7f)

        this.width = width.toFloat()
        this.height = height.toFloat()

        // set size and translate to center to match shape coordinates
        matrixGestureDetector.width = this.width
        matrixGestureDetector.height = this.height
        matrixGestureDetector.matrix.setTranslate(width / 2f, height / 2f)

        createShapes()
    }

    /**
     * Initialize shapes
     */
    fun createShapes() {

        // create shapes
        triangle = Triangle(width / 2f, height * (1 / 5f), width, height * (1 / 5f), width / 2f, height / 3f)
        circle = Circle(width / 2f, height / 2f, width / 4f)
        line = Line(0f, 0f, width / 2f, height / 2f)
        rectangle = Rectangle(width / 2f, height / 2f + height / 5f, width / 4f, width / 4f)

        line.strokeWidth = 6f
    }

    companion object {

        /**
         * Utility method for compiling a OpenGL shader.
         * @param type vertex or fragment shader type.
         * @param shaderCode string containing the shader code.
         * @return returns an id for the shader.
         */
        fun loadShader(type: Int, shaderCode: String): Int {

            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            val shader = GLES20.glCreateShader(type)

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            return shader
        }

        /**
         * Utility method for debugging OpenGL calls. Provide the name of the call
         * just after making it:
         *
         * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
         * MyGLRenderer.checkGlError("glGetUniformLocation");
         *
         * If the operation is not successful, the check throws an error.
         * @param glOperation name of the OpenGL call to check.
         */
        fun checkGlError(glOperation: String) {
            var error: Int
            while (GLES20.glGetError().also { error = it } != GLES20.GL_NO_ERROR) {
                throw RuntimeException("$glOperation: glError $error")
            }
        }

        const val NEAR: Int = 3                                       // near from the frustum, since it is 1 and not 3 as presented by the android team
        const val COORDS_PER_VERTEX = 3                               // hoe many coordinates per vertex
        lateinit var matrixGestureDetector: OpenGLMatrixGestureDetector     // static gesture detect (can convert it to object instead using it as class!!!)
        var enableAlpha: Boolean = true                               // if alpha transparency is enabled
        var enableAntialiasing: Boolean = true                        // if antialiasing is enabled
    }
}