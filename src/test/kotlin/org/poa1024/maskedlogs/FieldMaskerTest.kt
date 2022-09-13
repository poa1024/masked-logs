package org.poa1024.maskedlogs

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.poa1024.maskedlogs.masker.AsteriskMasker

class FieldMaskerTest {

    private val fieldMasker = FieldMasker(
        listOf("order", "apikey", "mobile_app_id", "p_mobile_app_id", "person_id"),
        AsteriskMasker(60.0)
    )

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
        val maskedLog = fieldMasker.mask(log)
        assertThat(maskedLog).isEqualTo(expectedMaskedLog)
    }

    @Test
    fun testMaskUrl() {
        val log =
            "https://poa1024.com/order/12?apikey=someApiKey&num=8787&APIKEY=someApiKey&apikey=someApiKey"
        val expectedMaskedLOg =
            "https://poa1024.com/order/**?apikey=so******ey&num=8787&APIKEY=so******ey&apikey=so******ey"
        val maskedLog = fieldMasker.mask(log)
        assertThat(maskedLog).isEqualTo(expectedMaskedLOg)
    }

    @Test
    fun testMaskMap() {
        val log =
            "{person_id=12345, mobile_app_id   =   txt1231  ,mobileAppId=txt1232,mobile-app-id=txt1233, p_mobile_app_id=txt1234), my_surname=Perekhod, mySurname=Perekhod, my-surname=Perekhod, null=null, empty=, number=1234}"
        val expectedMaskedLog =
            "{person_id=1***5, mobile_app_id   =   t****31  ,mobileAppId=t****32,mobile-app-id=t****33, p_mobile_app_id=t****34), my_surname=Perekhod, mySurname=Perekhod, my-surname=Perekhod, null=null, empty=, number=1234}"
        val maskedLog = fieldMasker.mask(log)
        assertThat(maskedLog).isEqualTo(expectedMaskedLog)
    }

    @Test
    fun testMaskJson() {
        val log =
            "{\"person_id\":12345,\"mobile_app_id\":\"txt1234\", \"mobileAppId\"  : \"txt1234\" ,\"mobile-app-id\":\"txt1234\",\"p_mobile_app_id\":\"txt1234\",\"my_surname\":\"Perekhod\",\"mySurname\":\"Perekhod\",\"my-surname\":\"Perekhod\",\"null\":null,\"empty\":\"\",\"number\":1234}"
        val expectedMaskedLog =
            "{\"person_id\":1***5,\"mobile_app_id\":\"t*****4\", \"mobileAppId\"  : \"t*****4\" ,\"mobile-app-id\":\"t*****4\",\"p_mobile_app_id\":\"t*****4\",\"my_surname\":\"Perekhod\",\"mySurname\":\"Perekhod\",\"my-surname\":\"Perekhod\",\"null\":null,\"empty\":\"\",\"number\":1234}"
        val maskedLog = fieldMasker.mask(log)
        assertThat(maskedLog).isEqualTo(expectedMaskedLog)
    }


}