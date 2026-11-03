package com.murzik.voidlauncher.util

object MathEvaluator {

    fun evaluate(expression: String): String? {
        val cleaned = expression.trim().replace(" ", "").replace(",", ".")
        // Only evaluate if string contains math operators and digits
        if (!cleaned.any { it in "+-*/^%" } || !cleaned.any { it.isDigit() }) {
            return null
        }
        // Don't evaluate plain text words
        if (cleaned.any { it.isLetter() && it != 'e' && it != 'p' }) {
            return null
        }

        return try {
            val result = parseExpression(cleaned)
            if (result.isNaN() || result.isInfinite()) return null
            if (result % 1.0 == 0.0) {
                result.toLong().toString()
            } else {
                String.format("%.4f", result).trimEnd('0').trimEnd('.')
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun parseExpression(str: String): Double {
        var pos = -1
        var ch = 0

        fun nextChar() {
            ch = if (++pos < str.length) str[pos].code else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parseFactor(): Double {
            if (eat('+'.code)) return +parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = pos
            if (eat('('.code)) {
                x = parseExpression(str.substring(pos, str.indexOf(')', pos).takeIf { it != -1 } ?: str.length))
                eat(')'.code)
            } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                x = str.substring(startPos, pos).toDouble()
            } else {
                return Double.NaN
            }

            if (eat('^'.code)) x = Math.pow(x, parseFactor())
            if (eat('%'.code)) x /= 100.0

            return x
        }

        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> x /= parseFactor()
                    else -> return x
                }
            }
        }

        nextChar()
        var x = parseTerm()
        while (true) {
            when {
                eat('+'.code) -> x += parseTerm()
                eat('-'.code) -> x -= parseTerm()
                else -> return x
            }
        }
    }
}
