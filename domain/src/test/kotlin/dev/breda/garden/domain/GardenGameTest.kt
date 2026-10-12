package dev.breda.garden.domain

import kotlin.random.Random
import org.junit.Assert.*
import org.junit.Test

class GardenGameTest {
    private val game = GardenGame(GardenCatalog(Random(7)))

    @Test
    fun eachActivityOffersEveryTargetExactlyOnceAndContainsTheAnswer() {
        for (activity in Activity.entries) {
            val session = game.start(activity)
            assertEquals(3, session.challenges.size)
            assertEquals(3, session.challenges.map { it.target }.distinct().size)
            session.challenges.forEach {
                assertTrue(it.target in it.choices)
                assertEquals(activity, it.activity)
            }
        }
    }

    @Test
    fun wrongChoiceDoesNotRemoveProgressOrAdvanceAndCanBeCorrected() {
        val initial = game.start(Activity.COLORS)
        val retry =
            game.choose(initial, initial.challenge.choices.first { it != initial.challenge.target })
        assertEquals(Feedback.TRY_ANOTHER, retry.feedback)
        assertEquals(0, retry.flowers)
        assertEquals(initial.challenge, retry.challenge)
        assertEquals(retry, game.next(retry))
        assertEquals(Feedback.FOUND, game.choose(retry, retry.challenge.target).feedback)
    }

    @Test
    fun repeatTapCannotCountTwiceAndFinishingRequiresExplicitNext() {
        var session = game.start(Activity.ANIMALS)
        repeat(3) {
            session = game.choose(session, session.challenge.target)
            assertEquals(session, game.choose(session, session.challenge.target))
            session = game.next(session)
        }
        assertEquals(Feedback.FINISHED, session.feedback)
        assertEquals(3, session.flowers)
        assertEquals(session, game.next(session))
    }

    @Test
    fun alphabetContainsAll26LettersWithDistinctWordsAndPronunciations() {
        assertEquals(('A'..'Z').toList(), Alphabet.entries.map { it.letter })
        assertEquals(26, Alphabet.entries.map { it.token }.distinct().size)
        Alphabet.entries.forEach {
            assertTrue(it.word.isNotBlank())
            assertTrue(it.spokenName.isNotBlank())
            assertEquals(it, Alphabet.entry(it.token))
        }
    }

    @Test
    fun letterRoundsOfferThreeDistinctChoicesAndVaryBeyondTheFirstLetters() {
        val targets = mutableSetOf<Token>()
        repeat(100) {
            val session = game.start(Activity.LETTERS)
            session.challenges.forEach { challenge ->
                assertEquals(3, challenge.choices.distinct().size)
                assertTrue(challenge.choices.all { it.letter != null })
                targets += challenge.target
            }
        }
        assertEquals(Alphabet.entries.map { it.token }.toSet(), targets)
    }

    @Test
    fun recordedAnimalChallengeOnlyOffersAnimalsWithBundledSounds() {
        val session = game.start(Activity.ANIMAL_SOUNDS)
        assertEquals(
            setOf(Token.CAT, Token.DOG, Token.COW),
            session.challenges.map { it.target }.toSet(),
        )
        session.challenges.forEach { challenge ->
            challenge.choices.forEach { assertTrue(AnimalCues.recording(it).startsWith("animal_")) }
        }
    }

    @Test
    fun tokensFromAnotherActivityAreIgnored() {
        val session = game.start(Activity.SHAPES)
        assertEquals(session, game.choose(session, Token.CAT))
    }
}
