package jirin.domain;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DictEntryTest {
    @Test
    public void equalWhenCorrectlyHexEncodeWord() {
        ArrayList<String> meanings = new ArrayList<>();
        meanings.add("２ 《胴を猫の皮で張るところから》三味線のこと。");

        DictEntry entry = new DictEntry("猫", "ねこ", meanings);

        assertEquals("%E7%8C%AB", entry.hexEncodeWord());
    }
}
