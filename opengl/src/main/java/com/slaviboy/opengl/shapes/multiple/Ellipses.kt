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
import com.slaviboy.opengl.shapes.Shapes

/**
 * A 2D ellipses for use as a drawn object using OpenGL 2.0
 * @param coordinatesInfo array holding information about the ellipses coordinates in format [cx1,cy1,rx1,ry1,  cx2,cy2,rx2,ry2,...]),
 * first are the center point of the ellipses (cx,cy), then the radius of the ellipses and finally the start angle of the ellipses.
 * @param colors array with integer representation of the color for each ellipses (1 integer value per ellipses)
 * @param strokeWidth stroke width for the ellipses in case the style is set to STYLE_STROKE
 * @param isVisible if ellipses should be drawn, or not when the methods draw() is called
 * @param gestureDetector gesture detect, with the transformation that will be applied when generating the coordinates for the ellipses
 * @param preloadProgram preloaded program in case we need to create a ellipses, when the OpenGL Context is not available
 * @param style style for the ellipses if it should be filled with the color or stroked
 * @param numberOfVertices the number of initial vertices for the ellipses, triangles:3, rectangles:4, pentagon:5, ...
 * @param useSingleColor if single color will be set to all shapes, instead of passing array with colors for each shape separately
 */
open class Ellipses(
    coordinatesInfo: FloatArray = floatArrayOf(),
    colors: IntArray = intArrayOf(),
    strokeWidth: Float = 1f,
    isVisible: Boolean = true,
    gestureDetector: OpenGLMatrixGestureDetector,
    preloadProgram: Int = -1,
    val style: Int = STYLE_FILL,
    val numberOfVertices: Int = 360,
    useSingleColor: Boolean = false
) : Shapes(getEllipsesCoordinatesByStyle(style, coordinatesInfo, numberOfVertices), colors, strokeWidth, isVisible, gestureDetector, getTypeByStyle(style), getRegularPolygonsNumberOfVerticesFromStyle(style, numberOfVertices, 0f), preloadProgram, useSingleColor)
