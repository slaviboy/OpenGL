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

import android.graphics.Color

/**
 * Set OpenGL color using rgba values
 * @param r red [0,255]
 * @param g green [0,255]
 * @param b blue [0,255]
 * @param a alpha [0,255]
 */
class OpenGLColor(r: Int = 0, g: Int = 0, b: Int = 0, a: Int = 255) {

    /**
     * Create open gl color using float array with values
     * that are between [0,1]
     * @param array array with 4 float values in range [0,1]
     */
    constructor(array: FloatArray) : this((array[0] * 255).toInt(), (array[1] * 255).toInt(), (array[2] * 255).toInt(), (array[3] * 255).toInt())

    /**
     * Create open gl color using color passed as integer
     * representation.
     * @param rgba integer representation of a color
     */
    constructor(rgba: Int) : this(Color.red(rgba), Color.green(rgba), Color.blue(rgba), Color.alpha(rgba))

    var value: FloatArray = FloatArray(4)

    var r: Int = r
        set(value) {
            field = value
            this.value[0] = value / 255f
        }

    var g: Int = g
        set(value) {
            field = value
            this.value[1] = value / 255f
        }

    var b: Int = b
        set(value) {
            field = value
            this.value[2] = value / 255f
        }

    var a: Int = a
        set(value) {
            field = value
            this.value[3] = value / 255f
        }

    init {
        setRGBA(r, g, b, a)
    }

    fun setRGBA(r: Int, g: Int, b: Int, a: Int) {
        this.r = r
        this.g = g
        this.b = b
        this.a = a
    }

    fun getInt(): Int {
        return Color.argb(a, r, g, b)
    }

    override fun toString(): String {
        return "r:$r, g:$g, b:$b, a:$a"
    }

    companion object {

        val WHITE: FloatArray = floatArrayOf(1f, 1f, 1f, 1f)
        val BLACK: FloatArray = floatArrayOf(0f, 0f, 0f, 1f)
        val RED: FloatArray = floatArrayOf(1f, 0f, 0f, 1f)
        val BLUE: FloatArray = floatArrayOf(0f, 0f, 1f, 1f)
        val GREEN: FloatArray = floatArrayOf(0f, 1f, 0f, 1f)
        val YELLOW: FloatArray = floatArrayOf(1f, 1f, 0f, 1f)
        val MAGENTA: FloatArray = floatArrayOf(1f, 0f, 1f, 1f)
        val CYAN: FloatArray = floatArrayOf(0f, 1f, 1f, 1f)
        val TRANSPARENT: FloatArray = floatArrayOf(0f, 0f, 0f, 0f)

        /**
         * Return array from the r,g,b,a values
         * @param r red [0,255]
         * @param g green [0,255]
         * @param b blue [0,255]
         * @param a alpha [0,255]
         */
        fun getArray(r: Int, g: Int, b: Int, a: Int): FloatArray {
            return floatArrayOf(r / 255f, g / 255f, b / 255f, a / 255f)
        }

        /**
         * Return array from color set as integer values
         * @param color color as integer representation
         */
        fun getArray(color: Int): FloatArray {

            /*val r = Color.red(color)/ 255f
            val g = Color.green(color)/ 255f
            val b = Color.blue(color)/ 255f
            val a = Color.alpha(color)/ 255f*/

            val r: Float = (color shr 16 and 0xff) / 255.0f
            val g: Float = (color shr 8 and 0xff) / 255.0f
            val b: Float = (color and 0xff) / 255.0f
            val a: Float = (color shr 24 and 0xff) / 255.0f

            return floatArrayOf(r, g, b, a)
        }

        /**
         * Return array from hex value in formats:
         * #RRGGBB
         * #AARRGGBB
         */
        fun getArray(hex: String): FloatArray {
            val color = Color.parseColor(hex)
            return getArray(color)
        }

        /**
         * Generate random OpenGL color object
         */
        fun random(includeAlpha: Boolean = false): OpenGLColor {
            val rgbRange = 0..255
            val a = if (includeAlpha) {
                rgbRange.random()
            } else {
                255
            }
            return OpenGLColor(rgbRange.random(), rgbRange.random(), rgbRange.random(), a)
        }
    }
}