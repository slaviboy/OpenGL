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

/**
 * A 2D representation of single circle, using OpenGL 2.0
 * @param x circle center x coordinate
 * @param y circle center y coordinate
 * @param radius circle radius (distance between the center and the vertices)
 * @param color color of the circle as integer representation
 * @param strokeWidth stroke width if the circle is stroked and not filled with the color
 * @param isVisible if circle should be drawn, or not when the methods draw() is called
 * @param gestureDetector gesture detect, with the transformation that will be applied when generating the coordinates for the circle
 * @param preloadProgram preloaded program in case we need to create a circle, when the OpenGL Context is not available
 * @param style style for the circle if it should be filled with the color or stroked
 */
class Circle(
    x: Float = 0f,
    y: Float = 0f,
    radius: Float = 100f,
    color: Int = Color.BLACK,
    strokeWidth: Float = 1f,
    isVisible: Boolean = true,
    gestureDetector: OpenGLMatrixGestureDetector,
    preloadProgram: Int = -1,
    style: Int = STYLE_FILL
) : RegularPolygon(x, y, radius, 0f, color, strokeWidth, isVisible, gestureDetector, preloadProgram, style, 0f, 360)
