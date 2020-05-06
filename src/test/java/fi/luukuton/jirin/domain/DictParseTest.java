package fi.luukuton.jirin.domain;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DictParseTest {

    private static DictParse parserBasic, parserNoKanji;

    @BeforeClass
    public static void setUp() throws Exception {
        // Basic URL for the dictionary.
        String mode = "/m0u/";

        // Dictionary entry for the word "猫" as in "cat".
        parserBasic = new DictParse( "/word/%E7%8C%AB");

        // Dictionary entry for the word "ふらふら" as in "staggering" or "wandering".
        // This is important to test because this word doesn't have a kanji form.
        // Simply put, the word and the reading of the word are the exact same.
        parserNoKanji = new DictParse("/word/%E3%81%B5%E3%82%89%E3%81%B5%E3%82%89");
    }

    @Test
    public void equalWhenWordParsedCorrectly() {
        assertEquals("猫", parserBasic.parseWord());
        assertEquals("ふらふら", parserNoKanji.parseWord());
    }

    @Test
    public void equalWhenWordReadingParsedCorrectly() {
        assertEquals("ねこ", parserBasic.parseReading());
        assertEquals("ふらふら", parserNoKanji.parseReading());
    }

    @Test
    public void equalWhenWordDefinitionsParsedCorrectly() {
        String secondDefinition = "２ 《胴を猫の皮で張るところから》三味線のこと。";
        String lastDefinition = "５ 「猫車」の略。";
        assertEquals(secondDefinition, parserBasic.parseDefinition().get(1));
        assertEquals(lastDefinition, parserBasic.parseDefinition().get(5));

        secondDefinition = "２ からだに力がはいらないさま。意識がはっきりしないさま。「熱でふらふらする」「ふらふらした足どり」";
        lastDefinition = "４ 態度が定まらないさま。「方針がふらふらする」";
        assertEquals(secondDefinition, parserNoKanji.parseDefinition().get(1));
        assertEquals(lastDefinition, parserNoKanji.parseDefinition().get(3));
    }
}
