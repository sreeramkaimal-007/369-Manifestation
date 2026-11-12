package com.example.model

enum class SessionType(
    val title: String,
    val count: Int,
    val description: String,
    val timeLabel: String,
    val iconName: String,
    val solfeggioHz: Float
) {
    MORNING(
        title = "Morning Ritual",
        count = 3,
        description = "Say aloud 3 times to ignite your daily intention into the universe.",
        timeLabel = "Morning (3x)",
        iconName = "wb_sunny",
        solfeggioHz = 528f // Transformation & Miracles
    ),
    AFTERNOON(
        title = "Afternoon Focus",
        count = 6,
        description = "Say aloud 6 times to amplify and ground your intention mid-day.",
        timeLabel = "Afternoon (6x)",
        iconName = "brightness_5",
        solfeggioHz = 639f // Harmonious Connection
    ),
    EVENING(
        title = "Evening Grounding",
        count = 9,
        description = "Say aloud 9 times to imprint your manifestation into your subconscious before sleep.",
        timeLabel = "Evening (9x)",
        iconName = "bedtime",
        solfeggioHz = 432f // Cosmic Calm & Sleep
    )
}
