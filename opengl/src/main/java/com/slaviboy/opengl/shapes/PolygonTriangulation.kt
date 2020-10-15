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
package com.slaviboy.opengl.shapes

import java.util.Collections.sort

/**
 * Simple class with methods for triangulating a irregular polygon given by array with coordinates, it
 * acquires the coordinates of the polygon and return the 3 point indices for each triangle. For the
 * points that form the triangles.
 */
object PolygonTriangulation {

    /**
     * Find the triangles that form a polygon
     * @param coordinates flat array of vertex coordinates in format: [x0,y0, x1,y1, x2,y2, ...]
     * @param holeIndices  array of hole indices if any (e.g. [5, 8] for a 12-vertex input would mean one hole with vertices 5–7 and another with 8–11)
     * @param dimension the number of coordinates per vertex in the input array (2 by default).
     */
    fun triangulate(coordinates: FloatArray, holeIndices: IntArray = intArrayOf(), dimension: Int = 2): List<Int> {

        val hasHoles = holeIndices.isNotEmpty()
        val outerLen = if (hasHoles) holeIndices[0] * dimension else coordinates.size
        var outerNode = linkedList(coordinates, 0, outerLen, dimension, true)
        if (outerNode == null || outerNode.next === outerNode.prev) {
            return emptyList()
        }

        val triangles: MutableList<Int> = ArrayList()
        var minX = 0f
        var minY = 0f
        var maxX = 0f
        var maxY = 0f
        var x: Float
        var y: Float
        var invSize = 0f

        if (hasHoles) {
            outerNode = eliminateHoles(coordinates, holeIndices, outerNode, dimension)
        }

        // if the shape is not too simple, we'll use z-order curve hash later; calculate polygon boundary-box
        if (coordinates.size > 80 * dimension) {
            maxX = coordinates[0]
            minX = maxX
            maxY = coordinates[1]
            minY = maxY
            var i = dimension
            while (i < outerLen) {
                x = coordinates[i]
                y = coordinates[i + 1]
                if (x < minX) minX = x
                if (y < minY) minY = y
                if (x > maxX) maxX = x
                if (y > maxY) maxY = y
                i += dimension
            }

            // minX, minY and invSize are later used to transform coords into integers for z-order calculation
            invSize = Math.max(maxX - minX, maxY - minY)
            invSize = if (invSize != 0f) 1 / invSize else 0f
        }
        triangulateLinked(outerNode, triangles, dimension, minX, minY, invSize, 0)
        return triangles
    }

    /**
     * Method that return a percentage difference between the polygon area and its triangulation area used
     * to verify correctness of triangulation
     */
    fun deviation(coordinates: FloatArray, holeIndices: IntArray = intArrayOf(), dimension: Int, triangles: List<Int>): Float {

        val hasHoles = holeIndices.isNotEmpty()
        val outerLen = if (hasHoles) holeIndices[0] * dimension else coordinates.size

        var polygonArea = Math.abs(signedArea(coordinates, 0, outerLen, dimension))
        if (hasHoles) {
            var i = 0
            val len = holeIndices.size
            while (i < len) {
                val start = holeIndices[i] * dimension
                val end = if (i < len - 1) holeIndices[i + 1] * dimension else coordinates.size
                polygonArea -= Math.abs(signedArea(coordinates, start, end, dimension))
                i++
            }
        }

        var trianglesArea = 0f
        var i = 0
        while (i < triangles.size) {
            val a = triangles[i] * dimension
            val b = triangles[i + 1] * dimension
            val c = triangles[i + 2] * dimension
            trianglesArea += Math.abs(
                (coordinates[a] - coordinates[c]) * (coordinates[b + 1] - coordinates[a + 1]) -
                        (coordinates[a] - coordinates[b]) * (coordinates[c + 1] - coordinates[a + 1])
            )
            i += 3
        }

        return if (polygonArea == 0f && trianglesArea == 0f) 0f else Math.abs((trianglesArea - polygonArea) / polygonArea)
    }


    /**
     * Create a circular doubly linked list from polygon points in the specified winding order
     */
    private fun linkedList(coordinates: FloatArray, start: Int, end: Int, dimension: Int, clockwise: Boolean): Node? {
        var i: Int
        var last: Node? = null

        if (clockwise == signedArea(coordinates, start, end, dimension) > 0) {
            i = start
            while (i < end) {
                last = insertNode(i, coordinates[i], coordinates[i + 1], last)
                i += dimension
            }
        } else {
            i = end - dimension
            while (i >= start) {
                last = insertNode(i, coordinates[i], coordinates[i + 1], last)
                i -= dimension
            }
        }

        if (last != null && equals(last, last.next)) {
            removeNode(last)
            last = last.next
        }
        return last
    }

