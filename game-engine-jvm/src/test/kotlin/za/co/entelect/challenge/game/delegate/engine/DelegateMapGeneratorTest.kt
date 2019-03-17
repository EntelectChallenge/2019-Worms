package za.co.entelect.challenge.game.delegate.engine

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import za.co.entelect.challenge.game.contracts.player.Player
import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import kotlin.test.Test

class DelegateMapGeneratorTest {

    /**
     * This test simply exists as an integration test to verify that no exceptions gets thrown during end to end map generation
     */
    @Test
    fun test_generator() {
        val delegateMapGenerator = DelegateMapGenerator(TEST_CONFIG, 0L)

        val mockPlayer1: Player = mock {
            on{name} doReturn "Player 1"
            on{number} doReturn 1
        }

        val mockPlayer2: Player = mock {
            on{name} doReturn "Player 2"
            on{number} doReturn 2
        }

        delegateMapGenerator.generateGameMap(listOf(mockPlayer1, mockPlayer2))
    }
}