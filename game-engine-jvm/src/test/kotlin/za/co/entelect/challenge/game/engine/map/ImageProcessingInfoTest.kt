package za.co.entelect.challenge.game.engine.map

import kotlin.test.Test
import kotlin.test.assertEquals

class ImageProcessingInfoTest {

    @Test
    fun test_properties() {
        val imageProcessingInfo = ImageProcessingInfo()
        imageProcessingInfo.srcValue = 1.0
        imageProcessingInfo.cookedValue = 1.0
        imageProcessingInfo.flag = 1

        assertEquals(imageProcessingInfo.srcValue, 1.0)
        assertEquals(imageProcessingInfo.cookedValue, 1.0)
        assertEquals(imageProcessingInfo.flag, 1)
    }

}
