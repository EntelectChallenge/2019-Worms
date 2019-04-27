package za.co.entelect.challenge.game.engine.simplexNoise

import kotlin.math.sqrt

/*
 * A speed-improved simplex noise algorithm for 2D, 3D and 4D in Java.
 *
 * Based on example code by Stefan Gustavson (stegu@itn.liu.se).
 * Optimisations by Peter Eastman (peastman@drizzle.stanford.edu).
 * Better rank ordering method for 4D by Stefan Gustavson in 2012.
 *
 * This could be speeded up even further, but it's useful as it is.
 *
 * Version 2012-03-09
 *
 * This code was placed in the public domain by its original author,
 * Stefan Gustavson. You may use it as you see fit, but
 * attribution is appreciated.
 *
 */

class SimplexNoise(private val seed: Int = 0) {  // Simplex noise in 2D, 3D and 4D

    private val grad3 = arrayOf(
            Grad(1.0, 1.0, 0.0),
            Grad(-1.0, 1.0, 0.0),
            Grad(1.0, -1.0, 0.0),
            Grad(-1.0, -1.0, 0.0),
            Grad(1.0, 0.0, 1.0),
            Grad(-1.0, 0.0, 1.0),
            Grad(1.0, 0.0, -1.0),
            Grad(-1.0, 0.0, -1.0),
            Grad(0.0, 1.0, 1.0),
            Grad(0.0, -1.0, 1.0),
            Grad(0.0, 1.0, -1.0),
            Grad(0.0, -1.0, -1.0))

    private val grad4 = arrayOf(Grad(0.0, 1.0, 1.0, 1.0),
            Grad(0.0, 1.0, 1.0, -1.0),
            Grad(0.0, 1.0, -1.0, 1.0),
            Grad(0.0, 1.0, -1.0, -1.0),
            Grad(0.0, -1.0, 1.0, 1.0),
            Grad(0.0, -1.0, 1.0, -1.0),
            Grad(0.0, -1.0, -1.0, 1.0),
            Grad(0.0, -1.0, -1.0, -1.0),
            Grad(1.0, 0.0, 1.0, 1.0),
            Grad(1.0, 0.0, 1.0, -1.0),
            Grad(1.0, 0.0, -1.0, 1.0),
            Grad(1.0, 0.0, -1.0, -1.0),
            Grad(-1.0, 0.0, 1.0, 1.0),
            Grad(-1.0, 0.0, 1.0, -1.0),
            Grad(-1.0, 0.0, -1.0, 1.0),
            Grad(-1.0, 0.0, -1.0, -1.0),
            Grad(1.0, 1.0, 0.0, 1.0),
            Grad(1.0, 1.0, 0.0, -1.0),
            Grad(1.0, -1.0, 0.0, 1.0),
            Grad(1.0, -1.0, 0.0, -1.0),
            Grad(-1.0, 1.0, 0.0, 1.0),
            Grad(-1.0, 1.0, 0.0, -1.0),
            Grad(-1.0, -1.0, 0.0, 1.0),
            Grad(-1.0, -1.0, 0.0, -1.0),
            Grad(1.0, 1.0, 1.0, 0.0),
            Grad(1.0, 1.0, -1.0, 0.0),
            Grad(1.0, -1.0, 1.0, 0.0),
            Grad(1.0, -1.0, -1.0, 0.0),
            Grad(-1.0, 1.0, 1.0, 0.0),
            Grad(-1.0, 1.0, -1.0, 0.0),
            Grad(-1.0, -1.0, 1.0, 0.0),
            Grad(-1.0, -1.0, -1.0, 0.0))

    private val p = shortArrayOf(151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103,
            30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203,
            117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134,
            139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245,
            40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135,
            130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147,
            118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119,
            248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79,
            113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162,
            241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121,
            50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61,
            156, 180)
    // To remove the need for index wrapping, double the permutation table length
    private val perm = ShortArray(512)
    private val permMod12 = ShortArray(512)

    // Skewing and unskewing factors for 2, 3, and 4 dimensions
    private val F2 = 0.5 * (sqrt(3.0) - 1.0)
    private val G2 = (3.0 - sqrt(3.0)) / 6.0
    private val F3 = 1.0 / 3.0
    private val G3 = 1.0 / 6.0
    private val F4 = (sqrt(5.0) - 1.0) / 4.0
    private val G4 = (5.0 - sqrt(5.0)) / 20.0

    init {
        for (i in 0..511) {
            perm[i] = p[i and 255]
            permMod12[i] = (perm[i] % 12).toShort()
        }
    }

    // This method is a *lot* faster than using (int)floor(x)
    private fun fastfloor(x: Double): Int {
        val xi = x.toInt()
        return if (x < xi) xi - 1 else xi
    }

    private fun dot(g: Grad, x: Double, y: Double): Double {
        return g.x * x + g.y * y
    }

    private fun dot(g: Grad, x: Double, y: Double, z: Double): Double {
        return g.x * x + g.y * y + g.z * z
    }

    private fun dot(g: Grad, x: Double, y: Double, z: Double, w: Double): Double {
        return g.x * x + g.y * y + g.z * z + g.w * w
    }

    fun n1d(xin: Int): Double = n1d(xin.toDouble())

    fun n1d(xin: Double): Double = n2d(xin, 0.0)

    fun n2d(xin: Int, yin: Int): Double = n2d(xin.toDouble(), yin.toDouble())

