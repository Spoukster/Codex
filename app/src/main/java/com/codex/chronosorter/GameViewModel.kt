package com.codex.chronosorter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

enum class Era(val title: String, val emoji: String) {
    PAST("Прошлое", "🏺"),
    PRESENT("Настоящее", "📱"),
    FUTURE("Будущее", "🛸")
}

data class Artifact(
    val name: String,
    val era: Era,
    val clue: String,
    val visualEmoji: String,
    val imagePrompt: String
)

data class GameUiState(
    val score: Int = 0,
    val combo: Int = 0,
    val lives: Int = 3,
    val currentArtifact: Artifact? = null,
    val feedMessage: String = "Стабилизируй временную линию!",
    val isFinished: Boolean = false
)

class GameViewModel : ViewModel() {
    private val artifacts = listOf(
        Artifact(
            "Кремниевый топор",
            Era.PAST,
            "Он старше пирамид.",
            "🪓",
            "flint axe artifact, centered studio shot, neo-retro flat illustration, thick clean outline, soft gradient, muted palette, parchment texture background"
        ),
        Artifact(
            "Кассетный плеер",
            Era.PAST,
            "Когда-то это был топовый звук.",
            "📼",
            "cassette player artifact, centered, neo-retro flat illustration, thick outline, warm muted palette, tiny grain, clean poster composition"
        ),
        Artifact(
            "Электросамокат",
            Era.PRESENT,
            "Обычно мешает на тротуаре.",
            "🛴",
            "electric scooter artifact, centered, neo-retro flat style, rounded geometry, clean outlines, balanced shadows, muted city colors"
        ),
        Artifact(
            "Смарт-часы",
            Era.PRESENT,
            "Считает шаги лучше тебя.",
            "⌚",
            "smart watch artifact, front view, unified neo-retro flat design, thick linework, soft blue gradient, subtle grain"
        ),
        Artifact(
            "Голографическая газета",
            Era.FUTURE,
            "Её нельзя порвать.",
            "📰",
            "holographic newspaper artifact, floating, cyan-magenta glow, neo-retro flat sci-fi illustration, clear outlines, minimal background"
        ),
        Artifact(
            "Нейрошлем сна",
            Era.FUTURE,
            "Загружает сны по подписке.",
            "🪖",
            "neural dream helmet artifact, centered, neon accents, neo-retro flat illustration, thick outlines, soft reflections"
        ),
        Artifact(
            "Механический компас",
            Era.PAST,
            "Навигация без GPS.",
            "🧭",
            "mechanical compass artifact, top-down, neo-retro flat poster style, bronze muted palette, crisp outlines"
        ),
        Artifact(
            "QR-кубик Рубика",
            Era.PRESENT,
            "Сканируешь — и он сам решается.",
            "🧊",
            "rubik cube with QR patterns, centered, neo-retro flat illustration, clean black outlines, modern muted colors"
        ),
        Artifact(
            "Антигравитационный скейт",
            Era.FUTURE,
            "Колёса тут лишние.",
            "🛹",
            "anti-gravity skateboard artifact, levitating, neon rim light, unified neo-retro flat sci-fi style, thick outlines"
        ),
        Artifact(
            "Пишущая машинка",
            Era.PAST,
            "Кнопки тяжелее твоего ноутбука.",
            "⌨️",
            "vintage typewriter artifact, centered, neo-retro flat illustration, warm muted palette, clear linework, paper texture"
        ),
        Artifact(
            "Дрон-курьер",
            Era.PRESENT,
            "Привозит роллы быстрее дождя.",
            "🚁",
            "delivery drone artifact, front 3/4 view, neo-retro flat style, chunky outline, soft gradients, modern palette"
        ),
        Artifact(
            "Квантовый термос",
            Era.FUTURE,
            "Чай всегда идеальной температуры.",
            "🥤",
            "quantum thermos artifact, centered, futuristic but cute, neo-retro flat illustration, cyan-violet accents, clean thick lines"
        )
    )

    private var deck = artifacts.shuffled().toMutableList()

    private val _uiState = MutableStateFlow(GameUiState(currentArtifact = drawArtifact()))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun sortTo(era: Era) {
        val state = _uiState.value
        val artifact = state.currentArtifact ?: return

        val isCorrect = artifact.era == era
        val newCombo = if (isCorrect) state.combo + 1 else 0
        val comboBonus = if (newCombo >= 3) newCombo else 0
        val scoreDelta = if (isCorrect) 10 + comboBonus else 0
        val remainingLives = if (isCorrect) state.lives else state.lives - 1

        val message = if (isCorrect) {
            "✔ ${artifact.name} на месте. Комбо: $newCombo"
        } else {
            "✘ Пространственный сбой! ${artifact.name} не из эры ${era.title}."
        }

        val nextArtifact = if (remainingLives > 0) drawArtifact() else null

        _uiState.value = state.copy(
            score = state.score + scoreDelta,
            combo = newCombo,
            lives = remainingLives,
            currentArtifact = nextArtifact,
            feedMessage = message,
            isFinished = remainingLives <= 0
        )
    }

    fun restart() {
        deck = artifacts.shuffled(Random(System.currentTimeMillis())).toMutableList()
        _uiState.value = GameUiState(currentArtifact = drawArtifact())
    }

    private fun drawArtifact(): Artifact {
        if (deck.isEmpty()) {
            deck = artifacts.shuffled().toMutableList()
        }
        return deck.removeFirst()
    }
}
