package com.example.data

data class ApiConfig(
    val primary: String = "https://www.tikwm.com/api/",
    val backup: String = "",
    val backup2: String = "",
    val latest_version: String = "1.0.0",
    val maintenance: Boolean = false
)
