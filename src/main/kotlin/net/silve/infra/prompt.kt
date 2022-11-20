package net.silve.infra

import java.lang.Exception

class Prompt {
    fun prompt(message: String, default: String): String {
        print("$message [$default] ? ")
        val line = readLine()
        return if (line.isNullOrBlank()) default else line
    }

    fun <T> promptList(message:String, default: Int = 0, labels: Array<String>, values: Array<T>): T {
        fun isValid(value: String?): Int {
            if (value.isNullOrBlank()) return default
            val index = value.toInt()
            if (index < 0 || index >= values.size) throw RuntimeException()
            return index
        }

        fun iterate(): Int {
            while (true) {
                print("Choose [$default]: ")
                val line = readln()
                try {
                    return isValid(line)
                } catch (_: Exception) {
                    print(ANSI_UP)
                    print(ANSI_CLEAR_LINE)
                }
            }
        }
        println("$message ? ")
        labels.forEachIndexed { index, s -> println("  [$index] $s") }
        val index = iterate()
        return values[index]
    }
}