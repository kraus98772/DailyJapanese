package com.example.dailyjapanese

import java.io.Serializable

enum class Kanamoji(val value: String) : Serializable {
    hiragana("hiragana"),
    katakana("katakana")
}