    /**
     *  Eliminate collinear or duplicate points
     */
    private fun filterPoints(start: Node?, end: Node?): Node? {
        var end = end
        if (start == null) return null
        if (end == null) end = start
        var p = start
        var again: Boolean
        do {
            again = false
            if (!p!!.steiner && (equals(p, p.next) || area(p.prev, p, p.next) == 0f)) {
                removeNode(p)
                end = p.prev
                p = end
                if (p === p!!.next) break
                again = true
            } else {
                p = p.next
            }
        } while (again || p !== end)
        return end
    }

    /**
     * Main ear slicing loop which triangulates a polygon (given as a linked list)
     */
    private fun triangulateLinked(ear: Node?, triangles: MutableList<Int>, dim: Int, minX: Float, minY: Float, invSize: Float, pass: Int) {
        var ear: Node? = ear ?: return

        // interlink polygon nodes in z-order
        if (pass == 0 && invSize != 0f) indexCurve(ear!!, minX, minY, invSize)
        var stop = ear
        var prev: Node?
        var next: Node?

        // iterate through ears, slicing them one by one
        while (ear!!.prev !== ear!!.next) {
            prev = ear!!.prev
            next = ear.next
            if (if (invSize != 0f) isEarHashed(ear, minX, minY, invSize) else isEar(ear)) {
                // cut off the triangle
                triangles.add(prev!!.i / dim)
                triangles.add(ear.i / dim)
                triangles.add(next!!.i / dim)
                removeNode(ear)

                // skipping the next vertex leads to less sliver triangles
                ear = next.next
                stop = next.next
                continue
            }
            ear = next

            // if we looped through the whole remaining polygon and can't find any more ears
            if (ear === stop) {
                // try filtering points and slicing again
                if (pass == 0) {
                    triangulateLinked(filterPoints(ear, null), triangles, dim, minX, minY, invSize, 1)

                    // if this didn't work, try curing all small self-intersections locally
                } else if (pass == 1) {
                    ear = cureLocalIntersections(filterPoints(ear, null), triangles, dim)
                    triangulateLinked(ear, triangles, dim, minX, minY, invSize, 2)

                    // as a last resort, try splitting the remaining polygon into two
                } else if (pass == 2) {
                    splitTriangulate(ear, triangles, dim, minX, minY, invSize)
                }
                break
            }
        }
    }

    /**
     * Check whether a polygon node forms a valid ear with adjacent nodes
     */
    private fun isEar(ear: Node?): Boolean {
        val a = ear!!.prev
        val c = ear.next
        if (area(a, ear, c) >= 0) return false // reflex, can't be an ear

        // now make sure we don't have other points inside the potential ear
        var p = ear.next!!.next
        while (p !== ear.prev) {
            if (pointInTriangle(a!!.x, a.y, ear.x, ear.y, c!!.x, c.y, p!!.x, p.y) &&
                area(p.prev, p, p.next) >= 0
            ) return false
            p = p.next
        }
        return true
    }

