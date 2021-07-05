open class Tensor(val shape: IntArray, val data: DoubleArray =
    DoubleArray(shape.reduce {
            total, num ->
            if (num <= 0) throw IllegalArgumentException("Tensor.init: Invalid shape")
            else total * num
        }
    )
) {
    val size: Int = data.size
    val dim: Int = shape.size

    init {
        if (dim < 0) throw IllegalArgumentException("Tensor.init: dimension must be a non-negative integer")
        if (shape.size != dim) throw IllegalArgumentException("Tensor.init: shape.size != dim")
        if (size != calculateSize(shape)) throw IllegalArgumentException("Tensor.init: Invalid data length")
    }

    constructor(shape: IntArray, data: LongArray) :
            this(shape, DoubleArray(shape.reduce {tot, num -> tot * num}) { data[it].toDouble() })

    constructor(shape: IntArray, data: FloatArray) :
            this(shape, DoubleArray(shape.reduce {tot, num -> tot * num}) { data[it].toDouble() })

    constructor(shape: IntArray, data: IntArray) :
            this(shape, DoubleArray(shape.reduce {tot, num -> tot * num}) { data[it].toDouble() })

    operator fun get(indices: IntArray): Double {
        return if (indices.size != dim) throw IllegalArgumentException("Tensor.get: Too many indices")
        else data[tensorIndicesToDataIndex(shape, indices)]
    }

    operator fun get(indexLong: Long): Tensor {
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
                Tensor(newShape, newData)
            }
        }
    }

    operator fun set(indices: IntArray, value: Number) {
        indices.forEachIndexed { index, it ->
            if (it < 0 || it >= shape[index]) throw IllegalArgumentException("Tensor.set: Index out of bound")
        }
        data[tensorIndicesToDataIndex(shape, indices)] = value.toDouble()
    }

    open operator fun unaryPlus() = this

    open operator fun unaryMinus(): Tensor {
        return Tensor(shape, DoubleArray(size) {- data[it]})
    }

    operator fun plus(other: Tensor): Tensor {
        shape.forEachIndexed { index, it ->
            if (it != other.shape[index]) throw IllegalArgumentException("Tensor.plus: Two tensors should have the same shape.")
        }
        val newData = DoubleArray(size) {
            data[it] + other.data[it]
        }
        return Tensor(shape, newData)
    }

    operator fun minus(other: Tensor): Tensor {
        shape.forEachIndexed { index, it ->
            if (it != other.shape[index]) throw IllegalArgumentException("Tensor.minus: Two tensors should have the same shape.")
        }
        val newData = DoubleArray(size) {
            data[it] - other.data[it]
        }
        return Tensor(shape, newData)
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
            Tensor(newShape, newData)
        }
    }

    open operator fun times(other: Number): Tensor {
        val newData = DoubleArray(size) {
            data[it] * other.toDouble()
        }
        return Tensor(shape, newData)
    }

    open operator fun div(other: Number): Tensor {
        val newData = DoubleArray(size) {
            data[it] / other.toDouble()
        }
        return Tensor(shape, newData)
    }

    operator fun plusAssign(other: Tensor) {
        shape.forEachIndexed { index, it ->
            if (it != other.shape[index]) throw IllegalArgumentException("Tensor.plus: Two tensors should have the same shape.")
        }
        for(i in 0 until size) {
            data[i] += other.data[i]
        }
    }

    operator fun minusAssign(other: Tensor) {
        shape.forEachIndexed { index, it ->
            if (it != other.shape[index]) throw IllegalArgumentException("Tensor.plus: Two tensors should have the same shape.")
        }
        for(i in 0 until size) {
            data[i] -= other.data[i]
        }
    }

    operator fun timesAssign(other: Number) {
        for (i in 0 until size) {
            data[i] *= other.toDouble()
        }
    }

    operator fun divAssign(other: Number) {
        for (i in 0 until size) {
            data[i] /= other.toDouble()
        }
    }

    fun toMatrix(): Matrix {
        return when (dim) {
            1 -> {
                Matrix(1, shape[0], data)
            }
            2 -> {
                Matrix(shape[0], shape[1], data)
            }
            else -> throw IllegalStateException("Tensor.toMatrix: must be a 2 dimensional tensor, not $dim.")
        }
    }

    fun reshape(newShape: IntArray): Tensor {
        var negOneIndex = -1
        var negOneCount = 0
        var acc = 1
        newShape.forEachIndexed { index, it ->
            when {
                it == -1 -> {
                    negOneCount++
                    negOneIndex = index
                }
                it > 0 -> {
                    acc *= it
                    if (size % acc != 0) throw IllegalArgumentException("Tensor.reshape: invalid shape input")
                }
                else -> throw IllegalArgumentException("Tensor.reshape: invalid shape input")
            }
        }
        if (negOneCount > 0) {
            newShape[negOneIndex] = size / acc
        }
        return Tensor(newShape, data)
    }

    fun flatten(): RowVector {
        return this.reshape(intArrayOf(-1)).toMatrix().toRowVector()
    }

    fun concat(other: Tensor, concatDim: Int): Tensor {
        if (dim != other.dim || concatDim >= dim) throw IllegalArgumentException("Tensor.concat: invalid dimension")
        else {
            shape.forEachIndexed { index, it ->
                if (index != concatDim && it != other.shape[index])
                    throw IllegalArgumentException("Tensor.concat: two tensors must have same shape except for concat dimension")
            }
            val newShape = IntArray(shape.size) {
                when (it) {
                    concatDim -> shape[it] + other.shape[it]
                    else -> shape[it]
                }
            }
            val newSize = calculateSize(newShape)
            return Tensor(newShape, DoubleArray(newSize) {
                val newIndices = dataIndexToTensorIndices(newShape, it)
                when {
                    newIndices[concatDim] < shape[concatDim] ->
                        this[newIndices]
                    else -> {
                        newIndices[concatDim] -= shape[concatDim]
                        other[newIndices]
                    }
                }
            })
        }
    }

    private fun dataIndexToTensorIndices(newShape: IntArray, dataIndex: Int): IntArray {
        val retList = arrayListOf<Int>()
        newShape.foldRight(dataIndex) { it, acc ->
            retList.add(0, acc % it)
            acc / it
        }
        return retList.toIntArray()
    }

    private fun tensorIndicesToDataIndex(newShape: IntArray, tensorIndices: IntArray): Int {
        return tensorIndices.reduceIndexed { index, acc, tensorIndex ->
            if (tensorIndex >= newShape[index]) throw IllegalArgumentException("Tensor.tensorIndicesToDataIndex: Index out of bound")
            (acc * newShape[index]) + tensorIndex
        }
    }

    private fun calculateSize(newShape: IntArray): Int {
        return newShape.reduce {
                total, num ->
            if (num <= 0) throw IllegalArgumentException("Tensor.init: Invalid shape")
            else total * num
        }
    }

    private fun stackSuppl(other: Tensor): Tensor {
        return when (dim-other.dim) {
            0 -> {
                other.shape.forEachIndexed {index, it ->
                    if (this.shape[index] != it) throw IllegalArgumentException("Tensor.stack: Cannot stack tensors with different shape")
                }
                Tensor(intArrayOf(2) + other.shape, data + other.data)
            }
            1 -> {
                other.shape.forEachIndexed { index, it ->
                    if (this.shape[index + 1] != it) throw IllegalArgumentException("Tensor.stack: Cannot stack tensors with different shape")
                }
                Tensor(intArrayOf(shape[0]+1) + other.shape, data + other.data)
            }
            else -> throw IllegalArgumentException("Tensor.stack: Cannot stack tensors with different shape")
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
                    data += value.toFormattedString()
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
                        data += value.toFormattedString()
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
        return toStringVector().toString() + "\n"
    }

    companion object {
        fun stack(tensors: Iterable<Tensor>): Tensor {
            val init = tensors.elementAt(0)
            return tensors.fold(init) { acc, tensor -> acc.stackSuppl(tensor) }
        }
    }
}

operator fun Number.times(other: Tensor): Tensor {
    val newData = DoubleArray(other.size) { other.data[it] * this.toDouble() }
    return Tensor(other.shape, newData)
}

fun Number.toFormattedString(): String {
    val thisToDouble = this.toDouble()
    return when {
        thisToDouble >= 1000 -> " %.0f ".format(thisToDouble)
        thisToDouble >= 100 -> " %.0f. ".format(thisToDouble)
        thisToDouble >= 10 -> " %.1f ".format(thisToDouble)
        thisToDouble == -0.0 -> " 0.00 "
        thisToDouble >= 0 -> " %.2f ".format(thisToDouble)
        thisToDouble > -10 -> "%.2f ".format(thisToDouble)
        thisToDouble > -100 -> "%.1f ".format(thisToDouble)
        thisToDouble > -1000 -> "%.0f. ".format(thisToDouble)
        else -> "%.0f ".format(thisToDouble)
    }
}