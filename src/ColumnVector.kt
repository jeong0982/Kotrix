class ColumnVector(val size: Int, data: DoubleArray = DoubleArray(size){0.0}) : Matrix(size, 1, data) {

    constructor(size: Int, data: LongArray) : this(size, DoubleArray(size) { data[it].toDouble() })

    constructor(size: Int, data: FloatArray) : this(size, DoubleArray(size) { data[it].toDouble() })

    constructor(size: Int, data: IntArray) : this(size, DoubleArray(size) { data[it].toDouble() })

    constructor (size: Int, lambda: (i: Int) -> Number) : this(size, DoubleArray(size) { lambda(it).toDouble() })

    operator fun get(index: Int): Double {
        if (index < 0 || index >= size) {
            throw IllegalArgumentException("ColumnVector.get: Index out of bound")
        } else {
            return data[index]
        }
    }

    operator fun set(index: Int, value: Double) {
        if (index < 0 || index >= size) {
            throw IllegalArgumentException("ColumnVector.get: Index out of bound")
        } else {
            data[index] = value
        }
    }

    override operator fun unaryPlus() = this

    override operator fun unaryMinus(): ColumnVector {
        return ColumnVector(size, DoubleArray(size) {- data[it]})
    }

    operator fun plus(other: ColumnVector): ColumnVector {
        return if (size != other.size) {
            throw IllegalArgumentException("ColumnVector.plus: Two vectors should have the same size.")
        } else {
            val newData = DoubleArray(size) {
                data[it] + other.data[it]
            }
            ColumnVector(size, newData)
        }
    }

    operator fun minus(other: ColumnVector): ColumnVector {
        return if (size != other.size) {
            throw IllegalArgumentException("ColumnVector.minus: Two vectors should have the same size.")
        } else {
            val newData = DoubleArray(size) {
                data[it] - other.data[it]
            }
            ColumnVector(size, newData)
        }
    }

    override operator fun times(other: Number): ColumnVector {
        val newData = DoubleArray(size) {
            other.toDouble() * this[it]
        }
        return ColumnVector(size, newData)
    }

    override operator fun div(other: Number): ColumnVector {
        val newData = DoubleArray(size) {
            this[it] / other.toDouble()
        }
        return ColumnVector(size, newData)
    }

    override fun transpose(): RowVector {
        return RowVector(size, data)
    }

    fun getSubvector(indexStart: Int, indexEnd: Int): ColumnVector {
        return if (indexStart < 0 || indexStart >= indexEnd || indexEnd > size) {
            throw IllegalArgumentException("ColumnVector.Subvector: Index out of bound")
        } else {
            val newSize = indexEnd - indexStart
            val newData = DoubleArray(newSize) {
                this[indexStart + it]
            }
            ColumnVector(newSize, newData)
        }
    }

    fun setSubvector(indexStart: Int, indexEnd: Int, other: ColumnVector) {
        val newSize = indexEnd - indexStart
        if (indexStart < 0 || indexStart >= indexEnd || indexEnd > size || newSize != other.size) {
            throw IllegalArgumentException("ColumnVector.Subvector: Index out of bound")
        } else {
            other.data.forEachIndexed { index, element ->
                this[indexStart + index] = element
            }
        }
    }

    fun eltwiseMul(other: ColumnVector): ColumnVector {
        if (size != other.size)
            throw IllegalArgumentException("ColumnVector.eltwiseMul: Both operands must have the same size")
        return ColumnVector(size, DoubleArray(size) { this[it] * other[it] })
    }

    fun dotProduct(other: ColumnVector): Double {
        if (size != other.size)
            throw IllegalArgumentException("ColumnVector.dotProduct: Both operands must have the same size")
        var sum = 0.0
        for (i in 0 until size) {
            sum += this[i] * other[i]
        }
        return sum
    }

    fun dotProduct(other: RowVector): Double {
        if (size != other.size)
            throw IllegalArgumentException("ColumnVector.dotProduct: Both operands must have the same size")
        var sum = 0.0
        for (i in 0 until size) {
            sum += this[i] * other[i]
        }
        return sum
    }

    fun crossProduct(other: ColumnVector): ColumnVector {
        if (size != 3 || other.size != 3)
            throw IllegalArgumentException("ColumnVector.dotProduct: Both operands must be 3 dimensional vectors")
        else {
            return ColumnVector(size, doubleArrayOf(
                this[1] * other[2] - this[2] * other[1],
                this[2] * other[0] - this[0] * other[2],
                this[0] * other[1] - this[1] * other[0]
            ))
        }
    }

    fun replicate(length: Int): Matrix {
        if (length < 1) throw IllegalArgumentException("RowVector.replicate: length must be greater than 0.")
        return Matrix(size, length, DoubleArray(size * length) {
            val rowIndex = it / length
            this[rowIndex]
        })
    }

    override fun map(lambda: (e: Double) -> Number): ColumnVector {
        return ColumnVector(size, DoubleArray(size) {
            lambda(this[it]).toDouble()
        })
    }
}

operator fun Number.times(other: ColumnVector): ColumnVector {
    val newData = DoubleArray(other.size) {
        this.toDouble() * other[it]
    }
    return ColumnVector(other.size, newData)
}
