class Tensor(val dim: Int, val shape: IntArray, val data: DoubleArray =
    DoubleArray(shape.reduce {
            total, num ->
            if (num <= 0) throw IllegalArgumentException("Tensor.init: Invalid shape")
            else total * num
        }
    )
) {
    val size: Int = data.size

    init {
        if (dim < 0) throw IllegalArgumentException("Tensor.init: dimension must be a non-negative integer")
        if (shape.size != dim) throw IllegalArgumentException("Tensor.init: shape.size != dim")
        if (size != calculateSize()) throw IllegalArgumentException("Tensor.init: Invalid data length")
    }

    operator fun get(indices: IntArray): Double {
        if (indices.size != dim) throw IllegalArgumentException("Tensor.get: Too many indices")
        else {
            val totalIndex = indices.reduceIndexed { index, acc, tensorIndex ->
                if (tensorIndex >= shape[index]) throw IllegalArgumentException("Tensor.get: Index out of bound")
                (acc * shape[index]) + tensorIndex
            }
            return data[totalIndex]
        }
    }

    operator fun get(index: Int): Tensor {
        return when {
            index >= shape[0] -> throw IllegalArgumentException("Tensor.get: Index out of bound")
            dim == 0 -> throw IllegalArgumentException("Tensor.get: cannot get from 0-dimensional tensor. use [intArrayOf()] to get value.")
            else -> {
                val newShape = (1..shape.lastIndex).map { shape[it] }.toIntArray()
                val newTensorSize = newShape.reduce { total, num -> total * num }
                val dataIndexStart = index * newTensorSize
                val dataIndexEnd = (index + 1) * newTensorSize
                val newData = (dataIndexStart until dataIndexEnd).map { data[it] }.toDoubleArray()
                Tensor(dim - 1, newShape, newData)
            }
        }
    }

    private fun calculateSize(): Int {
        return shape.reduce {
                total, num ->
            if (num <= 0) throw IllegalArgumentException("Tensor.init: Invalid shape")
            else total * num
        }
    }

    private class StringVector(val stringData: ArrayList<String>) {
        override fun toString(): String {
            var retStr = ""
            stringData.forEachIndexed {index, value ->
                retStr += value
                if (index != stringData.lastIndex) retStr += "\n"
            }
            return retStr
        }

        fun concatHorizontal(other: StringVector): StringVector {
            return if (stringData.size != other.stringData.size) throw IllegalArgumentException("StringMatrix: invalid Size")
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

    private fun toStringVector(): StringVector {
        return when {
            dim == 1 -> {
                val stringData = arrayListOf<String>()
                var data = "[ "
                for (i in 0 until shape[0]) {
                    val value = this[intArrayOf(i)]
                    data += when {
                        value >= 1000 -> " %.0f ".format(value)
                        value >= 100 -> " %.0f. ".format(value)
                        value >= 10 -> " %.1f ".format(value)
                        value == -0.0 -> " 0.00 "
                        value >= 0 -> " %.2f ".format(value)
                        value > -10 -> "%.2f ".format(value)
                        value > -100 -> "%.1f ".format(value)
                        value > -1000 -> "%.0f. ".format(value)
                        else -> "%.0f ".format(value)
                    }
                }
                data += " ]"
                stringData.add(data)
                StringVector(stringData)
            }
            dim == 2 -> {
                val stringData = arrayListOf<String>()
                for (i in 0 until shape[0]) {
                    var data = "[ "
                    for (j in 0 until shape[1]) {
                        val value = this[intArrayOf(i, j)]
                        data += when {
                            value >= 1000   -> " %.0f " .format(value)
                            value >= 100    -> " %.0f. ".format(value)
                            value >= 10     -> " %.1f " .format(value)
                            value == -0.0   -> " 0.00 "
                            value >= 0      -> " %.2f " .format(value)
                            value > -10     ->  "%.2f " .format(value)
                            value > -100    ->  "%.1f " .format(value)
                            value > -1000   ->  "%.0f. ".format(value)
                            else            ->  "%.0f " .format(value)
                        }
                    }
                    data += " ]"
                    stringData.add(data)
                }
                StringVector(stringData)
            }
            else -> {
                val leftBracketData = arrayListOf<String>()
                val rightBracketData = arrayListOf<String>()
                val upperBlankData =  arrayListOf<String>()
                val lowerBlankData =  arrayListOf<String>()
                when {
                    dim % 2 == 0 -> {
                        val height = shape.foldRightIndexed(1) { index, i, acc ->
                            when {
                                index % 2 == 1                  -> acc
                                index == shape.lastIndex - 1    -> acc * i
                                else                            -> acc * i + i + 1
                            }
                        }
                        repeat(height) { leftBracketData.add("["); rightBracketData.add("]") }
                        val leftBracket = StringVector(leftBracketData)
                        val rightBracket = StringVector(rightBracketData)
                        val bodyStringVector = (1 until shape[0]).fold(
                            (1 until shape[1]).fold(this[0][0].toStringVector()) { acc2, j ->
                                acc2.concatHorizontal(this[0][j].toStringVector())
                            }
                        ) { acc1, i ->
                            acc1.concatVertical(
                                (1 until shape[1]).fold(this[i][0].toStringVector()) { acc2, j ->
                                    acc2.concatHorizontal(this[i][j].toStringVector())
                                }
                            )
                        }
                        val bodyUpperWidth = bodyStringVector.stringData[0].length
                        val bodyLowerWidth = bodyStringVector.stringData.last().length
                        upperBlankData.add(" ".repeat(bodyUpperWidth))
                        lowerBlankData.add(" ".repeat(bodyLowerWidth))
                        val upperBlank = StringVector(upperBlankData)
                        val lowerBlank = StringVector(lowerBlankData)

                        val bodyStringVectorWithPadding = upperBlank.rawConcatVertical(bodyStringVector).rawConcatVertical(lowerBlank)
                        leftBracket.concatHorizontal(bodyStringVectorWithPadding).concatHorizontal(rightBracket)
                    }
                    else -> {
                        val height = shape.foldRightIndexed(1) { index, i, acc ->
                            when {
                                index == 0 -> acc + 2
                                index % 2 == 0 -> acc
                                index == shape.lastIndex - 1 -> acc * i
                                else -> acc*i + i + 1
                            }
                        }
                        repeat(height) { leftBracketData.add("["); rightBracketData.add("]") }
                        val leftBracket = StringVector(leftBracketData)
                        val rightBracket = StringVector(rightBracketData)
                        val bodyStringVector = (1 until shape[0]).fold(this[0].toStringVector()) { acc, i ->
                            acc.concatHorizontal(this[i].toStringVector())
                        }
                        val bodyUpperWidth = bodyStringVector.stringData[0].length
                        val bodyLowerWidth = bodyStringVector.stringData.last().length
                        upperBlankData.add(" ".repeat(bodyUpperWidth))
                        lowerBlankData.add(" ".repeat(bodyLowerWidth))
                        val upperBlank = StringVector(upperBlankData)
                        val lowerBlank = StringVector(lowerBlankData)

                        val bodyStringVectorWithPadding = upperBlank.rawConcatVertical(bodyStringVector).rawConcatVertical(lowerBlank)
                        leftBracket.concatHorizontal(bodyStringVectorWithPadding).concatHorizontal(rightBracket)
                    }
                }
            }
        }
    }

    override fun toString(): String {
        return toStringVector().toString()
    }
}