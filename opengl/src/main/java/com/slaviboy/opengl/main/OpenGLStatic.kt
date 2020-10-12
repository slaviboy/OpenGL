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
package com.slaviboy.opengl.main

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.opengl.GLES20
import android.opengl.GLUtils
import com.slaviboy.opengl.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Singleton with static methods that are used to load shader, check OpenGL error,
 * read string file from raw directory, load drawable resources and resize bitmap.
 */
object OpenGLStatic {

    /**
     * Rotate point around given center(pivot point) and return the result point, a existing PointF object
     * can be given so no new object has to be created each time the method is called.
     * @param cx center point x coordinate
     * @param cy center point y coordinate
     * @param x point that will be rotated x coordinate
     * @param y point that will be rotated y coordinate
     * @param angle angle of the rotation in degrees
     * @param antiClockwise if the rotation should be done anti-clockwise
     * @param result existing PointF object where the result with the new location of the rotated point will be saved
     */
    fun rotate(cx: Float, cy: Float, x: Float, y: Float, angle: Float, antiClockwise: Boolean = false, result: PointF = PointF()): PointF {

        // if the angle is zero return the same point
        if (angle == 0f) {
            result.x = x
            result.y = y
            return result
        }

        // convert to radians and take into account the direction of the rotation
        val radians = if (antiClockwise) {
            (Math.PI / 180) * angle
        } else {
            (Math.PI / -180) * angle
        }

        // calculate the new position of the rotated point
        val cos = Math.cos(radians).toFloat()
        val sin = Math.sin(radians).toFloat()
        result.x = (cos * (x - cx)) + (sin * (y - cy)) + cx
        result.y = (cos * (y - cy)) - (sin * (x - cx)) + cy

        return result
    }

    /**
     * Concat two float array, at given index and values per index. This method is used to add new coordinate to
     * a existing float array, for example if new line is added it has 2 vertex with 2 x,y coordinate values for
     * each vertex, that gives total of 4 coordinate values per line.
     * @param i index where to start putting the values from the other array
     * @param array the other array, that will be merged with current float array
     * @param valuesPerIndex how many values from the array are transferred per index
     */
    fun FloatArray.concat(i: Int, array: FloatArray, valuesPerIndex: Int): FloatArray {

        val result = FloatArray(this.size + array.size)

        // copy range [0, i] from original array
        for (j in 0 until i * valuesPerIndex) {
            result[j] = this[j]
        }

        // copy range [i, i + array.size]
        val start = i * valuesPerIndex
        for (j in array.indices) {
            result[start + j] = array[j]
        }

        // copy range [i + array.size, i + array.size + this.size]
        for (j in i * valuesPerIndex until this.size) {
            result[array.size + j] = this[j]
        }

        return result
    }

    /**
     * Extension function for removing given number of elements from start index, from array
     * @param i index where to start deleting the values from the array
     * @param valuesPerIndex how many values from the array are deleted per index
     */
    fun FloatArray.delete(i: Int, valuesPerIndex: Int): FloatArray {

        // copy range [0, i)
        val result = FloatArray(this.size - valuesPerIndex)
        for (j in 0 until i * valuesPerIndex) {
            result[j] = this[j]
        }

        // copy from [i+valuesPerIndex, this.size)
        for (j in i * valuesPerIndex + valuesPerIndex until this.size) {
            result[j - valuesPerIndex] = this[j]
        }

        return result
    }

    /**
     * Generate color for multiple shapes, by multiplying the color
     * @param color color that will be copied
     * @param numberElements number of elements that will have this color
     */
    fun generateColors(color: Int, numberElements: Int): IntArray {
        return IntArray(numberElements) {
            color
        }
    }

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
     * text using input stream.
     * @param context activity context
     * @param resourceId resource id to the raw file
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
     * Load OpenGL texture from drawable resources and return the handler
     * @param context activity context
     * @param resourceId resource id for the drawable file
     */
    fun loadTexture(context: Context, resourceId: Int): Int {

        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            val options = BitmapFactory.Options()
            options.inScaled = true // No pre-scaling
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            // read in the resource
            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)
            bitmap.setHasAlpha(true)

            // bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

            // set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

            // load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

            // recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle()
        }

        if (textureHandle[0] == 0) {
            throw RuntimeException("Error loading texture.")
        }

        return textureHandle[0]
    }

    /**
     * Load OpenGl texture from bitmap and return the handler
     * @param bitmap bitmap that will be used as texture
     * @param recycleBitmap whether to recycle the bitmaps after generating the texture handler
     */
    fun loadTexture(bitmap: Bitmap, recycleBitmap: Boolean = true): Int {

        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            val options = BitmapFactory.Options()
            options.inScaled = true // No pre-scaling

            // bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

            // set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)

            // load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

            // recycle the bitmap, since its data has been loaded into OpenGL.
            if (recycleBitmap) {
                bitmap.recycle()
            }
        }

        if (textureHandle[0] == 0) {
            throw RuntimeException("Error loading texture.")
        }

        return textureHandle[0]
    }

    /**
     * Generate a OpenGL program using the multiple vertex and fragment shader
     * string generated from the raw *glsl files and return its index. Used for
     * drawing multiple shapes with different color for each shape.
     */
    fun setMultipleColorsProgram(): Int {
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexMultipleColorsShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentMultipleColorsShaderCode)

        val program = GLES20.glCreateProgram()            // create empty OpenGL ES Program
        GLES20.glAttachShader(program, vertexShader)      // add the vertex shader to program
        GLES20.glAttachShader(program, fragmentShader)    // add the fragment shader to program
        GLES20.glLinkProgram(program)                     // creates OpenGL ES program executables

        return program
    }

    /**
     * Generate a OpenGL program using the ordinary vertex and fragment shader
     * string generated from the raw *glsl files and return its index. Used for
     * drawing shapes with single color.
     */
    fun setSingleColorsProgram(): Int {
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        val program = GLES20.glCreateProgram()            // create empty OpenGL ES Program
        GLES20.glAttachShader(program, vertexShader)      // add the vertex shader to program
        GLES20.glAttachShader(program, fragmentShader)    // add the fragment shader to program
        GLES20.glLinkProgram(program)                     // creates OpenGL ES program executables

        return program
    }

    /**
     * Generate a OpenGL program using the texture vertex and fragment shader
     * string generated from the raw *glsl files and return its index. Used
     * in generating texture on to a shape.
     */
    fun setTextureProgram(): Int {
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexTextureShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentTextureShaderCode)

        val program = GLES20.glCreateProgram()            // create empty OpenGL ES Program
        GLES20.glAttachShader(program, vertexShader)      // add the vertex shader to program
        GLES20.glAttachShader(program, fragmentShader)    // add the fragment shader to program
        GLES20.glLinkProgram(program)                     // creates OpenGL ES program executables

        GLES20.glBindAttribLocation(program, 0, "a_TexCoordinate")
        GLES20.glLinkProgram(program)

        return program
    }

    /**
     * Generate a resized bitmap from an exsting bitmap
     * @param bitmap original bitmap tha will be scaled
     * @param newWidth new width for the bitmap that will be created
     * @param newHeight new height for the bitmap that will be created
     */
    fun getResizedBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int, recycleBitmap: Boolean = false): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height

        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)

        val resizedBitmap: Bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
        if (recycleBitmap) {
            bitmap.recycle()
        }
        return resizedBitmap
    }


    /**
     * Scale a non-power of 2 bitmap to fit in bitmap with power of 2 size that means bitmap with
     * any of the following sized: 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096.
     * If bitmap is with non-power size or it is bigger than 4096, the texture is not shown.
     * @param bitmap bitmap that will be scaled in a size of power of two
     * @param backgroundColor background color behind the scaled bitmap
     */
    fun scaleBitmapPowerOfTwo(bitmap: Bitmap, backgroundColor: Int = Color.TRANSPARENT): BitmapInfo {

        val width = bitmap.width
        val height = bitmap.height

        // the maximum power of 2 size
        val maxSide = 4096

        // get the new size
        val newWidth = findPowerOfTwoSize(width)
        val newHeight = findPowerOfTwoSize(height)

        val finalWidth = Math.min(newWidth, maxSide)
        val finalHeight = Math.min(newHeight, maxSide)

        // create the new bitmap and draw the image in center
        val newBitmap = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply {
            isAntiAlias = true
        }

        val canvas = Canvas(newBitmap).apply {
            drawColor(backgroundColor)
        }

        val ratioX = finalWidth / width.toFloat()
        val ratioY = finalHeight / height.toFloat()

        // get x,y offset, width and height for the scaled bitmap
        val scaledBitmapWidth: Float
        val scaledBitmapHeight: Float
        val offsetX: Float
        val offsetY: Float
        if (ratioX < ratioY) {
            scaledBitmapWidth = width * ratioX
            scaledBitmapHeight = height * ratioX

            offsetX = 0f
            offsetY = (finalHeight / 2f - scaledBitmapHeight / 2f)
        } else {
            scaledBitmapWidth = width * ratioY
            scaledBitmapHeight = height * ratioY

            offsetX = (finalWidth / 2f - scaledBitmapWidth / 2f)
            offsetY = 0f
        }

        canvas.drawBitmap(
            bitmap, Rect(0, 0, width, height),
            Rect(offsetX.toInt(), offsetY.toInt(), scaledBitmapWidth.toInt() + offsetX.toInt(), scaledBitmapHeight.toInt() + offsetY.toInt()),
            paint
        )

        return BitmapInfo(
            bitmap = newBitmap, bitmapWidthNoTransparent = scaledBitmapWidth, bitmapHeightNoTransparent = scaledBitmapHeight,
            bitmapWidth = newBitmap.width, bitmapHeight = newBitmap.height
        )
    }

    /**
     * Find the nearest size that is power of two: 2, 4, 8, 16, 32...
     * @param size side size which will be used to determine the nearest
     * size that is powwer of two
     */
    fun findPowerOfTwoSize(size: Int): Int {
        var increase = 1
        while (true) {
            increase *= 2

            if (increase >= size) {
                return increase
            }
        }
    }

    /**
     * Generate a circle handle, this is a OpenGL texture handler which is generated by a
     * bitmap, which on its own is generated from a canvas with given parameters as radius,
     * stroke width and fill and stroke colors.
     * @param radius circle radius
     * @param strokeWidth stroke width for the circle
     * @param fillColor fill color for the circle
     * @param strokeColor stroke color for the circle
     * @param backgroundColor background color behind the circle
     */
    fun generateCircleHandle(radius: Float = 10f, strokeWidth: Float = 12f, fillColor: Int = Color.WHITE, strokeColor: Int = Color.BLACK, backgroundColor: Int = Color.TRANSPARENT): Int {

        val d = radius * 2 + strokeWidth
        val bitmap = Bitmap.createBitmap(d.toInt(), d.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = true
        }

        // background color
        if (backgroundColor != Color.TRANSPARENT) {
            canvas.drawColor(backgroundColor)
        }

        // fill
        paint.style = Paint.Style.FILL
        paint.color = fillColor
        canvas.drawCircle(radius + strokeWidth / 2f, radius + strokeWidth / 2f, radius, paint)

        // stroke
        paint.style = Paint.Style.STROKE
        paint.color = strokeColor
        paint.strokeWidth = strokeWidth
        canvas.drawCircle(radius + strokeWidth / 2f, radius + strokeWidth / 2f, radius, paint)

        return loadTexture(bitmap)
    }

    /**
     * Generate circle for the deleting tool element, that has radial gradient
     * used to show the are where all points inside will be deleted.
     * @param radius circle radius
     * @param colors colors for the radial gradient
     * @param stops color stops for the radial gradient
     * @param shadowColor color for the shadow
     * @param shadowRadius radius for the shadow
     * @param backgroundColor background color behind the circle
     */
    fun generateCircleAreaHandle(
        radius: Float = 10f, colors: IntArray, stops: FloatArray,
        shadowColor: Int = Color.TRANSPARENT, shadowRadius: Float = 1f, backgroundColor: Int = Color.TRANSPARENT
    ): Int {

        val radialGradient = RadialGradient(radius, radius, radius, colors, stops, Shader.TileMode.CLAMP)

        val diameter = (radius * 2).toInt()
        val bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            shader = radialGradient
            isDither = false
        }

        // background color
        if (backgroundColor != Color.TRANSPARENT) {
            canvas.drawColor(backgroundColor)
        }

        // shadow
        if (shadowColor != Color.TRANSPARENT) {
            paint.setShadowLayer(shadowRadius, 0f, 0f, shadowColor)
        }
        canvas.drawCircle(radius, radius, radius, paint)

        return loadTexture(bitmap)
    }

    /**
     * Generate the transparent background. That can be used by the user to
     * instantiate the difference between transparent area and are with color.
     * @param firstColor first color for the transparent background
     * @param secondColor second color for the rectangle of the transparent background
     * @param rectWidth width of each rectangle, that will be drawn with the second color
     */
    fun generateTransparentBackgroundHandle(firstColor: Int, secondColor: Int, rectWidth: Float): Int {
        val width = DEVICE_WIDTH.toInt()
        val height = DEVICE_HEIGHT.toInt()

        // generate bitmap and draw the the background with first color
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        canvas.drawColor(firstColor)

        // set style for the rectangles with second color
        val paint = Paint().apply {
            style = Paint.Style.FILL
            color = secondColor
        }

        // draw the rectangles
        val rectSize = rectWidth.toInt()
        for (i in 0 until width / rectSize + 1) {
            val topOffset = if (i % 2 == 0) 0 else rectSize

            for (j in 0 until height / rectSize + 1 step 2) {
                val top = topOffset + j * rectSize
                val left = i * rectSize
                val right = left + rectSize
                val bottom = top + rectSize
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
            }
        }

        return loadTexture(bitmap)
    }

    /**
     * Set all shader strings for shapes that use single color, for
     * multiple shapes that use multiple colors ofr each shape separately and
     * for shapes that sue texture.
     * @param context activity context
     */
    fun setShaderStrings(context: Context) {
        vertexShaderCode = readTextFileFromResource(context, R.raw.vertex_single_color_shader)
        fragmentShaderCode = readTextFileFromResource(context, R.raw.fragment_single_color_shader)
        vertexTextureShaderCode = readTextFileFromResource(context, R.raw.vertex_texture_shader)
        fragmentTextureShaderCode = readTextFileFromResource(context, R.raw.fragment_texture_shader)
        vertexMultipleColorsShaderCode = readTextFileFromResource(context, R.raw.vertex_mutiple_colors_shader)
        fragmentMultipleColorsShaderCode = readTextFileFromResource(context, R.raw.fragment_multiple_colors_shader)
    }

    /**
     * Distance from a point (x,y) to line segment connecting the
     * two points (x1,y1) and (x2,y2)
     * @param x x coordinate of the point
     * @param y y coordinate of the point
     * @param x1 x coordinate of first line point
     * @param y1 y coordinate of first line point
     * @param x2 x coordinate of second line point
     * @param y2 y coordinate of second line point
     */
    fun distancePointToLine(x: Float, y: Float, x1: Float, y1: Float, x2: Float, y2: Float): Float {

        val A = x - x1
        val B = y - y1
        val C = x2 - x1
        val D = y2 - y1

        val dot = A * C + B * D
        val lenSq = C * C + D * D
        var param = -1f

        //in case of 0 length line
        if (lenSq != 0f) {
            param = dot / lenSq
        }

        var xx = 0f
        var yy = 0f

        if (param < 0) {
            xx = x1
            yy = y1
        } else if (param > 1) {
            xx = x2
            yy = y2
        } else {
            xx = x1 + param * C
            yy = y1 + param * D
        }

        val dx = x - xx
        val dy = y - yy
        return Math.sqrt(0.0 + dx * dx + dy * dy).toFloat()
    }

    /**
     * Bitmap info class, that includes information about the bitmap that is opened by the user,
     * or received as cache when the user closes the app, to recreate the bitmap.
     * @param bitmap bitmap with sides that are power of two
     * @param bitmapPixelData pixel data of the scaled power of two bitmap, in which color are represented as integers
     * @param bitmapByteBufferData byte buffer data from the bitmap, used to recreate the bitmap from cache
     * @param bitmapWidthNoTransparent width of the scaled power of two bitmap, that does not include transparent area
     * @param bitmapHeightNoTransparent height of the scaled power of two bitmap, that does not include transparent area
     * @param bitmapWidth bitmap width of the scaled power of two bitmap, used for creating bitmap by pixel data
     * @param bitmapHeight bitmap height of the scaled power of two bitmap, used for creating bitmap by pixel data
     * @param lowerResolutionBitmapPixelData pixel data of the lower resolution bitmap from the scaled power of two bitmap
     * @param lowerResolutionBitmapWidth bitmap width of the lower resolution bitmap
     * @param lowerResolutionBitmapHeight bitmap height of the lower resolution bitmap
     * @param imageHandle handle for the OpenGL image, that holds the texture
     */
    class BitmapInfo(
        var bitmap: Bitmap? = null,
        var bitmapPixelData: IntArray = intArrayOf(),
        var bitmapByteBufferData: ByteArray = byteArrayOf(),
        var bitmapWidthNoTransparent: Float = 0f,
        var bitmapHeightNoTransparent: Float = 0f,
        var bitmapWidth: Int = 0,
        var bitmapHeight: Int = 0,
        var lowerResolutionBitmapPixelData: IntArray = intArrayOf(),
        var lowerResolutionBitmapWidth: Int = 0,
        var lowerResolutionBitmapHeight: Int = 0,
        var imageHandle: Int = -1
    )

    fun setComponents(width: Float, height: Float) {

        // set constants that are used by other class
        DEVICE_WIDTH = width
        DEVICE_HEIGHT = height
        DEVICE_HALF_WIDTH = width / 2f
        DEVICE_HALF_HEIGHT = height / 2f
        RATIO = (DEVICE_WIDTH / DEVICE_HEIGHT)
    }

    var DEVICE_HALF_WIDTH: Float = 0f                                   // half of the device width
    var DEVICE_HALF_HEIGHT: Float = 0f                                  // half of the device height
    var DEVICE_WIDTH: Float = 0f                                        // device width
    var DEVICE_HEIGHT: Float = 0f                                       // device height
    var RATIO: Float = 0f                                               // device width to height (width/height) ratio

    const val NEAR: Int = 3                                             // near from the frustum, since it is 1 and not 3 as presented by the android team

    var ENABLE_ALPHA: Boolean = true                                    // if alpha transparency is enabled
    var ENABLE_ANTIALIASING: Boolean = false                            // if antialiasing is enabled

    var vertexShaderCode: String = ""                                   // vertex shader string for shapes that are using single color
    var fragmentShaderCode: String = ""                                 // fragment shader string for shapes that are using single color
    var vertexTextureShaderCode: String = ""                            // vertex shader string for shapes that are using texture
    var fragmentTextureShaderCode: String = ""                          // fragment shader string for shapes that are using texture
    var vertexMultipleColorsShaderCode: String = ""                     // vertex shader string for shapes that are using multiple colors for each shape
    var fragmentMultipleColorsShaderCode: String = ""                   // fragment shader string for shapes that are using multiple colors for each shape
}
