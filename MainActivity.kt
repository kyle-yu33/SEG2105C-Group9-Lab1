package com.example.simplecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorApp()
        }
    }
}

@Composable
fun CalculatorApp() {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {

        // Show input and result on one line
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = input,
                fontSize = 28.sp,
                modifier = Modifier.weight(1f)
            )
            if (result.isNotEmpty()) {
                Text(
                    text = "= $result",
                    fontSize = 28.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button layout with "=" included in grid
        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("0", "C", "=", "+")
        )

        buttons.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { text ->
                    Button(
                        onClick = {
                            when (text) {
                                "C" -> {
                                    input = ""
                                    result = ""
                                }
                                "=" -> {
                                    try {
                                        result = eval(input).toString()
                                    } catch (e: Exception) {
                                        result = "Error"
                                    }
                                }
                                else -> input += text
                            }
                        },
                        modifier = Modifier
                            .padding(2.dp)
                    ) {
                        Text(text, fontSize = 22.sp)
                    }
                }
            }
        }
    }
}

fun eval(expr: String): Double {
    return object {
        var pos = -1
        var ch = 0
        fun nextChar() { ch = if (++pos < expr.length) expr[pos].code else -1 }
        fun eat(c: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == c) { nextChar(); return true }
            return false
        }
        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < expr.length) throw RuntimeException("Unexpected: ${expr[pos]}")
            return x
        }
        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.code) -> x += parseTerm()
                    eat('-'.code) -> x -= parseTerm()
                    else -> return x
                }
            }
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
        fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor()
            if (eat('-'.code)) return -parseFactor()
            val start = pos
            while (ch in '0'.code..'9'.code) nextChar()
            val num = expr.substring(start, pos)
            return num.toDouble()
        }
    }.parse()
}
