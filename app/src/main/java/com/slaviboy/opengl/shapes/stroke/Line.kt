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

package com.slaviboy.opengl.shapes.stroke

import android.graphics.PointF
import android.opengl.GLES20
import com.slaviboy.opengl.main.OpenGLColor
import com.slaviboy.opengl.main.OpenGLRenderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * A 2D line for use as a drawn object in OpenGL ES 2.0.
 * @param x1 first point x coordinate
 * @param y1 first point y coordinate
 * @param x2 second point x coordinate
 * @param y2 second point y coordinate
 */
class Line(x1: Float = 0f, y1: Float = 0f, x2: Float = 100f, y2: Float = 100f) {

    private var needUpdate = false

    var x1: Float = x1
        set(value) {
            field = value
            needUpdate = true
        }

    var y1: Float = y1
        set(value) {
            field = value
            needUpdate = true
        }

    var x2: Float = x2
        set(value) {
            field = value
            needUpdate = true
        }

    var y2: Float = y2
        set(value) {
            field = value
            needUpdate = true
        }

    private val vertexShaderCode: String =
        """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        void main() {
          gl_Position = uMVPMatrix * vPosition;
        }
        """.trimIndent()

    private val fragmentShaderCode: String =
        """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
          gl_FragColor = vColor;
        }
        """.trimIndent()

    private var program: Int
    private var positionHandle = 0
    private var colorHandle = 0
    private var MVPMatrixHandle = 0
    private val vertexStride: Int = OpenGLRenderer.COORDS_PER_VERTEX * 4               // 4 bytes per vertex
    private val vertexBuffer: FloatBuffer

    var color: OpenGLColor = OpenGLColor()
    var strokeWidth: Float = 1f

    var lineCoords: FloatArray = FloatArray(6)
        set(value) {
            field = value
            vertexBuffer.put(value)
            vertexBuffer.position(0)
            vertexCount = value.size / OpenGLRenderer.COORDS_PER_VERTEX

            // TODO -> set the corresponding x1,y1,x2,y2 line coordinates
        }

    private var vertexCount: Int = lineCoords.size / OpenGLRenderer.COORDS_PER_VERTEX
    private val result = PointF()                                      // result point from ordinary point to a OpenGL coordinate system

    init {

        val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(lineCoords.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = byteBuffer.asFloatBuffer()

        // generate OpenGL coordinate from the graphic coordinates
        generateCoordinates()

        vertexBuffer.put(lineCoords)
        vertexBuffer.position(0)

        val vertexShader: Int = OpenGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = OpenGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram()              // create empty OpenGL ES Program
        GLES20.glAttachShader(program, vertexShader)    // add the vertex shader to program
        GLES20.glAttachShader(program, fragmentShader)  // add the fragment shader to program
        GLES20.glLinkProgram(program)                   // creates OpenGL ES program executables
    }

    fun draw(mvpMatrix: FloatArray) {

        if (needUpdate) {
            // generate OpenGL coordinate from the graphic coordinates
            generateCoordinates()

            vertexBuffer.put(lineCoords)
            vertexBuffer.position(0)
        }

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(program)
        GLES20.glLineWidth(strokeWidth)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle)

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
            positionHandle, OpenGLRenderer.COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            vertexStride, vertexBuffer
        )

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(program, "vColor")

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color.value, 0)

        // get handle to shape's transformation matrix
        MVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        OpenGLRenderer.checkGlError("glGetUniformLocation")

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0)
        OpenGLRenderer.checkGlError("glUniformMatrix4fv")

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount)

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun generateCoordinates() {
        getCoordinatesOpenGL(x1, y1, 0)      // top left
        getCoordinatesOpenGL(x2, y2, 3)      // bottom left
        needUpdate = false
    }

    private fun getCoordinatesOpenGL(x: Float, y: Float, i: Int) {
        OpenGLRenderer.matrixGestureDetector.normalizeCoordinates(x, y, result)
        lineCoords[i] = result.x
        lineCoords[i + 1] = result.y
    }
}