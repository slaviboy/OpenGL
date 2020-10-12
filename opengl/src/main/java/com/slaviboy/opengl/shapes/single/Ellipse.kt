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
import com.slaviboy.opengl.shapes.multiple.Ellipses 

/**
 * A 2D representation of a single ellipse, using OpenGL ES2.0
 * @param x ellipse center x coordinate
 * @param y ellipse center y coordinate
 * @param rx ellipse horizontal radius
 * @param ry ellipse vertical radius
 * @param color color of the ellipse as integer representation
 * @param strokeWidth stroke width if the ellipse is stroked and not filled with the color
 * @param isVisible if ellipse should be drawn, or not when the methods draw() is called
 * @param gestureDetector gesture detect, with the transformation that will be applied when generating the coordinates for the ellipse
 * @param preloadProgram preloaded program in case we need to create a ellipse, when the OpenGL Context is not available
 * @param style style for the ellipse if it should be filled with the color or stroked
 */ 
open class Ellipse(
    x: Float = 0f,
    y: Float = 0f,
    rx: Float = 100f,
    ry: Float = 50f,
    color: Int = Color.BLACK,
    strokeWidth: Float = 1f,
    isVisible: Boolean = true,
    gestureDetector: OpenGLMatrixGestureDetector,
    preloadProgram: Int = -1,
    style: Int = STYLE_FILL,
    numberOfVertices: Int = 360
) : Ellipses(floatArrayOf(x, y, rx, ry), intArrayOf(color), strokeWidth, isVisible, gestureDetector, preloadProgram, style, numberOfVertices, true) {

    var x: Float = x
        set(value) {
            field = value
            change(0, value, y, rx, ry)
        }

    var y: Float = y
        set(value) {
            field = value
            change(0, x, value, rx, ry)
        }

    var rx: Float = rx
        set(value) {
            field = value
            change(0, x, y, value, ry)
        }

    var ry: Float = ry
        set(value) {
            field = value
            change(0, x, y, rx, value)
        }

    var color: Int = color
        set(value) {
            field = value
            setColor(0, value)
        }
}