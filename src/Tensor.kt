open class Tensor(val dim: Int, val shape: IntArray, val data: DoubleArray =
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

    constructor(dim: Int, shape: IntArray, data: LongArray) :
            this(dim, shape, DoubleArray(shape.reduce {tot, num -> tot * num}) { data[it].toDouble() })

    constructor(dim: Int, shape: IntArray, data: FloatArray) :
            this(dim, shape, DoubleArray(shape.reduce {tot, num -> tot * num}) { data[it].toDouble() })

    constructor(dim: Int, shape: IntArray, data: IntArray) :
            this(dim, shape, DoubleArray(shape.reduce {tot, num -> tot * num}) { data[it].toDouble() })

    open operator fun get(indices: IntArray): Double {
        if (indices.size != dim) throw IllegalArgumentException("Tensor.get: Too many indices")
        else {
            val totalIndex = indices.reduceIndexed { index, acc, tensorIndex ->
                if (tensorIndex >= shape[index]) throw IllegalArgumentException("Tensor.get: Index out of bound")
                (acc * shape[index]) + tensorIndex
            }
            return data[totalIndex]
        }
    }

    open operator fun get(indexLong: Long): Tensor {
        val index = indexLong.toInt()
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

    open operator fun unaryPlus() = this

    open operator fun unaryMinus(): Tensor {
        return Tensor(dim, shape, DoubleArray(size) {- data[it]})
    }

    operator fun plus(other: Tensor): Tensor {
        shape.forEachIndexed { index, it ->
            if (it != other.shape[index]) throw IllegalArgumentException("Tensor.plus: Two tensors should have the same shape.")
        }
        val newData = DoubleArray(size) {
            data[it] + other.data[it]
        }
        return Tensor(dim, shape, newData)
    }

    operator fun minus(other: Tensor): Tensor {
        shape.forEachIndexed { index, it ->
            if (it != other.shape[index]) throw IllegalArgumentException("Tensor.minus: Two tensors should have the same shape.")
        }
        val newData = DoubleArray(size) {
            data[it] - other.data[it]
        }
        return Tensor(dim, shape, newData)
    }

    operator fun times(other: Tensor): Tensor {
        return if (shape.last() != other.shape[0]) throw IllegalArgumentException("Tensor.times: Invalid tensor product.")
        else {
            val newDim = dim - 1 + other.dim - 1
            val newShape = IntArray(newDim) {
                when {
                    it < dim - 1 -> shape[it]
                    else -> other.shape[it - (dim - 1) + 1]
                }
            }
            val newSize = newShape.reduce { tot, num -> tot * num }
            val newData = DoubleArray(newSize) { dataIndex ->
                val newIndices = dataIndexToTensorIndices(newShape, dataIndex)
                var sum = 0.0
                for (sumIndex in 0 until shape.last()) {
                    val indices1 = IntArray(dim) {
                        when {
                            it < dim - 1 -> newIndices[it]
                            else -> sumIndex
                        }
                    }
                    val indices2 = IntArray(other.dim) {
                        when (it) {
                            0 -> sumIndex
                            else -> newIndices[dim - 1 - 1 + it]
                        }
                    }
                    sum += this[indices1] * other[indices2]
                }
                sum
            }
            Tensor(newDim, newShape, newData)
        }
    }

    operator fun times(other: Number): Tensor {
        val newData = DoubleArray(size) {
            data[it] * other.toDouble()
        }
        return Tensor(dim, shape, newData)
    }

    operator fun div(other: Number): Tensor {
        val newData = DoubleArray(size) {
            data[it] / other.toDouble()
        }
        return Tensor(dim, shape, newData)
    }

    private fun dataIndexToTensorIndices(newShape: IntArray, dataIndex: Int): IntArray {
        val retList = arrayListOf<Int>()
        newShape.foldRight(dataIndex) { it, acc ->
            retList.add(0, acc % it)
            acc / it
        }
        return retList.toIntArray()
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
                            (1 until shape[1]).fold(this[0L][0L].toStringVector()) { acc2, j ->
                                acc2.concatHorizontal(this[0L][j.toLong()].toStringVector())
                            }
                        ) { acc1, i ->
                            acc1.concatVertical(
                                (1 until shape[1]).fold(this[i.toLong()][0L].toStringVector()) { acc2, j ->
                                    acc2.concatHorizontal(this[i.toLong()][j.toLong()].toStringVector())
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
                        val bodyStringVector = (1 until shape[0]).fold(this[0L].toStringVector()) { acc, i ->
                            acc.concatHorizontal(this[i.toLong()].toStringVector())
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