package com.bizpulse.app
import com.bizpulse.app.util.PasswordHasher
import org.junit.Assert.*
import org.junit.Test
class PasswordHasherTest {
    @Test fun hashIsStableAndNotPlaintext() {assertEquals(64,PasswordHasher.hash("owner123").length);assertNotEquals("owner123",PasswordHasher.hash("owner123"));assertEquals(PasswordHasher.hash("owner123"),PasswordHasher.hash("owner123"))}
    @Test fun verificationRejectsWrongPassword() {assertTrue(PasswordHasher.verify("owner123",PasswordHasher.hash("owner123")));assertFalse(PasswordHasher.verify("wrong",PasswordHasher.hash("owner123")))}
    @Test fun saltConsistency() {assertEquals("338674e21fc4e5bac394a4334ca2ba020dba8d2650f1f04cbad3f32ba9e8ff21",PasswordHasher.hash("owner123"))}
}
