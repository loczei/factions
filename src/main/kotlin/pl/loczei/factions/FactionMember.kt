package pl.loczei.factions

import java.util.*

class FactionMember(private var rank: Byte, private val uuid: UUID) {

    fun setRank(rank: Byte) { this.rank = rank }

    fun getRank() : Byte { return rank }

    fun getUUID() : UUID { return uuid }

    override fun toString () : String {
        return "$uuid;$rank"
    }

    companion object {
        fun fromString(string: String) : FactionMember {
            return FactionMember(
                string.subSequence(string.indexOf(";") + 1, 100).toString().toByte(),
                UUID.fromString(string.subSequence(0, string.indexOf(";") - 1).toString())
            )
        }
    }
}