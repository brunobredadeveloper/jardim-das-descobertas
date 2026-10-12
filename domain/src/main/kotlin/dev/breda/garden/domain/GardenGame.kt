package dev.breda.garden.domain

import kotlin.random.Random

enum class Activity {
    ANIMALS,
    ANIMAL_SOUNDS,
    LETTERS,
    COLORS,
    SHAPES,
}

enum class Token(val letter: Char? = null) {
    CAT,
    BIRD,
    BUNNY,
    DOG,
    COW,
    BLUE,
    YELLOW,
    GREEN,
    CIRCLE,
    TRIANGLE,
    SQUARE,
    A('A'),
    B('B'),
    C('C'),
    D('D'),
    E('E'),
    F('F'),
    G('G'),
    H('H'),
    I('I'),
    J('J'),
    K('K'),
    L('L'),
    M('M'),
    N('N'),
    O('O'),
    P('P'),
    Q('Q'),
    R('R'),
    S('S'),
    T('T'),
    U('U'),
    V('V'),
    W('W'),
    X('X'),
    Y('Y'),
    Z('Z'),
}

enum class Feedback {
    EXPLORING,
    TRY_ANOTHER,
    FOUND,
    FINISHED,
}

data class Challenge(val activity: Activity, val target: Token, val choices: List<Token>) {
    init {
        require(target in choices && choices.distinct().size == choices.size)
    }
}

data class PlaySession(
    val challenges: List<Challenge>,
    val index: Int = 0,
    val feedback: Feedback = Feedback.EXPLORING,
) {
    init {
        require(challenges.isNotEmpty())
        require(index in challenges.indices)
    }

    val challenge
        get() = challenges[index]

    val flowers
        get() = index + if (feedback == Feedback.FOUND || feedback == Feedback.FINISHED) 1 else 0
}

fun interface ChallengeCatalog {
    fun create(activity: Activity): List<Challenge>
}

/** Strategy: each activity shares the protocol but provides different visual content. */
class GardenCatalog(private val random: Random = Random.Default) : ChallengeCatalog {
    override fun create(activity: Activity): List<Challenge> {
        val tokens =
            when (activity) {
                Activity.ANIMALS -> listOf(Token.CAT, Token.BIRD, Token.BUNNY)
                Activity.ANIMAL_SOUNDS -> AnimalCues.tokens
                Activity.LETTERS -> Alphabet.entries.map { it.token }
                Activity.COLORS -> listOf(Token.BLUE, Token.YELLOW, Token.GREEN)
                Activity.SHAPES -> listOf(Token.CIRCLE, Token.TRIANGLE, Token.SQUARE)
            }
        return tokens.shuffled(random).take(3).map { target ->
            val choices =
                if (activity == Activity.LETTERS)
                    (tokens.filterNot { it == target }.shuffled(random).take(2) + target).shuffled(
                        random
                    )
                else tokens.shuffled(random)
            Challenge(activity, target, choices)
        }
    }
}

/** Pure state machine: no score penalty, no clock, and no automatic next round. */
class GardenGame(private val catalog: ChallengeCatalog) {
    fun start(activity: Activity) = PlaySession(catalog.create(activity))

    fun choose(session: PlaySession, choice: Token): PlaySession {
        if (session.feedback == Feedback.FOUND || session.feedback == Feedback.FINISHED)
            return session
        if (choice !in session.challenge.choices) return session
        return session.copy(
            feedback =
                if (choice == session.challenge.target) Feedback.FOUND else Feedback.TRY_ANOTHER
        )
    }

    fun next(session: PlaySession): PlaySession {
        if (session.feedback != Feedback.FOUND) return session
        return if (session.index == session.challenges.lastIndex)
            session.copy(feedback = Feedback.FINISHED)
        else session.copy(index = session.index + 1, feedback = Feedback.EXPLORING)
    }
}

data class GardenSettings(val sound: Boolean = true, val reducedMotion: Boolean = false)

interface SettingsRepository {
    fun read(): GardenSettings

    fun save(settings: GardenSettings)
}
