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
import com.slaviboy.opengl.shapes.multiple.RegularPolygons

/**
 * A 2D representation of single regular polygon, using OpenGL 2.0
 * @param x regular polygon center x coordinate
 * @param y regular polygon center y coordinate
 * @param radius regular polygon radius (distance between the center and the vertices)
 * @param angle start angle of the polygon vertices
 * @param color color of the regular polygon as integer representation
 * @param strokeWidth stroke width if the regular polygon is stroked and not filled with the color
 * @param isVisible if regular polygon should be drawn, or not when the methods draw() is called
 * @param gestureDetector gesture detect, with the transformation that will be applied when generating the coordinates for the regular polygon
 * @param preloadProgram preloaded program in case we need to create a regular polygon, when the OpenGL Context is not available
 * @param style style for the regular polygon if it should be filled with the color or stroked
 * @param numberVertices the number of initial vertices for the polygon, triangles:3, rectangles:4, pentagon:5, ...
 */
open class RegularPolygon(
    x: Float = 0f,
    y: Float = 0f,
    radius: Float = 100f,
    angle: Float = 0f,
    color: Int = Color.BLACK,
    strokeWidth: Float = 1f,
    isVisible: Boolean = true,
    gestureDetector: OpenGLMatrixGestureDetector,
    preloadProgram: Int = -1,
    style: Int = STYLE_FILL,
    innerDepth: Float = 0f,
    numberVertices: Int = 4
) : RegularPolygons(floatArrayOf(x, y, radius, angle), intArrayOf(color), strokeWidth, isVisible, gestureDetector, preloadProgram, style, innerDepth, numberVertices, true) {

    var x: Float = x
        set(value) {
            field = value
            coordinatesInfo[0] = value
            change(0, value, y, radius, angle)
        }

    var y: Float = y
        set(value) {
            field = value
            coordinatesInfo[1] = value
            change(0, x, value, radius, angle)
        }

    var radius: Float = radius
        set(value) {
            field = value
            coordinatesInfo[2] = value
            change(0, x, y, value, angle)
        }

    var angle: Float = angle
        set(value) {
            field = value
            coordinatesInfo[3] = value
            change(0, x, y, radius, value)
        }

    var color: Int = color
        set(value) {
            field = value
            setColor(0, value)
        }
}