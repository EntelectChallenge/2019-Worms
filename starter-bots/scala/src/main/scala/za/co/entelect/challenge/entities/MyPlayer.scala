package za.co.entelect.challenge.entities

case class MyPlayer(id: Int,
                    score: Int,
                    health: Int,
                    worms: List[MyWorm])