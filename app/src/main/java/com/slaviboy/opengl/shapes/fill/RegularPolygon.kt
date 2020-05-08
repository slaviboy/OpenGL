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
 * A 2D regular polygon for use as a drawn object in OpenGL ES 2.0.
 * @param x polygon center x coordinate
 * @param y polygon center y coordinate
 * @param r polygon radius (distance between the center and the vertices)
 */
open class RegularPolygon(x: Float = 0f, y: Float = 0f, r: Float = 100f, numberVertices: Int = 4) {

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

    var radius: Float = r
        set(value) {
            field = value
            needUpdate = true
        }

    // the number of the polygon vertices
    var numberVertices: Int = numberVertices
        set(value) {
            field = value

            // we need to update the byte buffer as well
            polygonCoords = FloatArray((numberVertices + 2) * OpenGLHelper.COORDS_PER_VERTEX)
            val byteBuffer = ByteBuffer.allocateDirect(polygonCoords.size * 4)
            byteBuffer.order(ByteOrder.nativeOrder())
            vertexBuffer = byteBuffer.asFloatBuffer()
            needUpdate = true
        }

    // angle where the rotation is started
    var startAngle = 45
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

    var vertexShaderCode: String                // shader with the vertex
    var fragmentShaderCode: String              // shader with the fragment
    var color: OpenGLColor                      // color for the shape
    var keepSize: Boolean = false               // weather to keep shape size when scale is made

    var polygonCoords = FloatArray((numberVertices + 2) * OpenGLHelper.COORDS_PER_VERTEX)
        set(value) {
            field = value

            val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(polygonCoords.size * 4)
            byteBuffer.order(ByteOrder.nativeOrder())
            vertexBuffer = byteBuffer.asFloatBuffer()
            vertexBuffer.put(polygonCoords)
            vertexBuffer.position(0)

            // TODO -> set the corresponding x,y,width,height rectangle coordinates
        }

    init {

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

        val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(polygonCoords.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = byteBuffer.asFloatBuffer()

        // generate OpenGL coordinate from the graphic coordinates
        generateCoordinates()

        vertexBuffer.put(polygonCoords)
        vertexBuffer.position(0)

        val vertexShader = OpenGLHelper.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = OpenGLHelper.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram()                                              // create empty OpenGL ES Program
        GLES20.glAttachShader(program, vertexShader)                                    // add the vertex shader to program
        GLES20.glAttachShader(program, fragmentShader)                                  // add the fragment shader to program
        GLES20.glLinkProgram(program)
    }

    fun draw(mvpMatrix: FloatArray) {

        if (needUpdate || keepSize) {

            // generate OpenGL coordinate from the graphic coordinates
            val updateCoordinate = if (needUpdate) true else !keepSize
            generateCoordinates(updateCoordinate)

            vertexBuffer.put(polygonCoords)
            vertexBuffer.position(0)
        }

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
        MVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0)

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, (numberVertices + 2))

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    /**
     * Generate the OpenGL coordinates in range [-1,1] for the polygon using the
     * x, y as center
     */
    private fun generateCoordinates(updateCoordinate: Boolean = true) {

        if (updateCoordinate) {
            OpenGLHelper.matrixGestureDetector.normalizeCoordinates(x, y, result)  // center
        }

        val openGLRadius = OpenGLHelper.matrixGestureDetector.normalizeWidth(radius, !keepSize)

        val n = OpenGLHelper.COORDS_PER_VERTEX
        polygonCoords[0] = result.x
        polygonCoords[1] = result.y
        polygonCoords[2] = 0f
        for (i in 1 until numberVertices + 1) {
            val j = startAngle + (i) * ((360.0) / (numberVertices))
            polygonCoords[i * n + 0] = openGLRadius * Math.cos(3.14 / 180 * j).toFloat() + result.x
            polygonCoords[i * n + 1] = openGLRadius * Math.sin(3.14 / 180 * j).toFloat() + result.y
        }

        // set last vertex to match first which starts from index 1
        polygonCoords[(numberVertices + 2 - 1) * n + 0] = polygonCoords[1 * n + 0]
        polygonCoords[(numberVertices + 2 - 1) * n + 1] = polygonCoords[1 * n + 1]

        needUpdate = false
    }

}