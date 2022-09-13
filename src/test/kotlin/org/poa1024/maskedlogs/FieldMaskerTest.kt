package org.poa1024.maskedlogs

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FieldMaskerTest {

    private val fieldMasker = FieldMasker(listOf("order", "apikey", "unique_app_id", "p_unique_app_id", "person_id")) { "<MASKED>" }

    @Test
    fun testMaskUrl() {
        val log = "https://poa1024.com/order/12?apikey=someApiKey&num=8787&APIKEY=someApiKey&apikey=someApiKey"
        val expectedMaskedLOg = "https://poa1024.com/order/<MASKED>?apikey=<MASKED>&num=8787&APIKEY=<MASKED>&apikey=<MASKED>"
        val maskedLog = fieldMasker.mask(log)
        assertThat(maskedLog).isEqualTo(expectedMaskedLOg)
    }

    @Test
    fun testMaskMap() {
        val log = "{person_id=12345, unique_app_id   =   txt1231  ,uniqueAppId=txt1232,unique-app-id=txt1233, p_unique_app_id=txt1234), my_surname=Perekhod, mySurname=Perekhod, my-surname=Perekhod, null=null, empty=, number=1234}"
        val expectedMaskedLog = "{person_id=<MASKED>, unique_app_id   =   <MASKED>  ,uniqueAppId=<MASKED>,unique-app-id=<MASKED>, p_unique_app_id=<MASKED>), my_surname=Perekhod, mySurname=Perekhod, my-surname=Perekhod, null=null, empty=, number=1234}"
        val maskedLog = fieldMasker.mask(log)
        assertThat(maskedLog).isEqualTo(expectedMaskedLog)
    }

    @Test
    fun testMaskJson() {
        val log = "{\"person_id\":12345,\"unique_app_id\":\"txt1234\", \"uniqueAppId\"  : \"txt1234\" ,\"unique-app-id\":\"txt1234\",\"p_unique_app_id\":\"txt1234\",\"my_surname\":\"Perekhod\",\"mySurname\":\"Perekhod\",\"my-surname\":\"Perekhod\",\"null\":null,\"empty\":\"\",\"number\":1234}"
        val expectedMaskedLog = "{\"person_id\":<MASKED>,\"unique_app_id\":\"<MASKED>\", \"uniqueAppId\"  : \"<MASKED>\" ,\"unique-app-id\":\"<MASKED>\",\"p_unique_app_id\":\"<MASKED>\",\"my_surname\":\"Perekhod\",\"mySurname\":\"Perekhod\",\"my-surname\":\"Perekhod\",\"null\":null,\"empty\":\"\",\"number\":1234}"
        val maskedLog = fieldMasker.mask(log)
        assertThat(maskedLog).isEqualTo(expectedMaskedLog)
    }

}