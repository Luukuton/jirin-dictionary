package com.mycompany.unicafe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class KassapaateTest {

    Kassapaate kassa;
    Maksukortti kortti;
    int alkusumma;

    @Before
    public void setUp() {
        kassa = new Kassapaate();
        kortti = new Maksukortti(100000);
        alkusumma = 100000;
    }

    @Test
    public void alkutilanneOnOikein() {
        assertEquals(alkusumma, kassa.kassassaRahaa());
        assertEquals(0, kassa.edullisiaLounaitaMyyty() + kassa.maukkaitaLounaitaMyyty());
    }

    @Test
    public void maukasKateismaksuToimiiOikeinJaPalauttaaOikeanMaaranVaihtorahaa() {
        int maksu = 500;
        int palautettavaRaha = kassa.syoMaukkaasti(maksu);
        assertEquals(100, palautettavaRaha);
        assertEquals(alkusumma + (maksu - palautettavaRaha), kassa.kassassaRahaa());
        assertEquals(1, kassa.edullisiaLounaitaMyyty() + kassa.maukkaitaLounaitaMyyty());
    }

    @Test
    public void edullinenKateismaksuToimiiOikeinJaPalauttaaOikeanMaaranVaihtorahaa() {
        int maksu = 300;
        int palautettavaRaha = kassa.syoEdullisesti(maksu);
        assertEquals(60, palautettavaRaha);
        assertEquals(alkusumma + (maksu - palautettavaRaha), kassa.kassassaRahaa());
        assertEquals(1, kassa.edullisiaLounaitaMyyty());
    }

    @Test
    public void kateismaksuOllessaRiittamatonMaukkaaseenPalautetaanRahatJaLounastaEiMyyda() {
        int maksu = 300;
        int palautettavaRaha = kassa.syoMaukkaasti(maksu);
        assertEquals(maksu, palautettavaRaha);
        assertEquals(alkusumma, kassa.kassassaRahaa());
        assertEquals(0, kassa.edullisiaLounaitaMyyty() + kassa.maukkaitaLounaitaMyyty());
    }

    @Test
    public void kateismaksunOllessaRiittamatonEdulliseenPalautetaanRahatJaLounastaEiMyyda() {
        int maksu = 100;
        int palautettavaRaha = kassa.syoEdullisesti(maksu);
        assertEquals(maksu, palautettavaRaha);
        assertEquals(alkusumma, kassa.kassassaRahaa());
        assertEquals(0, kassa.edullisiaLounaitaMyyty());
    }

    @Test
    public void maukasKorttimaksuToimiiOikeinJaPalauttaaTrue() {
        assertTrue(kassa.syoMaukkaasti(kortti));
        assertEquals(1, kassa.maukkaitaLounaitaMyyty());
    }

    @Test
    public void edullinenKorttimaksuToimiiOikeinJaPalauttaaTrue() {
        assertTrue(kassa.syoEdullisesti(kortti));
        assertEquals(1, kassa.edullisiaLounaitaMyyty());
    }

    @Test
    public void korttimaksunOllessaRiittamatonKortinSaldoJaMyydytMaukkaatLuonaatEiMuutuSekaPalautetaanFalse() {
        kortti.otaRahaa(99999);
        assertFalse(kassa.syoMaukkaasti(kortti));
        assertEquals(0, kassa.edullisiaLounaitaMyyty());
    }

    @Test
    public void korttimaksunOllessaRiittamatonKortinSaldoJaMyydytEdullisetLuonaatEiMuutuSekaPalautetaanFalse() {
        kortti.otaRahaa(99999);
        assertFalse(kassa.syoEdullisesti(kortti));
        assertEquals(0, kassa.edullisiaLounaitaMyyty());
    }

    @Test
    public void kassanRahanMaaraEiMuutuKortillaOstettaessa() {
        kassa.syoMaukkaasti(kortti);
        assertEquals(alkusumma, kassa.kassassaRahaa());
    }

    @Test
    public void kortilleRahaaLadattaessaKassanRahaKasvaaOikein() {
        kassa.lataaRahaaKortille(kortti, 1000);
        assertEquals(alkusumma + 1000, kassa.kassassaRahaa());
    }

    @Test
    public void kortilleLadattavanRahanMaaraOllessaNegatiivinenMitaanEiTapahdu() {
        kassa.lataaRahaaKortille(kortti, -1);
        assertEquals(alkusumma, kassa.kassassaRahaa());
    }
}