    private fun isEarHashed(ear: Node?, minX: Float, minY: Float, invSize: Float): Boolean {
        val a = ear!!.prev
        val c = ear.next
        if (area(a, ear, c) >= 0) return false // reflex, can't be an ear

        // triangle bbox; min & max are calculated like this for speed
        val minTX = if (a!!.x < ear.x) if (a.x < c!!.x) a.x else c.x else if (ear.x < c!!.x) ear.x else c.x
        val minTY = if (a.y < ear.y) if (a.y < c.y) a.y else c.y else if (ear.y < c.y) ear.y else c.y
        val maxTX = if (a.x > ear.x) if (a.x > c.x) a.x else c.x else if (ear.x > c.x) ear.x else c.x
        val maxTY = if (a.y > ear.y) if (a.y > c.y) a.y else c.y else if (ear.y > c.y) ear.y else c.y

        // z-order range for the current triangle bbox;
        val minZ = zOrder(minTX, minTY, minX, minY, invSize)
        val maxZ = zOrder(maxTX, maxTY, minX, minY, invSize)
        var p = ear.prevZ
        var n = ear.nextZ

        // look for points inside the triangle in both directions
        while (p != null && p.z >= minZ && n != null && n.z <= maxZ) {
            if (p !== ear.prev && p !== ear.next &&
                pointInTriangle(a.x, a.y, ear.x, ear.y, c.x, c.y, p.x, p.y) && area(p.prev, p, p.next) >= 0
            ) return false
            p = p.prevZ
            if (n !== ear.prev && n !== ear.next &&
                pointInTriangle(a.x, a.y, ear.x, ear.y, c.x, c.y, n.x, n.y) && area(n.prev, n, n.next) >= 0
            ) return false
            n = n.nextZ
        }

        // look for remaining points in decreasing z-order
        while (p != null && p.z >= minZ) {
            if (p !== ear.prev && p !== ear.next &&
                pointInTriangle(a.x, a.y, ear.x, ear.y, c.x, c.y, p.x, p.y) && area(p.prev, p, p.next) >= 0
            ) return false
            p = p.prevZ
        }

        // look for remaining points in increasing z-order
        while (n != null && n.z <= maxZ) {
            if (n !== ear.prev && n !== ear.next &&
                pointInTriangle(a.x, a.y, ear.x, ear.y, c.x, c.y, n.x, n.y) && area(n.prev, n, n.next) >= 0
            ) return false
            n = n.nextZ
        }
        return true
    }

    /**
     * Go through all polygon nodes and cure small local self-intersections
     */
    private fun cureLocalIntersections(start: Node?, triangles: MutableList<Int>, dim: Int): Node? {
        var start = start
        var p = start
        do {
            val a = p!!.prev
            val b = p.next!!.next
            if (!equals(a, b) && intersects(a, p, p.next, b) && locallyInside(a, b) && locallyInside(b, a)) {
                triangles.add(a!!.i / dim)
                triangles.add(p.i / dim)
                triangles.add(b!!.i / dim)

                // remove two nodes involved
                removeNode(p)
                removeNode(p.next)
                start = b
                p = start
            }
            p = p.next
        } while (p !== start)
        return filterPoints(p, null)
    }

    /**
     * Try splitting polygon into two and triangulate them independently
     */
    private fun splitTriangulate(start: Node?, triangles: MutableList<Int>, dim: Int, minX: Float, minY: Float, invSize: Float) {
        // look for a valid diagonal that divides the polygon into two
        var a = start
        do {
            var b = a!!.next!!.next
            while (b !== a!!.prev) {
                if (a!!.i != b!!.i && isValidDiagonal(a, b)) {
                    // split the polygon in two by the diagonal
                    var c: Node? = splitPolygon(a, b)

                    // filter colinear points around the cuts
                    a = filterPoints(a, a.next)
                    c = filterPoints(c, c!!.next)

                    // run earcut on each half
                    triangulateLinked(a, triangles, dim, minX, minY, invSize, 0)
                    triangulateLinked(c, triangles, dim, minX, minY, invSize, 0)
                    return
                }
                b = b.next
            }
            a = a!!.next
        } while (a !== start)
    }

    /**
     * Link every hole into the outer loop, producing a single-ring polygon without holes
     */
    private fun eliminateHoles(coordinates: FloatArray, holeIndices: IntArray = intArrayOf(), outerNode: Node, dim: Int): Node {
        var outerNode: Node? = outerNode
        val queue: MutableList<Node?> = ArrayList()
        var i: Int
        val len: Int
        var start: Int
        var end: Int
        var list: Node?
        i = 0
        len = holeIndices.size
        while (i < len) {
            start = holeIndices[i] * dim
            end = if (i < len - 1) holeIndices[i + 1] * dim else coordinates.size
            list = linkedList(coordinates, start, end, dim, false)
            if (list === list!!.next) list!!.steiner = true
            queue.add(getLeftmost(list))
            i++
        }
        sort(queue, compareX())

        // process holes from left to right
        i = 0
        while (i < queue.size) {
            eliminateHole(queue[i], outerNode)
            outerNode = filterPoints(outerNode, outerNode!!.next)
            i++
        }
        return outerNode!!
    }

    private fun compareX(): Comparator<Node?> {
        return Comparator { a, b -> java.lang.Float.compare(a!!.x, b!!.x) }
    }

