package dev.jpires.rounds.model.data

enum class TimerType {
    PREP, ROUND, REST;

    fun next(): TimerType {
        return when (this) {
            PREP -> ROUND
            ROUND -> REST
            REST -> ROUND
        }
    }

    override fun toString(): String {
        return when (this) {
            PREP -> "Prep"
            ROUND -> "Fight"
            REST -> "Rest"
        }
    }

}