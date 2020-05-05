package com.slaviboy.opengl.shapes.fill

import android.graphics.PointF
import android.opengl.GLES20
import com.slaviboy.opengl.main.OpenGLColor
import com.slaviboy.opengl.main.OpenGLRenderer
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

    protected var needUpdate = false

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

    var color: OpenGLColor = OpenGLColor()
    var keepSize: Boolean = false     // weather to keep size on scale

    var numberVertices: Int = numberVertices
        set(value) {
            field = value

            // we need to update the byte buffer as well
            circleCoords = FloatArray((numberVertices + 2) * OpenGLRenderer.COORDS_PER_VERTEX)
            val byteBuffer = ByteBuffer.allocateDirect(circleCoords.size * 4)
            byteBuffer.order(ByteOrder.nativeOrder())
            vertexBuffer = byteBuffer.asFloatBuffer()
            needUpdate = true
        }

    var startAngle = 45
        set(value) {
            field = value
            needUpdate = true
        }

    private var program: Int
    private var positionHandle = 0
    private var colorHandle = 0
    private var MVPMatrixHandle = 0
    private val vertexStride: Int = OpenGLRenderer.COORDS_PER_VERTEX * 4               // 4 bytes per vertex
    protected lateinit var vertexBuffer: FloatBuffer
    private var circleCoords = FloatArray((numberVertices + 2) * OpenGLRenderer.COORDS_PER_VERTEX)
    private val result = PointF()                                        // result point from ordinary point to a OpenGL coordinate system

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

    init {

        val vertexShader = OpenGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = OpenGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
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

            vertexBuffer.put(circleCoords)
            vertexBuffer.position(0)
        }

        GLES20.glUseProgram(program)

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
        MVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0)

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, (numberVertices + 2))

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun generateCoordinates(updateCoordinate: Boolean) {

        if (updateCoordinate) {
            getCoordinatesOpenGL(x, y)  // center
        }

        val openGLRadius = OpenGLRenderer.matrixGestureDetector.normalizeWidth(radius, !keepSize)

        circleCoords[0] = result.x
        circleCoords[1] = result.y
        circleCoords[2] = 0f
        for (i in 1 until numberVertices + 1) {
            val j = startAngle + (i) * ((360.0) / (numberVertices))
            circleCoords[i * 3 + 0] = openGLRadius * Math.cos(3.14 / 180 * j).toFloat() + result.x
            circleCoords[i * 3 + 1] = openGLRadius * Math.sin(3.14 / 180 * j).toFloat() + result.y
            // circleCoords[i * 3 + 2] = 0f
        }

        // set last vertex to match first which starts from index 1
        circleCoords[(numberVertices + 2 - 1) * 3 + 0] = circleCoords[1 * 3 + 0]
        circleCoords[(numberVertices + 2 - 1) * 3 + 1] = circleCoords[1 * 3 + 1]

        needUpdate = false
    }

    private fun getCoordinatesOpenGL(x: Float, y: Float) {
        OpenGLRenderer.matrixGestureDetector.normalizeCoordinates(x, y, result)
    }

}