package br.com.mobicare.cielo.commons.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringUtilsTest {

    private val stringsToBeNormalized = listOf(
        "Um texto comum",
        "Pré-aprovado",
        "Hello World!!",
        "  Remover espaços na esquerda e direita    ",
        "Snake_Case_Text",
        "CAIXA ALTA",
        "The quick, brown fox jumps over a lazy dog. DJs flock by when MTV ax quiz prog. Junk MTV quiz graced by fox whelps. Bawd",
    )

    private val expectedNormalizedStrings = listOf(
        "um_texto_comum",
        "pre_aprovado",
        "hello_world",
        "remover_espacos_na_esquerda_e_direita",
        "snake_case_text",
        "caixa_alta",
        "the_quick_brown_fox_jumps_over_a_lazy_dog_djs_flock_by_when_mtv_ax_quiz_prog_junk_mtv_quiz_graced_by"
    )

    @Test
    fun `it should return the correct normalized string on normalizeToLowerSnakeCase call`() {
        stringsToBeNormalized.forEachIndexed { i, string ->
            val result = string.normalizeToLowerSnakeCase()
            assertThat(result).isEqualTo(expectedNormalizedStrings[i])
        }
    }

}