    /**
     * Find a bridge between vertices that connects hole with an outer ring and and link it
     */
    private fun eliminateHole(hole: Node?, outerNode: Node?) {
        var outerNode = outerNode
        outerNode = findHoleBridge(hole, outerNode)
        if (outerNode != null) {
            val b = splitPolygon(outerNode, hole)

            // filter collinear points around the cuts
            filterPoints(outerNode, outerNode.next)
            filterPoints(b, b.next)
        }
    }

    /**
     * David Eberly's algorithm for finding a bridge between hole and outer polygon
     */
    private fun findHoleBridge(hole: Node?, outerNode: Node?): Node? {
        var p = outerNode
        val hx = hole!!.x
        val hy = hole.y
        var qx: Float = -Float.MAX_VALUE
        var m: Node? = null

        // find a segment intersected by a ray from the hole's leftmost point to the left;
        // segment's endpoint with lesser x will be potential connection point
        do {
            if (hy <= p!!.y && hy >= p.next!!.y && p.next!!.y != p.y) {
                val x = p.x + (hy - p.y) * (p.next!!.x - p.x) / (p.next!!.y - p.y)
                if (x <= hx && x > qx) {
                    qx = x
                    if (x == hx) {
                        if (hy == p.y) return p
                        if (hy == p.next!!.y) return p.next
                    }
                    m = if (p.x < p.next!!.x) p else p.next
                }
            }
            p = p.next
        } while (p !== outerNode)
        if (m == null) return null
        if (hx == qx) return m // hole touches outer segment; pick leftmost endpoint

        // look for points inside the triangle of hole point, segment intersection and endpoint;
        // if there are no points found, we have a valid connection;
        // otherwise choose the point of the minimum angle with the ray as connection point
        val stop: Node = m
        val mx = m.x
        val my = m.y
        var tanMin: Float = Float.MAX_VALUE
        var tan: Float
        p = m
        do {
            if (hx >= p!!.x && p.x >= mx && hx != p.x &&
                pointInTriangle(if (hy < my) hx else qx, hy, mx, my, if (hy < my) qx else hx, hy, p.x, p.y)
            ) {
                tan = Math.abs(hy - p.y) / (hx - p.x) // tangential
                if (locallyInside(p, hole) &&
                    (tan < tanMin || tan == tanMin && (p.x > m!!.x || p.x == m.x && sectorContainsSector(m, p)))
                ) {
                    m = p
                    tanMin = tan
                }
            }
            p = p.next
        } while (p !== stop)
        return m
    }

    /**
     * Whether sector in vertex m contains sector in vertex p in the same coordinates
     */
    private fun sectorContainsSector(m: Node?, p: Node?): Boolean {
        return area(m!!.prev, m, p!!.prev) < 0 && area(p.next, m, m.next) < 0
    }

    /**
     * Interlink polygon nodes in z-order
     */
    private fun indexCurve(start: Node, minX: Float, minY: Float, invSize: Float) {
        var p: Node? = start
        do {
            if (p!!.z == -1f) p.z = zOrder(p.x, p.y, minX, minY, invSize)
            p.prevZ = p.prev
            p.nextZ = p.next
            p = p.next
        } while (p !== start)
        p.prevZ!!.nextZ = null
        p.prevZ = null
        sortLinked(p)
    }

    /**
     * Simon Tatham's linked list merge sort algorithm
     * http://www.chiark.greenend.org.uk/~sgtatham/algorithms/listsort.html
     */
    private fun sortLinked(list: Node?): Node? {
        var list = list
        var i: Int
        var p: Node?
        var q: Node?
        var e: Node?
        var tail: Node?
        var numMerges: Int
        var pSize: Int
        var qSize: Int
        var inSize = 1
        do {
            p = list
            list = null
            tail = null
            numMerges = 0
            while (p != null) {
                numMerges++
                q = p
                pSize = 0
                i = 0
                while (i < inSize) {
                    pSize++
                    q = q!!.nextZ
                    if (q == null) break
                    i++
                }
                qSize = inSize
                while (pSize > 0 || qSize > 0 && q != null) {
                    if (pSize != 0 && (qSize == 0 || q == null || p!!.z <= q.z)) {
                        e = p
                        p = p!!.nextZ
                        pSize--
                    } else {
                        e = q
                        q = q!!.nextZ
                        qSize--
                    }
                    if (tail != null) tail.nextZ = e else list = e
                    e!!.prevZ = tail
                    tail = e
                }
                p = q
            }
            tail!!.nextZ = null
            inSize *= 2
        } while (numMerges > 1)
        return list
    }

