package com.slaviboy.opengl.main

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object OpenGLHelper {

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

    /**
     * Reads in text from a resource file and returns a String containing the
     * text.
     */
    fun readTextFileFromResource(context: Context, resourceId: Int): String {
        val body = StringBuilder()
        try {
            val inputStream = context.resources.openRawResource(resourceId)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var nextLine: String?
            while (bufferedReader.readLine().also { nextLine = it } != null) {
                body.append(nextLine)
                body.append('\n')
            }
        } catch (e: IOException) {
            throw RuntimeException(
                "Could not open resource: $resourceId", e
            )
        } catch (nfe: Resources.NotFoundException) {
            throw RuntimeException("Resource not found: $resourceId", nfe)
        }
        return body.toString()
    }

    /**
     * Load OpenGL texture from drawable resources
     */
    fun loadTexture(context: Context, resourceId: Int): Int {

        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            val options = BitmapFactory.Options()
            options.inScaled = false // No pre-scaling

            // Read in the resource
            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle()
        }

        if (textureHandle[0] == 0) {
            throw RuntimeException("Error loading texture.")
        }

        return textureHandle[0]
    }

    /**
     * Load OpenGl texture from bitmap
     */
    fun loadTexture(bitmap: Bitmap): Int {

        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            val options = BitmapFactory.Options()
            options.inScaled = false // No pre-scaling

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle()
        }

        if (textureHandle[0] == 0) {
            throw RuntimeException("Error loading texture.")
        }

        return textureHandle[0]
    }

    fun getResizedBitmap(bitamp: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bitamp.width
        val height = bitamp.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height

        val matrix = android.graphics.Matrix()
        matrix.postScale(scaleWidth, scaleHeight)

        val resizedBitmap: Bitmap = Bitmap.createBitmap(bitamp, 0, 0, width, height, matrix, false)
        bitamp.recycle()
        return resizedBitmap
    }

    fun getBitmap(context: Context, resourceId: Int): Bitmap {

        val options = BitmapFactory.Options()
        options.inScaled = false // No pre-scaling

        // Read in the resource
        return BitmapFactory.decodeResource(context.resources, resourceId, options)
    }

    const val NEAR: Int = 3                                             // near from the frustum, since it is 1 and not 3 as presented by the android team
    const val COORDS_PER_VERTEX = 2                                     // how many coordinates per vertex
    lateinit var matrixGestureDetector: OpenGLMatrixGestureDetector     // static gesture detect (can convert it to object instead using it as class!!!)
    var enableAlpha: Boolean = true                                     // if alpha transparency is enabled
    var enableAntialiasing: Boolean = true                              // if antialiasing is enabled
    lateinit var vertexShaderCode: String
    lateinit var fragmentShaderCode: String
    lateinit var vertexTextureShaderCode: String
    lateinit var fragmentTextureShaderCode: String

    var width: Float = 0f                                               // device width
    var height: Float = 0f                                              // device height
}