package com.astro.app.core.phrases

import com.astro.app.core.phrases.PhraseImport.Reason
import org.junit.Assert.assertEquals
import org.junit.Test

class PhraseFileParserTest {

    @Test
    fun `objets avec et sans auteur`() {
        val result = PhraseFileParser.parse(
            """[ { "texte": " Ose. ", "auteur": "Moi" }, { "texte": "Avance." } ]""",
        )
        assertEquals(PhraseImport.Success(listOf(Phrase("Ose.", "Moi"), Phrase("Avance.", ""))), result)
    }

    @Test
    fun `une chaine seule est une phrase sans auteur`() {
        assertEquals(PhraseImport.Success(listOf(Phrase("Respire."))), PhraseFileParser.parse("""["Respire."]"""))
    }

    @Test
    fun `les doublons exacts sont ignores`() {
        val result = PhraseFileParser.parse("""["A", {"texte":"A"}, "B"]""") as PhraseImport.Success
        assertEquals(listOf("A", "B"), result.phrases.map { it.text })
    }

    @Test
    fun `accents, guillemets et champs inconnus passent`() {
        val result = PhraseFileParser.parse(
            """[{"texte":"L'été « arrive »","auteur":"Élise","note":5}]""",
        ) as PhraseImport.Success
        assertEquals(Phrase("L'été « arrive »", "Élise"), result.phrases.single())
    }

    @Test
    fun `une phrase sans texte fait tout refuser, en disant laquelle`() {
        val result = PhraseFileParser.parse("""["A", {"auteur":"X"}, "C"]""")
        assertEquals(PhraseImport.Failure(Reason.MISSING_TEXT, 2), result)
        assertEquals(PhraseImport.Failure(Reason.MISSING_TEXT, 1), PhraseFileParser.parse("""["   "]"""))
        assertEquals(PhraseImport.Failure(Reason.MISSING_TEXT, 1), PhraseFileParser.parse("""[42]"""))
    }

    @Test
    fun `fichiers mal formes`() {
        assertEquals(PhraseImport.Failure(Reason.NOT_JSON), PhraseFileParser.parse("""["A",""""))
        assertEquals(PhraseImport.Failure(Reason.NOT_A_LIST), PhraseFileParser.parse("""{"texte":"A"}"""))
        assertEquals(PhraseImport.Failure(Reason.EMPTY), PhraseFileParser.parse("[]"))
    }

    @Test
    fun `theme et genre sont lus, genre neutre par defaut`() {
        val result = PhraseFileParser.parse(
            """[{"texte":"A","theme":" Paix ","genre":"Féminin"},{"texte":"B","genre":"feminin"},{"texte":"C"}]""",
        ) as PhraseImport.Success
        assertEquals(
            listOf(
                Phrase("A", theme = "Paix", gender = PhraseGender.FEMININE),
                Phrase("B", gender = PhraseGender.FEMININE),
                Phrase("C"),
            ),
            result.phrases,
        )
    }

    @Test
    fun `un genre inconnu fait tout refuser`() {
        assertEquals(
            PhraseImport.Failure(Reason.UNKNOWN_GENDER, 2),
            PhraseFileParser.parse("""[{"texte":"A"},{"texte":"B","genre":"homme"}]"""),
        )
    }
}
