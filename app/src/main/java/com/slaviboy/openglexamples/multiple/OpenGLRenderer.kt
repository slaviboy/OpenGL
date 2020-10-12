/*
* Copyright (C) 2020 Stanislav Georgiev
* https://github.com/slaviboy
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
package  com.slaviboy.openglexamples.multiple

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.slaviboy.opengl.main.OpenGLStatic
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *
 */
class OpenGLRenderer(val context: Context, var openGLHelper: OpenGLHelper, requestRenderListener: (() -> Unit)) : GLSurfaceView.Renderer {

    private var MVPMatrix: FloatArray                       // model view projection matrix
    private var projectionMatrix: FloatArray                // matrix with applied projection
    private var viewMatrix: FloatArray                      // view matrix
    private val transformedMatrixOpenGL: FloatArray         // matrix with transformation applied by finger gestures as OpenGL values
    private val totalScaleMatrix: android.graphics.Matrix   // the graphic matrix, that has the total scale of both scale and transformation matrices

    init {

        OpenGLStatic.DEVICE_HALF_WIDTH = 0f
        OpenGLStatic.DEVICE_HALF_HEIGHT = 0f

        // set default OpenGL matrix
        MVPMatrix = FloatArray(16)
        viewMatrix = FloatArray(16)
        projectionMatrix = FloatArray(16)
        transformedMatrixOpenGL = FloatArray(16)
        totalScaleMatrix = android.graphics.Matrix()
        OpenGLStatic.setShaderStrings(context)
        openGLHelper.requestRenderListener = requestRenderListener
    }


    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // set the background frame color
        GLES20.glClearColor(1f, 1f, 1f, 1f)
    }

    override fun onDrawFrame(unused: GL10) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // get OpenGL matrix with the applied transformations, from finger gestures
        openGLHelper.mainGestureDetector.transform(MVPMatrix, transformedMatrixOpenGL)

        // draw all elements
        openGLHelper.draw(transformedMatrixOpenGL)
    }

    /**
     * When the surface is changed, it is used to update the device size,
     * and the projection matrix. And also to recreate the shapes, since
     * they are dependent on the device size.
     */
    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {

        if (OpenGLStatic.ENABLE_ALPHA) {
            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        }

        OpenGLStatic.setComponents(width.toFloat(), height.toFloat())

        // Adjust the viewport based on geometry changes, such as screen rotation
        GLES20.glViewport(0, 0, width, height)

        // this projection matrix is applied to object coordinates
        Matrix.frustumM(projectionMatrix, 0, -OpenGLStatic.RATIO, OpenGLStatic.RATIO, -1f, 1f, 3f / OpenGLStatic.NEAR, 7f)

        // translate to center, make the openGL point(0,0) the center of the device
        openGLHelper.mainGestureDetector.matrix = android.graphics.Matrix()
        openGLHelper.mainGestureDetector.matrix.postTranslate(OpenGLStatic.DEVICE_HALF_WIDTH, OpenGLStatic.DEVICE_HALF_HEIGHT)

        openGLHelper.createShapes(context)
    }
}