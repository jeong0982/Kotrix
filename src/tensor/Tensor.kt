package tensor

import matrix.Matrix
import vector.row.RowVector
import utils.R
import utils.visualization.StringVector

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

    open fun map(lambda: (e: Double) -> Number): Tensor {
        return Tensor(shape, DoubleArray(size) {
            lambda(data[it]).toDouble()
        })
    }

    open fun toComplex(): ComplexTensor {
        return ComplexTensor(shape, Array(size) { data[it].R })
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

    override fun toString(): String {
        return StringVector.build(this).toString() + "\n"
    }

    companion object {
        fun stack(tensors: Iterable<Tensor>): Tensor {
            val init = tensors.elementAt(0)
            return tensors.fold(init) { acc, tensor -> acc.stackSuppl(tensor) }
        }
    }
}
