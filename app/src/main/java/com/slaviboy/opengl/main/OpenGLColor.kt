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
 * @param a alpha [0,100]
 */
class OpenGLColor(r: Int = 0, g: Int = 0, b: Int = 0, a: Int = 100) {

    constructor(array: FloatArray) : this((array[0] * 255).toInt(), (array[1] * 255).toInt(), (array[2] * 255).toInt(), (array[3] * 100).toInt())

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
            this.value[3] = value / 100f
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
        return Color.argb((a * (255 / 100f)).toInt(), r, g, b)
    }

    override fun toString(): String {
        return "$r, $g, $b, $a"
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
         * @param a alpha [0,100]
         */
        fun getArray(r: Int = 0, g: Int = 0, b: Int = 0, a: Int): FloatArray {
            return floatArrayOf(r / 255f, g / 255f, b / 255f, a / 100f)
        }

        /**
         * Return array from hex value in formats:
         * #RRGGBB
         * #AARRGGBB
         */
        fun getArray(hex: String): FloatArray {

            val color = Color.parseColor(hex)
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            val a = Color.alpha(color)
            return floatArrayOf(r / 255f, g / 255f, b / 255f, a / 225f)
        }
    }
}