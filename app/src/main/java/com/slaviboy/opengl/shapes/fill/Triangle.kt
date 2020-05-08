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
import com.slaviboy.opengl.main.OpenGLHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * A two-dimensional triangle for use as a drawn object in OpenGL ES 2.0.
 * @param x1 first point x coordinate
 * @param y1 first point y coordinate
 * @param x2 second point x coordinate
 * @param y2 second point y coordinate
 * @param x3 third point x coordinate
 * @param y3 third point y coordinate
 */
class Triangle(x1: Float = 0f, y1: Float = 0f, x2: Float = 100f, y2: Float = 0f, x3: Float = 100f, y3: Float = 100f) {

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

    var x3: Float = x3
        set(value) {
            field = value
            needUpdate = true
        }

    var y3: Float = y3
        set(value) {
            field = value
            needUpdate = true
        }

    private var needUpdate: Boolean             // if the array rectCoords should be generated again once the draw method is called

    private val program: Int                    // program for attaching the shaders
    private var vertexBuffer: FloatBuffer       // buffer fo the vertex
    private var positionHandle: Int             // handle for the position
    private var colorHandle: Int                // handle for the color
    private var MVPMatrixHandle: Int            // handle for the MVP matrix
    private val drawOrder: ShortArray           // order to draw vertices
    private val vertexStride: Int               // bytes per vertex
    private var result: PointF                  // result point from graphic point to a OpenGL coordinate system
    private var vertexCount: Int                // number of vertices

    var vertexShaderCode: String                // shader with the vertex
    var fragmentShaderCode: String              // shader with the fragment
    var color: OpenGLColor                      // color for the shape

    var triangleCoords: FloatArray = FloatArray(3 * OpenGLHelper.COORDS_PER_VERTEX)
        set(value) {
            field = value

            val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(triangleCoords.size * 4)
            byteBuffer.order(ByteOrder.nativeOrder())
            vertexBuffer = byteBuffer.asFloatBuffer()
            vertexBuffer.put(triangleCoords)
            vertexBuffer.position(0)
            vertexCount = value.size / OpenGLHelper.COORDS_PER_VERTEX

            // TODO -> set the corresponding x1,y1,x2,y2... polygon coordinates
        }

    init {

        vertexCount = triangleCoords.size / OpenGLHelper.COORDS_PER_VERTEX
        needUpdate = false
        vertexShaderCode = OpenGLHelper.vertexShaderCode
        fragmentShaderCode = OpenGLHelper.fragmentShaderCode
        positionHandle = 0
        colorHandle = 0
        MVPMatrixHandle = 0
        drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)
        vertexStride = OpenGLHelper.COORDS_PER_VERTEX * 4

        color = OpenGLColor()
        result = PointF()

        // initialize vertex byte buffer for shape coordinates
        val byteBuffer = ByteBuffer.allocateDirect(triangleCoords.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = byteBuffer.asFloatBuffer()

        // generate OpenGL coordinate from the graphic coordinates
        generateCoordinates()

        vertexBuffer.put(triangleCoords)
        vertexBuffer.position(0)

        // prepare shaders and OpenGL program
        val vertexShader = OpenGLHelper.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = OpenGLHelper.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram() // create empty OpenGL Program
        GLES20.glAttachShader(program, vertexShader) // add the vertex shader to program
        GLES20.glAttachShader(program, fragmentShader) // add the fragment shader to program
        GLES20.glLinkProgram(program) // create OpenGL program executables
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     * @param mvpMatrix the Model View Project matrix in which to draw
     * this shape.
     */
    fun draw(mvpMatrix: FloatArray) {

        if (needUpdate) {
            // generate OpenGL coordinate from the graphic coordinates
            generateCoordinates()

            vertexBuffer.put(triangleCoords)
            vertexBuffer.position(0)
        }

        // Add program to OpenGL environment
        GLES20.glUseProgram(program)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle)

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
            positionHandle, OpenGLHelper.COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            vertexStride, vertexBuffer
        )

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(program, "vColor")

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color.value, 0)

        // get handle to shape's transformation matrix
        MVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        OpenGLHelper.checkGlError("glGetUniformLocation")

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0)
        OpenGLHelper.checkGlError("glUniformMatrix4fv")

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    /**
     * Generate the OpenGL coordinates in range [-1,1] for the triangle using the
     * x1, y1, x2, y2, x3, y3 coordinates
     */
    private fun generateCoordinates() {
        val n = OpenGLHelper.COORDS_PER_VERTEX
        getCoordinatesOpenGL(x1, y1, 0 * n)
        getCoordinatesOpenGL(x2, y2, 1 * n)
        getCoordinatesOpenGL(x3, y3, 2 * n)
        needUpdate = false
    }

    /**
     * Get the open gl coordinates in range [-1,1]
     */
    private fun getCoordinatesOpenGL(x: Float, y: Float, i: Int) {
        OpenGLHelper.matrixGestureDetector.normalizeCoordinates(x, y, result)
        triangleCoords[i] = result.x
        triangleCoords[i + 1] = result.y
    }
}