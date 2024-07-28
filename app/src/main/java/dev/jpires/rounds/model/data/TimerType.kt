package dev.jpires.rounds.model.data

enum class TimerType {
    PREP, ROUND, REST, FINISHED;

    override fun toString(): String {
        return when (this) {
            PREP -> "Prep"
            ROUND -> "Fight"
            REST -> "Rest"
            FINISHED -> "Finished"
        }
    }

}