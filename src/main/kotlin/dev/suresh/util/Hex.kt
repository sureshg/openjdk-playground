package dev.suresh.util

/**
 * This is the direct port from one-nio library [https://github.com/odnoklassniki/one-nio/tree/master/src/one/nio/util]
 */

object Hex {

    private val SMALL = "0123456789abcdef".toCharArray()
    private val CAPITAL = "0123456789ABCDEF".toCharArray()
    private val DIGIT_VALUE = IntArray(256)

    init {
        DIGIT_VALUE.fill(-1)
        (0..9).forEach {
            DIGIT_VALUE['0'.code + it] = it
        }
        (10..15).forEach {
            DIGIT_VALUE['A'.code + it - 10] = it
            DIGIT_VALUE['a'.code + it - 10] = it
        }
    }

    fun toHex(input: ByteArray, digits: CharArray = SMALL): String {
        val result = CharArray(input.size * 2)
        input.forEachIndexed { i, b ->
            result[i * 2] = digits[b.toInt() and 0xff ushr 4]
            result[i * 2 + 1] = digits[b.toInt() and 0x0f]
        }
        return result.concatToString()
    }

    fun toHex(n: Int, digits: CharArray = SMALL): String {
        val result = CharArray(8)
        var j = n
        for (i in 7 downTo 0) {
            j = j ushr 4
            result[i] = digits[j and 0x0f]
        }
        return result.concatToString()
    }

    fun toHex(n: Long, digits: CharArray = SMALL): String {
        val result = CharArray(16)
        var l = n
        for (i in 15 downTo 0) {
            l = l ushr 4
            result[i] = digits[l.toInt() and 0x0f]
        }
        return result.concatToString()
    }

    fun parseBytes(input: String): ByteArray {
        val length = input.length
        val result = ByteArray(length / 2)
        for (i in 0 until length step 2) {
            result[i ushr 1] = (
                DIGIT_VALUE[input[i].code] shl 4 or
                    DIGIT_VALUE[input[i + 1].code]
                ).toByte()
        }
        return result
    }

    fun parseInt(input: String): Int {
        val length = input.length
        var result = 0
        for (i in 0 until length) {
            val digit = DIGIT_VALUE[input[i].code]
            require(digit >= 0)
            result = result shl 4 or digit
        }
        return result
    }

    fun parseLong(input: String): Long {
        val length = input.length
        var result: Long = 0
        for (i in 0 until length) {
            val digit = DIGIT_VALUE[input[i].code]
            require(digit >= 0)
            result = result shl 4 or digit.toLong()
        }
        return result
    }
}
