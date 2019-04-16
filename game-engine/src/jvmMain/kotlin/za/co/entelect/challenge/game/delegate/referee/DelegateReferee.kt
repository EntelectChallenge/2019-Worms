package za.co.entelect.challenge.game.delegate.referee

import za.co.entelect.challenge.game.contracts.game.GameReferee

class DelegateReferee : GameReferee {
    override fun isMatchValid(): Boolean {
        return true
    }
}
