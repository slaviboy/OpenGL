/*
 * Copyright (C) 2020 Stanislav Georgiev
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

package com.slaviboy.opengl.shapes.fill

import android.graphics.PointF
import android.opengl.GLES20
import com.slaviboy.opengl.main.OpenGLColor
import com.slaviboy.opengl.main.OpenGLRenderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * A 2D rectangle for use as a drawn object in OpenGL ES 2.0.
 * @param x top left x coordinate
 * @param y top left y coordinate
 * @param width rectangle width
 * @param height rectangle height
 */
class Rectangle(x: Float = 0f, y: Float = 0f, width: Float = 100f, height: Float = 100f) {

    private var needUpdate = false

    var x: Float = x
        set(value) {
            field = value
            needUpdate = true
        }

    var y: Float = y
        set(value) {
            field = value
            needUpdate = true
        }

    var width: Float = width
        set(value) {
            field = value
            needUpdate = true
        }

    var height: Float = height
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

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var drawListBuffer: ShortBuffer
    private var program: Int = -1
    private var positionHandle = 0
    private var colorHandle = 0
    private var MVPMatrixHandle = 0
    private val drawOrder: ShortArray = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices
    private val vertexStride: Int = OpenGLRenderer.COORDS_PER_VERTEX * 4              // 4 bytes per vertex

    var color: OpenGLColor = OpenGLColor()
    var rectCoords: FloatArray = FloatArray(12)                   // array with coordinate
        set(value) {
            field = value
            vertexBuffer.put(rectCoords)
            vertexBuffer.position(0)

            // TODO -> set the corresponding x,y,width,height rectangle coordinates
        }

    private val result = PointF()                                      // result point from ordinary point to a OpenGL coordinate system

    init {

        val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(rectCoords.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = byteBuffer.asFloatBuffer()

        // generate OpenGL coordinate from the graphic coordinates
        generateCoordinates()

        vertexBuffer.put(rectCoords)
        vertexBuffer.position(0)

        // initialize byte buffer for the draw list
        val dlb = ByteBuffer.allocateDirect(drawOrder.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer()
        drawListBuffer.put(drawOrder)
        drawListBuffer.position(0)

        // prepare shaders and OpenGL program
        val vertexShader = OpenGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = OpenGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram()              // create empty OpenGL Program
        GLES20.glAttachShader(program, vertexShader)    // add the vertex shader to program
        GLES20.glAttachShader(program, fragmentShader)  // add the fragment shader to program
        GLES20.glLinkProgram(program)                   // create OpenGL program executables
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    fun draw(mvpMatrix: FloatArray) {

        if (needUpdate) {
            // generate OpenGL coordinate from the graphic coordinates
            generateCoordinates()

            vertexBuffer.put(rectCoords)
            vertexBuffer.position(0)
        }

        // Add program to OpenGL environment
        GLES20.glUseProgram(program)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")

        // enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle)

        // prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
            positionHandle, OpenGLRenderer.COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            vertexStride, vertexBuffer
        )

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(program, "vColor")

        // set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color.value, 0)

        // get handle to shape's transformation matrix
        MVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        OpenGLRenderer.checkGlError("glGetUniformLocation")

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0)
        OpenGLRenderer.checkGlError("glUniformMatrix4fv")

        // draw the square
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, drawOrder.size,
            GLES20.GL_UNSIGNED_SHORT, drawListBuffer
        )

        // disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun generateCoordinates() {
        getCoordinatesOpenGL(x, y, 0)                            // top left
        getCoordinatesOpenGL(x, y + height, 3)                // bottom left
        getCoordinatesOpenGL(x + width, y + height, 6)     // bottom right
        getCoordinatesOpenGL(x + width, y, 9)                 // top right
        needUpdate = false
    }

    private fun getCoordinatesOpenGL(x: Float, y: Float, i: Int) {
        OpenGLRenderer.matrixGestureDetector.normalizeCoordinates(x, y, result)
        rectCoords[i] = result.x
        rectCoords[i + 1] = result.y
    }

}