    // 2D simplex noise
    fun n2d(xin: Double, yin: Double): Double {
        val n0: Double
        val n1: Double
        val n2: Double
        val n3: Double // Noise contributions from the four corners
        // Skew the input space to determine which simplex cell we're in
        val s = (xin + yin + seed) * F3 // Very nice and simple skew factor for 3D
        val i = fastfloor(xin + s)
        val j = fastfloor(yin + s)
        val k = fastfloor(seed + s)
        val t = (i + j + k) * G3
        val X0 = i - t // Unskew the cell origin back to (x,y,z) space
        val Y0 = j - t
        val Z0 = k - t
        val x0 = xin - X0 // The x,y,z distances from the cell origin
        val y0 = yin - Y0
        val z0 = seed - Z0
        // For the 3D case, the simplex shape is a slightly irregular tetrahedron.
        // Determine which simplex we are in.
        val i1: Int
        val j1: Int
        val k1: Int // Offsets for second corner of simplex in (i,j,k) coords
        val i2: Int
        val j2: Int
        val k2: Int // Offsets for third corner of simplex in (i,j,k) coords
        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1
                j1 = 0
                k1 = 0
                i2 = 1
                j2 = 1
                k2 = 0
            } // X Y Z order
            else if (x0 >= z0) {
                i1 = 1
                j1 = 0
                k1 = 0
                i2 = 1
                j2 = 0
                k2 = 1
            } // X Z Y order
            else {
                i1 = 0
                j1 = 0
                k1 = 1
                i2 = 1
                j2 = 0
                k2 = 1
            } // Z X Y order
        } else { // x0<y0
            if (y0 < z0) {
                i1 = 0
                j1 = 0
                k1 = 1
                i2 = 0
                j2 = 1
                k2 = 1
            } // Z Y X order
            else if (x0 < z0) {
                i1 = 0
                j1 = 1
                k1 = 0
                i2 = 0
                j2 = 1
                k2 = 1
            } // Y Z X order
            else {
                i1 = 0
                j1 = 1
                k1 = 0
                i2 = 1
                j2 = 1
                k2 = 0
            } // Y X Z order
        }
        // A step of (1,0,0) in (i,j,k) means a step of (1-c,-c,-c) in (x,y,z),
        // a step of (0,1,0) in (i,j,k) means a step of (-c,1-c,-c) in (x,y,z), and
        // a step of (0,0,1) in (i,j,k) means a step of (-c,-c,1-c) in (x,y,z), where
        // c = 1/6.
        val x1 = x0 - i1 + G3 // Offsets for second corner in (x,y,z) coords
        val y1 = y0 - j1 + G3
        val z1 = z0 - k1 + G3
        val x2 = x0 - i2 + 2.0 * G3 // Offsets for third corner in (x,y,z) coords
        val y2 = y0 - j2 + 2.0 * G3
        val z2 = z0 - k2 + 2.0 * G3
        val x3 = x0 - 1.0 + 3.0 * G3 // Offsets for last corner in (x,y,z) coords
        val y3 = y0 - 1.0 + 3.0 * G3
        val z3 = z0 - 1.0 + 3.0 * G3
        // Work out the hashed gradient indices of the four simplex corners
        val ii = i and 255
        val jj = j and 255
        val kk = k and 255
        val gi0 = permMod12[ii + perm[jj + perm[kk]]].toInt()
        val gi1 = permMod12[ii + i1 + perm[jj + j1 + perm[kk + k1].toInt()].toInt()].toInt()
        val gi2 = permMod12[ii + i2 + perm[jj + j2 + perm[kk + k2].toInt()].toInt()].toInt()
        val gi3 = permMod12[ii + 1 + perm[jj + 1 + perm[kk + 1].toInt()].toInt()].toInt()
        // Calculate the contribution from the four corners
        var t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0
        if (t0 < 0)
            n0 = 0.0
        else {
            t0 *= t0
            n0 = t0 * t0 * dot(grad3[gi0], x0, y0, z0)
        }
        var t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1
        if (t1 < 0)
            n1 = 0.0
        else {
            t1 *= t1
            n1 = t1 * t1 * dot(grad3[gi1], x1, y1, z1)
        }
        var t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2
        if (t2 < 0)
            n2 = 0.0
        else {
            t2 *= t2
            n2 = t2 * t2 * dot(grad3[gi2], x2, y2, z2)
        }
        var t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3
        if (t3 < 0)
            n3 = 0.0
        else {
            t3 *= t3
            n3 = t3 * t3 * dot(grad3[gi3], x3, y3, z3)
        }
        // Add contributions from each corner to get the final noise value.
        // The result is scaled to stay just inside [-1,1]
        return toSingleUnitDecimal(32.0 * (n0 + n1 + n2 + n3))
    }

    /**
     * SimplexNoise normally hits +&- 0.8679777777778225
     * Transform this to a double in the range [0..1]
     */
    private fun toSingleUnitDecimal(incorrectValue: Double): Double {
        return (incorrectValue + 0.8679777777778225) * 0.576051614
    }

    // Inner class to speed upp gradient computations
    // (In Java, array access is a lot slower than member access)
    private class Grad {

        internal var x: Double = 0.0
        internal var y: Double = 0.0
        internal var z: Double = 0.0
        internal var w: Double = 0.0

        internal constructor(x: Double, y: Double, z: Double) {
            this.x = x
            this.y = y
            this.z = z
        }

        internal constructor(x: Double, y: Double, z: Double, w: Double) {
            this.x = x
            this.y = y
            this.z = z
            this.w = w
        }
    }
}
