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
import com.slaviboy.opengl.shapes.multiple.Rectangles

/**
 * A 2D representation of single rectangle, using OpenGL 2.0
 * @param x top-left rectangle corner x coordinate
 * @param y top-left  rectangle corner y coordinate
 * @param width rectangle width
 * @param height rectangle height
 * @param color color of the rectangle as integer representation
 * @param strokeWidth stroke width if the rectangle is stroked and not filled with the color
 * @param isVisible if rectangle should be drawn, or not when the methods draw() is called
 * @param gestureDetector gesture detect, with the transformation that will be applied when generating the coordinates for the rectangle
 * @param preloadProgram preloaded program in case we need to create a rectangle, when the OpenGL Context is not available
 * @param style style for the rectangle if it should be filled with the color or stroked
 */
class Rectangle(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    color: Int = Color.BLACK,
    strokeWidth: Float = 1f,
    isVisible: Boolean = true,
    gestureDetector: OpenGLMatrixGestureDetector,
    preloadProgram: Int = -1,
    style: Int = STYLE_FILL
) : Rectangles(floatArrayOf(x, y, width, height), intArrayOf(color), strokeWidth, isVisible, gestureDetector, preloadProgram, style, true) {

    var x: Float = x
        set(value) {
            field = value
            change(0, value, y, width, height)
        }

    var y: Float = y
        set(value) {
            field = value
            change(0, x, value, width, height)
        }

    var width: Float = width
        set(value) {
            field = value
            change(0, x, y, value, height)
        }

    var height: Float = height
        set(value) {
            field = value
            change(0, x, y, width, value)
        }

    var color: Int = color
        set(value) {
            field = value
            setColor(0, value)
        }
}