package com.application.projecttbh;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void AEStest() {
        String original = "howtodoinjava.com";
        String encryptedString = AES.encrypt(original) ;
        String decryptedString = AES.decrypt(encryptedString) ;
        assertEquals(original, decryptedString);
    }

}