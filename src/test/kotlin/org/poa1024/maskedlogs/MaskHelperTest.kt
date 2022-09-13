package org.poa1024.maskedlogs

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MaskHelperTest {

    private val maskHelper =
        MaskHelper(60.0, listOf("order", "apikey", "mobile_app_id", "p_mobile_app_id", "person_id"))

    @Test
    fun testMaskFullReqLog() {
        val log =
            """
            2022-07-25 12:46:15,633 INFO ort.poa1024.app.ClientLoggingInterceptor [http-nio-8080-exec-3] Request
            POST https://poa1024.com/person_id/poa1024/order/214351?apikey=someApiKey
            Accept: application/json, application/*+json
            Content-Type: application/json
            Content-Length: 433
            """.trimIndent()
        val expectedMaskedLog =
            """
            2022-07-25 12:46:15,633 INFO ort.poa1024.app.ClientLoggingInterceptor [http-nio-8080-exec-3] Request
            POST https://poa1024.com/person_id/p****24/order/2***51?apikey=so******ey
            Accept: application/json, application/*+json
            Content-Type: application/json
            Content-Length: 433
            """.trimIndent()
        val maskedLog = maskHelper.maskText(log)
        assertThat(maskedLog).isEqualTo(expectedMaskedLog)
    }

    @Test
    fun testMaskUrl() {
        val log =
            "https://poa1024.com/order/12?apikey=someApiKey&num=8787&APIKEY=someApiKey&apikey=someApiKey"
        val expectedMaskedLOg =
            "https://poa1024.com/order/**?apikey=so******ey&num=8787&APIKEY=so******ey&apikey=so******ey"
        val maskedLog = maskHelper.maskText(log)
        assertThat(maskedLog).isEqualTo(expectedMaskedLOg)
    }

    @Test
    fun testMaskMap() {
        val log =
            "{person_id=12345, mobile_app_id   =   txt1231  ,mobileAppId=txt1232,mobile-app-id=txt1233, p_mobile_app_id=txt1234), my_surname=Perekhod, mySurname=Perekhod, my-surname=Perekhod, null=null, empty=, number=1234}"
        val expectedMaskedLog =
            "{person_id=1***5, mobile_app_id   =   t****31  ,mobileAppId=t****32,mobile-app-id=t****33, p_mobile_app_id=t****34), my_surname=Perekhod, mySurname=Perekhod, my-surname=Perekhod, null=null, empty=, number=1234}"
        val maskedLog = maskHelper.maskText(log)
        assertThat(maskedLog).isEqualTo(expectedMaskedLog)
    }

    @Test
    fun testMaskJson() {
        val log =
            "{\"person_id\":12345,\"mobile_app_id\":\"txt1234\", \"mobileAppId\"  : \"txt1234\" ,\"mobile-app-id\":\"txt1234\",\"p_mobile_app_id\":\"txt1234\",\"my_surname\":\"Perekhod\",\"mySurname\":\"Perekhod\",\"my-surname\":\"Perekhod\",\"null\":null,\"empty\":\"\",\"number\":1234}"
        val expectedMaskedLog =
            "{\"person_id\":1***5,\"mobile_app_id\":\"t*****4\", \"mobileAppId\"  : \"t*****4\" ,\"mobile-app-id\":\"t*****4\",\"p_mobile_app_id\":\"t*****4\",\"my_surname\":\"Perekhod\",\"mySurname\":\"Perekhod\",\"my-surname\":\"Perekhod\",\"null\":null,\"empty\":\"\",\"number\":1234}"
        val maskedLog = maskHelper.maskText(log)
        assertThat(maskedLog).isEqualTo(expectedMaskedLog)
    }

    @Test
    fun testMaskValue() {
        assertThat(maskHelper.maskValue("1")).isEqualTo("***")
        assertThat(maskHelper.maskValue("12")).isEqualTo("***")
        assertThat(maskHelper.maskValue("123")).isEqualTo("1*3")
        assertThat(maskHelper.maskValue("1234")).isEqualTo("1**4")
        assertThat(maskHelper.maskValue("12345")).isEqualTo("1***5")
        assertThat(maskHelper.maskValue("123456")).isEqualTo("1***56")
        assertThat(maskHelper.maskValue("1234567")).isEqualTo("1****67")
        assertThat(maskHelper.maskValue("12345678")).isEqualTo("12****78")
        assertThat(maskHelper.maskValue("123456789")).isEqualTo("12*****89")
        assertThat(maskHelper.maskValue("1234567890")).isEqualTo("12******90")
        assertThat(maskHelper.maskValue("12345678901")).isEqualTo("12******901")
        assertThat(maskHelper.maskValue("123456789012")).isEqualTo("12*******012")
        assertThat(maskHelper.maskValue("1234567890123")).isEqualTo("123*******123")
        assertThat(maskHelper.maskValue("12345678901234")).isEqualTo("123********234")
        assertThat(maskHelper.maskValue("123456789012345")).isEqualTo("123*********345")
        assertThat(maskHelper.maskValue("1234567890123456")).isEqualTo("123*********3456")
        assertThat(maskHelper.maskValue("12345678901234567")).isEqualTo("123**********4567")
        assertThat(maskHelper.maskValue("123456789012345678")).isEqualTo("1234**********5678")
        assertThat(maskHelper.maskValue("1234567890123456789")).isEqualTo("1234***********6789")
        assertThat(maskHelper.maskValue("12345678901234567890")).isEqualTo("1234************7890")
    }
}