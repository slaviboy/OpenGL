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

import android.graphics.Bitmap
import android.graphics.PointF
import android.opengl.GLES20
import com.slaviboy.opengl.main.OpenGLColor
import com.slaviboy.opengl.main.OpenGLHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * A 2D image for use as a drawn object in OpenGL ES 2.0.
 * @param bitmap bitmap that will be draw
 * @param x top left x coordinate
 * @param y top left y coordinate
 * @param width rectangle width
 * @param height rectangle height
 */
class Image(bitmap: Bitmap, x: Float = 0f, y: Float = 0f, width: Float = 100f, height: Float = 100f) {

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

    var width: Float = 0f
        set(value) {
            field = value
            needUpdate = true
            setRawSize()
        }

    var height: Float = 0f
        set(value) {
            field = value
            needUpdate = true
            setRawSize()
        }

    private var rawWidth: Float                         // raw calculate width in case it is set as WRAP_CONTENT, AUTO_SIZE...
    private var rawHeight: Float                        // raw calculate height in case it is set as WRAP_CONTENT, AUTO_SIZE...

    private var needUpdate: Boolean                     // if the array spriteCoords should be generated again once the draw method is called

    private var bitmapWidth: Float                      // width of the bitmap
    private var bitmapHeight: Float                     // height of the bitmap
    private var textureDataHandle: Int                  // handle for the bitmap

    private val squareTextureCoordinates: FloatBuffer   // array with coordinate for the texture
    private var textureUniformHandle: Int               // handle for the texture uniform
    private var textureCoordinateHandle: Int            // handle for the texture coordinates
    private var textureCoordinateDataSize: Int          // size for the texture coordinates

    private val program: Int                            // program for attaching the shaders
    private val vertexBuffer: FloatBuffer               // buffer fo the vertex
    private val drawListBuffer: ShortBuffer             // buffer for the draw list
    private var positionHandle: Int                     // handle for the position
    private var colorHandle: Int                        // handle for the color
    private var MVPMatrixHandle: Int                    // handle for the MVP matrix
    private var drawOrder: ShortArray                   // order to draw vertices
    private var vertexStride: Int                       // bytes per vertex
    private var result: PointF                          // result point from graphic point to a OpenGL coordinate system

    var vertexShaderCode: String                        // shader with the vertex
    var fragmentShaderCode: String                      // shader with the fragment
    var color: OpenGLColor                              // color for the shape
    var useCoordinateAsCenter: Boolean                  // use the x, y coordinate as center point for the image
    var keepSize: Boolean                               // weather to keep shape size when scale is made

    // array with coordinate for the rectangle shape between [-1,1]
    var imageCoords = FloatArray(4 * OpenGLHelper.COORDS_PER_VERTEX)
        set(value) {
            field = value
            vertexBuffer.put(imageCoords)
            vertexBuffer.position(0)

            // TODO -> set the corresponding x,y,width,height rectangle coordinates
        }

    init {

        keepSize = false
        needUpdate = false

        rawWidth = 0f
        rawHeight = 0f

        bitmapWidth = bitmap.width.toFloat()
        bitmapHeight = bitmap.height.toFloat()
        textureDataHandle = OpenGLHelper.loadTexture(bitmap)

        textureUniformHandle = 0
        textureCoordinateHandle = 0
        textureCoordinateDataSize = 2

        vertexShaderCode = OpenGLHelper.vertexTextureShaderCode
        fragmentShaderCode = OpenGLHelper.fragmentTextureShaderCode

        positionHandle = 0
        colorHandle = 0
        MVPMatrixHandle = 0
        drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)
        vertexStride = OpenGLHelper.COORDS_PER_VERTEX * 4
        result = PointF()

        color = OpenGLColor(255, 255, 255, 100)
        useCoordinateAsCenter = false

        this.width = width
        this.height = height

        // initialize Vertex Byte Buffer for Shape Coordinates / # of coordinate values * 4 bytes per float
        val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(imageCoords.size * 4)

        // use the Device's Native Byte Order
        byteBuffer.order(ByteOrder.nativeOrder())

        //create a floating point buffer from the ByteBuffer
        vertexBuffer = byteBuffer.asFloatBuffer()

