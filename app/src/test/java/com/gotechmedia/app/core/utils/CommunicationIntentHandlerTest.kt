package com.gotechmedia.app.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class CommunicationIntentHandlerTest {

    @Test
    fun `verify agency contact constants match specifications`() {
        assertEquals("+919476325586", Constants.AGENCY_WHATSAPP_NUMBER)
        assertEquals("919476325586", Constants.AGENCY_WHATSAPP_PHONE_DIGITS)
        assertEquals("+919476325586", Constants.AGENCY_CALL_NUMBER)
        assertEquals("inquiries@gotechmedia.com", Constants.AGENCY_EMAIL)
        assertEquals("https://gotechmedia.com", Constants.AGENCY_WEBSITE_URL)
        assertEquals("Hi GoTech Media, I would like to discuss a project.", Constants.WHATSAPP_PREFILLED_MESSAGE)
    }
}
