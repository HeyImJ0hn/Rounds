package dev.jpires.rounds.model.data

import kotlin.time.Duration

data class Preset(
    val id: Int = 0,
    var name: String,
    var rounds: Int,
    var roundLength: Duration,
    var restTime: Duration,
    var prepTime: Duration
) {
    fun toEntityModel(): PresetEntity {
        return PresetEntity(
            id = this.id,
            name = this.name,
            rounds = this.rounds,
            roundLength = this.roundLength.inWholeSeconds,
            restTime = this.restTime.inWholeSeconds,
            prepTime = this.prepTime.inWholeSeconds
        )
    }

}