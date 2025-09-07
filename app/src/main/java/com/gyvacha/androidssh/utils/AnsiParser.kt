@file:Suppress("MagicNumber")

package com.gyvacha.androidssh.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

fun parseAnsiToAnnotatedString(input: String): AnnotatedString {
    val builder = AnnotatedString.Builder()
    var currentStyle = SpanStyle()
    val regex = Regex("\u001B\\[([0-9;]+)m")
    val ansiControlSeqRegex = Regex("\u001B\\[[?;0-9]*[hl]")
    var lastIndex = 0
    val cleaned = input.replace(ansiControlSeqRegex, "")

    regex.findAll(cleaned).forEach { matchResult ->
        val text = cleaned.substring(lastIndex, matchResult.range.first)
        if (text.isNotEmpty()) {
            builder.withStyle(currentStyle) {
                append(text)
            }
        }
        val codes = matchResult.groupValues[1]
            .split(";")
            .mapNotNull { it.toIntOrNull() }
        currentStyle = ansiCodesToSpanStyle(codes)
        lastIndex = matchResult.range.last + 1
    }
    if (lastIndex < cleaned.length) {
        builder.withStyle(currentStyle) {
            append(cleaned.substring(lastIndex))
        }
    }
    return builder.toAnnotatedString()
}

fun ansiCodesToSpanStyle(codes: List<Int>): SpanStyle {
    var color: Color? = null
    var background: Color? = null
    var weight: FontWeight? = null
    var style: FontStyle? = null
    var decoration: TextDecoration? = null
    var reset = false

    var i = 0
    while (i < codes.size) {
        when (val code = codes[i]) {
            0 -> reset = true
            1 -> weight = FontWeight.Bold
            2 -> weight = FontWeight.Thin
            3 -> style = FontStyle.Italic
            4 -> decoration = TextDecoration.Underline
            9 -> decoration = TextDecoration.LineThrough
            in 30..37 -> color = basicAnsiColor(code - 30, bright = false)
            in 90..97 -> color = basicAnsiColor(code - 90, bright = true)
            in 40..47 -> background = basicAnsiColor(code - 40, bright = false)
            in 100..107 -> background = basicAnsiColor(code - 100, bright = true)
            38, 48 -> {
                val isForeground = (code == 38)
                if (i + 1 < codes.size) {
                    when (codes[i + 1]) {
                        5 -> if (i + 2 < codes.size) { // 256-color
                            val ansi256 = ansi256Color(codes[i + 2])
                            if (isForeground) color = ansi256 else background = ansi256
                            i += 2
                        }
                        2 -> if (i + 3 < codes.size) { // true color
                            val r = codes[i + 2].coerceIn(0, 255)
                            val g = codes[i + 3].coerceIn(0, 255)
                            val b = codes[i + 4].coerceIn(0, 255)
                            val rgb = Color(r, g, b)
                            if (isForeground) color = rgb else background = rgb
                            i += 4
                        }
                    }
                }
            }
        }
        i++
    }

    return if (reset) {
        SpanStyle()
    } else {
        SpanStyle(
            color = color ?: Color.Unspecified,
            background = background ?: Color.Unspecified,
            fontWeight = weight,
            fontStyle = style,
            textDecoration = decoration
        )
    }
}

private fun basicAnsiColor(code: Int, bright: Boolean): Color = when (code) {
    0 -> if (bright) Color.Gray else Color.Black
    1 -> if (bright) Color.Red.copy(alpha = 0.85f) else Color.Red
    2 -> if (bright) Color.Green.copy(alpha = 0.85f) else Color.Green
    3 -> if (bright) Color.Yellow.copy(alpha = 0.85f) else Color.Yellow
    4 -> if (bright) Color.Blue.copy(alpha = 0.85f) else Color.Blue
    5 -> if (bright) Color.Magenta.copy(alpha = 0.85f) else Color.Magenta
    6 -> if (bright) Color.Cyan.copy(alpha = 0.85f) else Color.Cyan
    7 -> Color.White
    else -> Color.Unspecified
}

private fun ansi256Color(code: Int): Color {
    return when {
        code < 16 -> basicAnsiColor(code % 8, bright = code >= 8)
        code in 16..231 -> {
            val c = code - 16
            val r = (c / 36) % 6
            val g = (c / 6) % 6
            val b = c % 6
            Color(r * 51, g * 51, b * 51)
        }
        code in 232..255 -> {
            val v = (code - 232) * 10 + 8
            Color(v, v, v)
        }
        else -> Color.Unspecified
    }
}
