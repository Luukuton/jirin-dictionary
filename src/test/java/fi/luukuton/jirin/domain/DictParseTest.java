package fi.luukuton.jirin.domain;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DictParseTest {

    private static DictParse parserBasic, parserNoKanji;

    @BeforeClass
    public static void setUp() throws Exception {
        // Dictionary entry for the word "猫" as in "cat".
        parserBasic = new DictParse( "/word/%E7%8C%AB");

        // Dictionary entry for the word "ふらふら" as in "staggering" or "wandering".
        // This is important to test because this word doesn't have a kanji form.
        // Simply put, the word and the reading of the word are the exact same.
        parserNoKanji = new DictParse("/word/%E3%81%B5%E3%82%89%E3%81%B5%E3%82%89");
    }

    @Test
    public void equalWhenNormalWordParsedCorrectly() {
        assertEquals("猫", parserBasic.parseWord());
    }

    @Test
    public void equalWhenNoKanjiWordParsedCorrectly() {
        assertEquals("ふらふら", parserNoKanji.parseWord());
    }

    @Test
    public void equalWhenNormalWordReadingParsedCorrectly() {
        assertEquals("ねこ", parserBasic.parseReading());
    }

    @Test
    public void equalWhenNoKanjiWordReadingParsedCorrectly() {
        assertEquals("ふらふら", parserNoKanji.parseReading());
    }

    @Test
    public void equalWhenNormalWordDefinitionsParsedCorrectly() {
        assertEquals(
                "２ 《胴を猫の皮で張るところから》三味線のこと。",
                parserBasic.parseDefinition().get(1)
        );
        assertEquals(
                "５ 「猫車」の略。",
                parserBasic.parseDefinition().get(5)
        );
    }

    @Test
    public void equalWhenNoKanjiWordDefinitionsParsedCorrectly() {
        assertEquals(
                "２ からだに力がはいらないさま。意識がはっきりしないさま。「熱でふらふらする」「ふらふらした足どり」",
                parserNoKanji.parseDefinition().get(1)
        );
        assertEquals(
                "４ 態度が定まらないさま。「方針がふらふらする」",
                parserNoKanji.parseDefinition().get(3)
        );
    }
}
