package dev.jpires.rounds.model.data

import kotlin.time.Duration

data class Preset(
    val id: Int,
    val name: String,
    val rounds: Int,
    val roundLength: Duration,
    val restTime: Duration,
    val prepTime: Duration
)