    /**
     *  Z-order of a point given coords and inverse of the longer side of data bbox
     */
    fun zOrder(x0: Float, y0: Float, minX: Float, minY: Float, invSize: Float): Float {
        // coords are transformed into non-negative 15-bit integer range
        var x = (32767 * (x0 - minX) * invSize).toInt()
        var y = (32767 * (y0 - minY) * invSize).toInt()
        x = x or (x shl 8) and 0x00FF00FF
        x = x or (x shl 4) and 0x0F0F0F0F
        x = x or (x shl 2) and 0x33333333
        x = x or (x shl 1) and 0x55555555
        y = y or (y shl 8) and 0x00FF00FF
        y = y or (y shl 4) and 0x0F0F0F0F
        y = y or (y shl 2) and 0x33333333
        y = y or (y shl 1) and 0x55555555
        return (x or (y shl 1)).toFloat()
    }

    /**
     * Find the leftmost node of a polygon ring
     */
    private fun getLeftmost(start: Node?): Node? {
        var p = start
        var leftmost = start
        do {
            if (p!!.x < leftmost!!.x || p.x == leftmost.x && p.y < leftmost.y) leftmost = p
            p = p.next
        } while (p !== start)
        return leftmost
    }

    /**
     * Check if a point lies within a convex triangle
     */
    private fun pointInTriangle(ax: Float, ay: Float, bx: Float, by: Float, cx: Float, cy: Float, px: Float, py: Float): Boolean {
        return (cx - px) * (ay - py) - (ax - px) * (cy - py) >= 0 && (ax - px) * (by - py) - (bx - px) * (ay - py) >= 0 && (bx - px) * (cy - py) - (cx - px) * (by - py) >= 0
    }

    /**
     * Check if a diagonal between two polygon nodes is valid (lies in polygon interior)
     */
    private fun isValidDiagonal(a: Node?, b: Node?): Boolean {
        return a!!.next!!.i != b!!.i && a.prev!!.i != b.i && !intersectsPolygon(a, b) &&  // dones't intersect other edges
                (locallyInside(a, b) && locallyInside(b, a) && middleInside(a, b) &&  // locally visible
                        (area(a.prev, a, b.prev) != 0f || area(a, b.prev, b) != 0f) ||  // does not create opposite-facing sectors
                        equals(a, b) && area(a.prev, a, a.next) > 0 && area(b.prev, b, b.next) > 0) // special zero-length case
    }

    /**
     * Signed area of a triangle
     */
    private fun area(p: Node?, q: Node?, r: Node?): Float {
        return (q!!.y - p!!.y) * (r!!.x - q.x) - (q.x - p.x) * (r.y - q.y)
    }

    /**
     * Check if two points are equal
     */
    private fun equals(p1: Node?, p2: Node?): Boolean {
        return p1!!.x == p2!!.x && p1.y == p2.y
    }

    /**
     * Check if two segments intersect
     */
    private fun intersects(p1: Node?, q1: Node?, p2: Node?, q2: Node?): Boolean {
        val o1 = sign(area(p1, q1, p2))
        val o2 = sign(area(p1, q1, q2))
        val o3 = sign(area(p2, q2, p1))
        val o4 = sign(area(p2, q2, q1))
        if (o1 != o2 && o3 != o4) return true // general case
        if (o1 == 0 && onSegment(p1, p2, q1)) return true // p1, q1 and p2 are collinear and p2 lies on p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true // p1, q1 and q2 are collinear and q2 lies on p1q1
        if (o3 == 0 && onSegment(p2, p1, q2)) return true // p2, q2 and p1 are collinear and p1 lies on p2q2
        return if (o4 == 0 && onSegment(p2, q1, q2)) true else false // p2, q2 and q1 are collinear and q1 lies on p2q2
    }

