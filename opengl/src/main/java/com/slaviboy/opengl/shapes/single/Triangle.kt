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
package com.slaviboy.opengl.shapes.single

import android.graphics.Color
import com.slaviboy.opengl.main.OpenGLMatrixGestureDetector
import com.slaviboy.opengl.shapes.multiple.Triangles

/**
 * A 2D representation of single triangle, using OpenGL 2.0
 * @param x1 first point x coordinate of the triangle
 * @param y1 first point y coordinate of the triangle
 * @param x2 second point x coordinate of the triangle
 * @param y2 second point y coordinate of the triangle
 * @param x3 third point x coordinate of the triangle
 * @param y3 third point y coordinate of the triangle
 * @param color color for the triangle
 * @param gestureDetector gesture detect, with the transformation that will be applied when generating the coordinates for the triangle
 * @param preloadProgram preloaded program in case we need to create a triangle, when the OpenGL Context is not available
 */
class Triangle(
    x1: Float = 0f, y1: Float = 0f,
    x2: Float = 100f, y2: Float = 0f,
    x3: Float = 100f, y3: Float = 100f,
    color: Int = Color.BLACK,
    strokeWidth: Float = 1f,
    isVisible: Boolean = true,
    gestureDetector: OpenGLMatrixGestureDetector,
    preloadProgram: Int = -1,
    style: Int = STYLE_FILL
) : Triangles(floatArrayOf(x1, y1, x2, y2, x3, y3), intArrayOf(color), strokeWidth, isVisible, gestureDetector, preloadProgram, style, true) {

    var x1: Float = x1
        set(value) {
            field = value
            change(0, value, y1, x2, y2, x3, y3)
        }

    var y1: Float = y1
        set(value) {
            field = value
            change(0, x1, value, x2, y2, x3, y3)
        }

    var x2: Float = x2
        set(value) {
            field = value
            change(0, x1, y1, value, y2, x3, y3)
        }

    var y2: Float = y2
        set(value) {
            field = value
            change(0, x1, y1, x2, value, x3, y3)
        }

    var x3: Float = x3
        set(value) {
            field = value
            change(0, x1, y1, x2, y2, value, y3)
        }

    var y3: Float = y3
        set(value) {
            field = value
            change(0, x1, y1, x2, y2, x3, value)
        }

    var color: Int = color
        set(value) {
            field = value
            setColor(0, value)
        }
}