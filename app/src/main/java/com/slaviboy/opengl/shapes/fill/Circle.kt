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

package com.slaviboy.opengl.shapes.fill

/**
 * A 2D circle for use as a drawn object in OpenGL ES 2.0.
 * @param x circle center x coordinate
 * @param y circle center y coordinate
 * @param r circle radius
 */
class Circle(x: Float = 0f, y: Float = 0f, r: Float = 100f) : RegularPolygon(x, y, r) {

    init {
        numberVertices = 360
        startAngle = 0
    }
}