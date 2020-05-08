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
import java.nio.ShortBuffer

/**
 * A 2D rectangle for use as a drawn object in OpenGL ES 2.0.
 * @param x top left x coordinate
 * @param y top left y coordinate
 * @param width rectangle width
 * @param height rectangle height
 */
class Rectangle(x: Float = 0f, y: Float = 0f, width: Float = 100f, height: Float = 100f) {

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

    private var needUpdate: Boolean             // if the array rectCoords should be generated again once the draw method is called

    private val program: Int                    // program for attaching the shaders
    private var vertexBuffer: FloatBuffer       // buffer fo the vertex
    private val drawListBuffer: ShortBuffer     // buffer for the draw list
    private var positionHandle: Int             // handle for the position
    private var colorHandle: Int                // handle for the color
    private var MVPMatrixHandle: Int            // handle for the MVP matrix
    private val drawOrder: ShortArray           // order to draw vertices
    private val vertexStride: Int               // bytes per vertex
    private var result: PointF                  // result point from graphic point to a OpenGL coordinate system

    var vertexShaderCode: String                // shader with the vertex
    var fragmentShaderCode: String              // shader with the fragment
    var color: OpenGLColor                      // color for the shape
    var keepSize: Boolean                       // weather to keep shape size when scale is made

    // array with coordinate for the rectangle shape between [-1,1]
    var rectCoords = FloatArray(4 * OpenGLHelper.COORDS_PER_VERTEX)
        set(value) {
            field = value

            val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(rectCoords.size * 4)
            byteBuffer.order(ByteOrder.nativeOrder())
            vertexBuffer = byteBuffer.asFloatBuffer()
            vertexBuffer.put(rectCoords)
            vertexBuffer.position(0)

            // TODO -> set the corresponding x,y,width,height rectangle coordinates
        }

    init {

        keepSize = false
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
        val vertexShader = OpenGLHelper.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = OpenGLHelper.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram()              // create empty OpenGL Program
        GLES20.glAttachShader(program, vertexShader)    // add the vertex shader to program
        GLES20.glAttachShader(program, fragmentShader)  // add the fragment shader to program
        GLES20.glLinkProgram(program)                   // create OpenGL program executables
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     * @param mvpMatrix the Model View Project matrix in which to draw
     * this shape.
     */
    fun draw(mvpMatrix: FloatArray) {

        if (needUpdate || keepSize) {

            // generate OpenGL coordinate from the graphic coordinates
            val updateCoordinate = if (needUpdate) true else !keepSize
            generateCoordinates(updateCoordinate)

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
            positionHandle, OpenGLHelper.COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            vertexStride, vertexBuffer
        )

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(program, "vColor")

        // set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color.value, 0)

        // get handle to shape's transformation matrix
        MVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        OpenGLHelper.checkGlError("glGetUniformLocation")

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0)
        OpenGLHelper.checkGlError("glUniformMatrix4fv")

        // draw the square
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, drawOrder.size,
            GLES20.GL_UNSIGNED_SHORT, drawListBuffer
        )

        // disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    /**
     * Generate the OpenGL coordinates in range [-1,1] for the rectangle using the
     * x, y, width and height
     */
    private fun generateCoordinates(updateCoordinate: Boolean = true) {

        val w = -OpenGLHelper.matrixGestureDetector.normalizeWidth(width, !keepSize) * 2
        val h = -OpenGLHelper.matrixGestureDetector.normalizeHeight(height, !keepSize) * 2

        if (updateCoordinate) {
            OpenGLHelper.matrixGestureDetector.normalizeCoordinates(x, y, result)
        }

        // top left
        rectCoords[0] = result.x
        rectCoords[1] = result.y

        // bottom left
        rectCoords[2] = result.x
        rectCoords[3] = result.y + h

        // bottom right
        rectCoords[4] = result.x + w
        rectCoords[5] = result.y + h

        // top right
        rectCoords[6] = result.x + w
        rectCoords[7] = result.y

        needUpdate = false
    }

}