    /**
     * For collinear points p, q, r, check if point q lies on segment pr
     */
    private fun onSegment(p: Node?, q: Node?, r: Node?): Boolean {
        return q!!.x <= Math.max(p!!.x, r!!.x) && q.x >= Math.min(p.x, r.x) && q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y)
    }

    private fun sign(num: Float): Int {
        return if (num > 0) 1 else if (num < 0) -1 else 0
    }

    /**
     * Check if a polygon diagonal intersects any polygon segments
     */
    private fun intersectsPolygon(a: Node?, b: Node?): Boolean {
        var p = a
        do {
            if (p!!.i != a!!.i && p.next!!.i != a.i && p.i != b!!.i && p.next!!.i != b.i &&
                intersects(p, p.next, a, b)
            ) return true
            p = p.next
        } while (p !== a)
        return false
    }

    /**
     * Check if a polygon diagonal is locally inside the polygon
     */
    private fun locallyInside(a: Node?, b: Node?): Boolean {
        return if (area(a!!.prev, a, a.next) < 0) area(a, b, a.next) >= 0 && area(a, a.prev, b) >= 0 else area(a, b, a.prev) < 0 || area(a, a.next, b) < 0
    }

    /**
     * Check if the middle point of a polygon diagonal is inside the polygon
     */
    private fun middleInside(a: Node?, b: Node?): Boolean {
        var p = a
        var inside = false
        val px = (a!!.x + b!!.x) / 2
        val py = (a.y + b.y) / 2
        do {
            if (p!!.y > py != p.next!!.y > py && p.next!!.y != p.y &&
                px < (p.next!!.x - p.x) * (py - p.y) / (p.next!!.y - p.y) + p.x
            ) inside = !inside
            p = p.next
        } while (p !== a)
        return inside
    }

    /**
     * Link two polygon vertices with a bridge; if the vertices belong to the same ring, it splits polygon into two.
     * If one belongs to the outer ring and another to a hole, it merges it into a single ring
     */
    private fun splitPolygon(a: Node?, b: Node?): Node {
        val a2 = Node(a!!.i, a.x, a.y)
        val b2 = Node(b!!.i, b.x, b.y)
        val an = a.next
        val bp = b.prev
        a.next = b
        b.prev = a
        a2.next = an
        an!!.prev = a2
        b2.next = a2
        a2.prev = b2
        bp!!.next = b2
        b2.prev = bp
        return b2
    }

    /**
     * Create a node and optionally link it with previous one (in a circular doubly linked list)
     */
    private fun insertNode(i: Int, x: Float, y: Float, last: Node?): Node {
        val p = Node(i, x, y)
        if (last == null) {
            p.prev = p
            p.next = p
        } else {
            p.next = last.next
            p.prev = last
            last.next!!.prev = p
            last.next = p
        }
        return p
    }

    private fun removeNode(p: Node?) {
        p!!.next!!.prev = p.prev
        p.prev!!.next = p.next
        if (p.prevZ != null) p.prevZ!!.nextZ = p.nextZ
        if (p.nextZ != null) p.nextZ!!.prevZ = p.prevZ
    }

    private fun signedArea(data: FloatArray, start: Int, end: Int, dim: Int): Float {
        var sum = 0f
        var i = start
        var j = end - dim
        while (i < end) {
            sum += (data[j] - data[i]) * (data[i + 1] + data[j + 1])
            j = i
            i += dim
        }
        return sum
    }

    /**
     * Turn a polygon in a multi-dimensional array form (e.g. as in GeoJSON) into a form Triangulation accepts
     */
    fun flatten(data: Array<Array<FloatArray>>): TriangulationObject {
        val dimension: Int = data[0][0].size
        val result = TriangulationObject(arrayListOf(), arrayListOf(), dimension)
        var holeIndex = 0
        for (i in data.indices) {
            for (element in data[i]) {
                for (d in 0 until dimension) result.coordinates.add(element[d])
            }
            if (i > 0) {
                holeIndex += data[i - 1].size
                result.holeIndices.add(holeIndex)
            }
        }
        return result
    }

    /**
     * Simple node class
     * @param i vertex index in coordinates array
     * @param x vertex x coordinate
     * @param y vertex y coordinate
     */
    internal class Node(var i: Int, var x: Float, var y: Float) {

        var z: Float          // z-order curve value
        var steiner: Boolean  // indicates whether this is a steiner point
        var prev: Node?       // previous vertex nodes in a polygon ring
        var next: Node?       // next vertex nodes in a polygon ring
        var nextZ: Node?      // next nodes in z-order
        var prevZ: Node?      // previous nodes in z-order

        init {
            z = -1f
            prev = null
            next = null
            prevZ = null
            nextZ = null
            steiner = false
        }
    }

    /**
     * Simple class for holding the input values about the triangulation
     */
    class TriangulationObject(var coordinates: ArrayList<Float>, var holeIndices: ArrayList<Int>, var dimensions: Int)

}