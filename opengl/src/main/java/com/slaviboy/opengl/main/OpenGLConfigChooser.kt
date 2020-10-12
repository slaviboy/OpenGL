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

import android.opengl.GLSurfaceView.EGLConfigChooser
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay

/**
 * ConfigChooser for applying antialiasing for the OpenGL drawing, but it might get a little bit slower,
 * since extra calculation are needed for the antialiasing to take effect.
 */
class OpenGLConfigChooser : EGLConfigChooser {

    override fun chooseConfig(egl: EGL10, display: EGLDisplay): EGLConfig? {
        mValue = IntArray(1)

        // try to find a normal multi sample configuration first.
        var configSpec = intArrayOf(
            EGL10.EGL_RED_SIZE, 5,
            EGL10.EGL_GREEN_SIZE, 6,
            EGL10.EGL_BLUE_SIZE, 5,
            EGL10.EGL_DEPTH_SIZE, 16,
            EGL10.EGL_RENDERABLE_TYPE, 4,
            EGL10.EGL_SAMPLE_BUFFERS, 1,
            EGL10.EGL_SAMPLES, 2,
            EGL10.EGL_NONE
        )

        egl.eglChooseConfig(
            display, configSpec, null, 0,
            mValue
        )

        var numConfigs = mValue[0]
        if (numConfigs <= 0) {
            // no normal multi sampling config was found. Try to create a
            // converge multi sampling configuration, for the nVidia Tegra2.
            // See the EGL_NV_coverage_sample documentation.
            val EGL_COVERAGE_BUFFERS_NV = 0x30E0
            val EGL_COVERAGE_SAMPLES_NV = 0x30E1
            configSpec = intArrayOf(
                EGL10.EGL_RED_SIZE, 5,
                EGL10.EGL_GREEN_SIZE, 6,
                EGL10.EGL_BLUE_SIZE, 5,
                EGL10.EGL_DEPTH_SIZE, 16,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL_COVERAGE_BUFFERS_NV, 1,
                EGL_COVERAGE_SAMPLES_NV, 2,
                EGL10.EGL_NONE
            )

            egl.eglChooseConfig(
                display, configSpec, null, 0,
                mValue
            )

            numConfigs = mValue[0]
            if (numConfigs <= 0) {
                // Give up, try without multi sampling.
                configSpec = intArrayOf(
                    EGL10.EGL_RED_SIZE, 5,
                    EGL10.EGL_GREEN_SIZE, 6,
                    EGL10.EGL_BLUE_SIZE, 5,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_RENDERABLE_TYPE, 4,
                    EGL10.EGL_NONE
                )

                egl.eglChooseConfig(
                    display, configSpec, null, 0,
                    mValue
                )

                numConfigs = mValue[0]
                require(numConfigs > 0) { "No configs match configSpec" }
            } else {
                mUsesCoverageAa = true
            }
        }

        // get all matching configurations.
        val configs = arrayOfNulls<javax.microedition.khronos.egl.EGLConfig>(numConfigs)
        require(
            egl.eglChooseConfig(
                display, configSpec, configs, numConfigs,
                mValue
            )
        ) { "data eglChooseConfig failed" }

        // CAUTION! eglChooseConfigs returns configs with higher bit depth
        // first: Even though we asked for rgb565 configurations, rgb888
        // configurations are considered to be "better" and returned first.
        // You need to explicitly filter the data returned by eglChooseConfig!
        var index = -1
        for (i in configs.indices) {
            if (findConfigAttrib(egl, display, configs[i], EGL10.EGL_RED_SIZE, 0) == 5) {
                index = i
                break
            }
        }
        if (index == -1) {
            //Log.w(kTag, "Did not find sane config, using first")
        }
        return (if (configs.isNotEmpty()) configs[index] else null) ?: throw IllegalArgumentException("No config chosen")
    }

    private fun findConfigAttrib(
        egl: EGL10, display: EGLDisplay,
        config: EGLConfig?, attribute: Int, defaultValue: Int
    ): Int {
        return if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
            mValue[0]
        } else defaultValue
    }

    fun usesCoverageAa(): Boolean {
        return mUsesCoverageAa
    }

    private lateinit var mValue: IntArray
    private var mUsesCoverageAa = false
}