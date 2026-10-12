package dev.breda.garden.domain

data class LetterEntry(
    val token: Token,
    val spokenName: String,
    val word: String,
    val picture: String,
) {
    val letter
        get() = requireNotNull(token.letter)

    val narration
        get() = "Letra $spokenName. $letter de $word."
}

object Alphabet {
    val entries =
        listOf(
            LetterEntry(Token.A, "á", "abelha", "🐝"),
            LetterEntry(Token.B, "bê", "bola", "⚽"),
            LetterEntry(Token.C, "cê", "casa", "🏠"),
            LetterEntry(Token.D, "dê", "dado", "🎲"),
            LetterEntry(Token.E, "é", "estrela", "⭐"),
            LetterEntry(Token.F, "efe", "flor", "🌷"),
            LetterEntry(Token.G, "gê", "gato", "🐱"),
            LetterEntry(Token.H, "agá", "helicóptero", "🚁"),
            LetterEntry(Token.I, "i", "ilha", "🏝"),
            LetterEntry(Token.J, "jota", "jacaré", "🐊"),
            LetterEntry(Token.K, "cá", "kiwi", "🥝"),
            LetterEntry(Token.L, "ele", "lua", "🌙"),
            LetterEntry(Token.M, "eme", "maçã", "🍎"),
            LetterEntry(Token.N, "ene", "nuvem", "☁"),
            LetterEntry(Token.O, "ó", "ovo", "🥚"),
            LetterEntry(Token.P, "pê", "pato", "🦆"),
            LetterEntry(Token.Q, "quê", "queijo", "🧀"),
            LetterEntry(Token.R, "erre", "rato", "🐭"),
            LetterEntry(Token.S, "esse", "sol", "☀"),
            LetterEntry(Token.T, "tê", "tartaruga", "🐢"),
            LetterEntry(Token.U, "u", "uva", "🍇"),
            LetterEntry(Token.V, "vê", "vaca", "🐄"),
            LetterEntry(Token.W, "dáblio", "wi-fi", "📶"),
            LetterEntry(Token.X, "xis", "xícara", "☕"),
            LetterEntry(Token.Y, "ípsilon", "Yara", "👧"),
            LetterEntry(Token.Z, "zê", "zebra", "🦓"),
        )

    fun entry(token: Token): LetterEntry =
        requireNotNull(entries.firstOrNull { it.token == token }) { "Not a letter" }
}

object AnimalCues {
    val tokens = listOf(Token.CAT, Token.DOG, Token.COW)

    fun recording(token: Token): String =
        when (token) {
            Token.CAT -> "animal_cat"
            Token.DOG -> "animal_dog"
            Token.COW -> "animal_cow"
            else -> error("Animal has no recording")
        }
}
