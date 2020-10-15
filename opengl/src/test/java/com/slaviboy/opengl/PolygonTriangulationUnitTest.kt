package com.slaviboy.opengl

import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.slaviboy.opengl.shapes.PolygonTriangulation
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class PolygonTriangulationUnitTest {

    class FixtureValuesPack(
        var badDiagonals: FixtureValues, var building: FixtureValues, var dude: FixtureValues,
        var water: FixtureValues, var water2: FixtureValues, var water3: FixtureValues,
        var water3b: FixtureValues, var water4: FixtureValues, var waterHuge: FixtureValues,
        var waterHuge2: FixtureValues, var degenerate: FixtureValues, var badHole: FixtureValues,
        var emptySquare: FixtureValues, var issue16: FixtureValues, var issue17: FixtureValues,
        var steiner: FixtureValues, var issue29: FixtureValues, var issue34: FixtureValues,
        var issue35: FixtureValues, var selfTouching: FixtureValues, var outsideRing: FixtureValues
    )

    class FixtureValues(var coordinates: Array<Array<FloatArray>>, var triangles: Int, var errors: Float = 0f)

    @Test
    fun MainTest() {

        SimpleTest()
        TestFixtures()
    }

    fun TestFixtures() {

        val gson = Gson()
        val context: Context = ApplicationProvider.getApplicationContext()

        // load json file with expected test values
        val jsonValues = loadStringFromRawResource(context.resources, R.raw.fixtures)
        val fixtureValuesPack = gson.fromJson(jsonValues, FixtureValuesPack::class.java)

        TestFixture(fixtureValuesPack.badDiagonals)
        TestFixture(fixtureValuesPack.building)
        TestFixture(fixtureValuesPack.dude)
        TestFixture(fixtureValuesPack.water)
        TestFixture(fixtureValuesPack.water2)
        TestFixture(fixtureValuesPack.water3)
        TestFixture(fixtureValuesPack.water3b)
        TestFixture(fixtureValuesPack.water4)
        TestFixture(fixtureValuesPack.waterHuge)
        TestFixture(fixtureValuesPack.waterHuge2)
        TestFixture(fixtureValuesPack.degenerate)
        TestFixture(fixtureValuesPack.badHole)
        TestFixture(fixtureValuesPack.emptySquare)
        TestFixture(fixtureValuesPack.issue16)
        TestFixture(fixtureValuesPack.issue17)
        TestFixture(fixtureValuesPack.steiner)
        TestFixture(fixtureValuesPack.issue29)
        TestFixture(fixtureValuesPack.issue34)
        TestFixture(fixtureValuesPack.issue35)
        TestFixture(fixtureValuesPack.selfTouching)
        TestFixture(fixtureValuesPack.outsideRing)

    }

    fun TestFixture(fixtureValues: FixtureValues) {

        val data = PolygonTriangulation.flatten(fixtureValues.coordinates)
        val indices = PolygonTriangulation.triangulate(data.coordinates.toFloatArray(), data.holeIndices.toIntArray(), data.dimensions)
        val deviation = PolygonTriangulation.deviation(data.coordinates.toFloatArray(), data.holeIndices.toIntArray(), data.dimensions, indices)
        val expectedTriangles = fixtureValues.triangles
        val expectedDeviation = fixtureValues.errors

        val numTriangles = indices.size / 3
        assertThat(numTriangles).isEqualTo(expectedTriangles)

        if (expectedTriangles > 0) {
            //assertThat(deviation <= expectedDeviation).isTrue()
        }
    }

    fun SimpleTest() {

        // indices-2d
        var triangulation = PolygonTriangulation.triangulate(floatArrayOf(10f, 0f, 0f, 50f, 60f, 60f, 70f, 10f))
        assertThat(triangulation.toIntArray()).isEqualTo(intArrayOf(1, 0, 3, 3, 2, 1))

        // indices-3d
        triangulation = PolygonTriangulation.triangulate(
            coordinates = floatArrayOf(10f, 0f, 0f, 0f, 50f, 0f, 60f, 60f, 0f, 70f, 10f, 0f),
            dimension = 3
        )
        assertThat(triangulation.toIntArray()).isEqualTo(intArrayOf(1, 0, 3, 3, 2, 1))

        // empty
        triangulation = PolygonTriangulation.triangulate(floatArrayOf())
        assertThat(triangulation.toIntArray()).isEqualTo(intArrayOf())
    }

    companion object {

        /**
         * Load string from the raw folder using a resource id of the given file.
         * @param resources resource from the context
         * @param resId resource id of the file
         */
        fun loadStringFromRawResource(resources: Resources, resId: Int): String {
            val rawResource = resources.openRawResource(resId)
            val content = streamToString(rawResource)
            try {
                rawResource.close()
            } catch (e: IOException) {
                throw e
            }
            return content
        }

        /**
         * Read the file from the raw folder using input stream
         */
        private fun streamToString(inputStream: InputStream): String {
            var l: String?
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            try {
                while (bufferedReader.readLine().also { l = it } != null) {
                    stringBuilder.append(l)
                }
            } catch (e: IOException) {
            }
            return stringBuilder.toString()
        }
    }
}