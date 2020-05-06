package fi.luukuton.jirin.domain;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DictEntryTest {
    @Test
    public void equalWhenCorrectlyHexEncodeWord() {
        ArrayList<String> definitions = new ArrayList<>();
        definitions.add("２ 《胴を猫の皮で張るところから》三味線のこと。");

        DictEntry entry = new DictEntry("猫", "ねこ", definitions);

        assertEquals("%E7%8C%AB", entry.hexEncodeWord());
    }
}
