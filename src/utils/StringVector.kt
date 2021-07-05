package utils


class StringVector(val stringData: ArrayList<String>) {
    override fun toString(): String {
        var retStr = ""
        stringData.forEachIndexed {index, value ->
            retStr += value
            if (index != stringData.lastIndex) retStr += "\n"
        }
        return retStr
    }

    fun concatHorizontal(other: StringVector): StringVector {
        return if (stringData.size != other.stringData.size) throw IllegalArgumentException("StringVector: invalid Size")
        else {
            val newStringData = arrayListOf<String>()
            stringData.forEachIndexed {index, str -> newStringData.add(str + "  " + other.stringData[index])}
            StringVector(newStringData)
        }
    }

    fun concatVertical(other: StringVector): StringVector {
        return StringVector((stringData + arrayListOf(" ".repeat(other.stringData[0].length)) + other.stringData) as ArrayList<String>)
    }

    fun rawConcatVertical(other: StringVector): StringVector {
        return StringVector((stringData + other.stringData) as ArrayList<String>)
    }
}