        // generate OpenGL coordinate from the graphic coordinates
        generateCoordinates()

        // add the coordinates to the FloatBuffer
        vertexBuffer.put(imageCoords)

        // set the Buffer to Read the first coordinate
        vertexBuffer.position(0)

        // S, T (or X, Y)
        // Texture coordinate data.
        // Because images have a Y axis pointing downward (values increase as you move down the image) while
        // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
        // What's more is that the texture coordinates are the same for every face.
        val cubeTextureCoordinateData = floatArrayOf(
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
        )
        squareTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        squareTextureCoordinates.put(cubeTextureCoordinateData).position(0)

        // initialize byte buffer for the draw list
        val dlb: ByteBuffer = ByteBuffer.allocateDirect(imageCoords.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer()
        drawListBuffer.put(drawOrder)
        drawListBuffer.position(0)

        val vertexShader: Int = OpenGLHelper.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = OpenGLHelper.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)

        // texture Code
        GLES20.glBindAttribLocation(program, 0, "a_TexCoordinate")
        GLES20.glLinkProgram(program)
    }

    /**
     * Set the raw size, which is the size that is calculated in case
     * width or height is set as WRAP_CONTENT, MATCH_DEVICE or AUTO_SIZE
     */
    private fun setRawSize() {

        rawWidth = when (width) {
            WRAP_CONTENT -> bitmapWidth
            MATCH_DEVICE -> OpenGLHelper.width
            AUTO_SIZE -> height * (bitmapWidth / bitmapHeight)
            else -> width
        }

        rawHeight = when (height) {
            WRAP_CONTENT -> bitmapHeight
            MATCH_DEVICE -> OpenGLHelper.height
            AUTO_SIZE -> width * (bitmapHeight / bitmapWidth)
            else -> height
        }
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

            vertexBuffer.put(imageCoords)
            vertexBuffer.position(0)
        }

        // add program to OpenGL ES Environment
        GLES20.glUseProgram(program)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")

        // enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle)

        // prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, OpenGLHelper.COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)

        // get Handle to Fragment Shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(program, "vColor")

        // set the Color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color.value, 0)

        // set Texture Handles and bind Texture
        textureUniformHandle = GLES20.glGetAttribLocation(program, "u_Texture")
        textureCoordinateHandle = GLES20.glGetAttribLocation(program, "a_TexCoordinate")

        // set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)

        // bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle)

        // tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(textureUniformHandle, 0)

        // pass in the texture coordinate information
        squareTextureCoordinates.position(0)
        GLES20.glVertexAttribPointer(textureCoordinateHandle, textureCoordinateDataSize, GLES20.GL_FLOAT, false, 0, squareTextureCoordinates)
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle)

        // get Handle to Shape's Transformation Matrix
        MVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        // apply the projection and view transformation
        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0)

        // draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_SHORT, drawListBuffer)

        // disable Vertex Array
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    /**
     * Generate the OpenGL coordinates in range [-1,1] for the rectangle using the
     * x, y, width and height
     */
    private fun generateCoordinates(updateCoordinate: Boolean = true) {

        val w = -OpenGLHelper.matrixGestureDetector.normalizeWidth(rawWidth, !keepSize) * 2
        val h = -OpenGLHelper.matrixGestureDetector.normalizeHeight(rawHeight, !keepSize) * 2

        if (updateCoordinate) {
            OpenGLHelper.matrixGestureDetector.normalizeCoordinates(this.x, this.y, result)
        }

        var x = result.x
        var y = result.y
        if (useCoordinateAsCenter) {
            x -= (w / 2f)
            y -= (h / 2f)
        }

        // top left
        imageCoords[0] = x
        imageCoords[1] = y

        // bottom left
        imageCoords[2] = x
        imageCoords[3] = y + h

        // bottom right
        imageCoords[4] = x + w
        imageCoords[5] = y + h

        // top right
        imageCoords[6] = x + w
        imageCoords[7] = y

        needUpdate = false
    }

    companion object {
        const val WRAP_CONTENT = -1f
        const val MATCH_DEVICE = -2f
        const val AUTO_SIZE = -3f
    }
}