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
package com.slaviboy.opengl.shapes.multiple

import com.slaviboy.opengl.main.OpenGLMatrixGestureDetector

/**
 * A 2D circles for use as a drawn object using OpenGL 2.0
 * @param coordinatesInfo array holding information about the circles coordinates in format [cx1,cy1,radius1,angle1,  cx2,cy2,radius2,angle2,...]),
 * first are the center point of the circles (cx,cy), then the radius of the circles and finally the start angle of the circles.
 * @param colors array with integer representation of the color for each circle (1 integer value per circle)
 * @param strokeWidth stroke width for the circles in case the style is set to STYLE_STROKE
 * @param isVisible if circles should be drawn, or not when the methods draw() is called
 * @param gestureDetector gesture detect, with the transformation that will be applied when generating the coordinates for the circles
 * @param preloadProgram preloaded program in case we need to create circles, when the OpenGL Context is not available
 * @param style style for the circles if it should be filled with the color or stroked
 * @param useSingleColor if single color will be set to all shapes, instead of passing array with colors for each shape separately
 */
class Circles(
    coordinatesInfo: FloatArray = floatArrayOf(),
    colors: IntArray = intArrayOf(),
    strokeWidth: Float = 1f,
    isVisible: Boolean = true,
    gestureDetector: OpenGLMatrixGestureDetector,
    preloadProgram: Int = -1,
    style: Int = STYLE_FILL,
    useSingleColor: Boolean = false
) : RegularPolygons(coordinatesInfo, colors, strokeWidth, isVisible, gestureDetector, preloadProgram, style, 0f, 360, useSingleColor)