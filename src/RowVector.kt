class RowVector(val size: Int, data: DoubleArray = DoubleArray(size){0.0}): Matrix(1, size, data) {

    constructor(size: Int, data: LongArray) : this(size, DoubleArray(size) { data[it].toDouble() })

    constructor(size: Int, data: FloatArray) : this(size, DoubleArray(size) { data[it].toDouble() })

    constructor(size: Int, data: IntArray) : this(size, DoubleArray(size) { data[it].toDouble() })

    constructor (size: Int, lambda: (i: Int) -> Number) : this(size, DoubleArray(size) { lambda(it).toDouble() })

    operator fun get(index: Int): Double {
        if (index < 0 || index >= size) {
            throw IllegalArgumentException("RowVector.get: Index out of bound")
        } else {
            return data[index]
        }
    }

    operator fun set(index: Int, value: Double) {
        if (index < 0 || index >= size) {
            throw IllegalArgumentException("RowVector.get: Index out of bound")
        } else {
            data[index] = value
        }
    }

    override operator fun unaryPlus() = this

    override operator fun unaryMinus(): RowVector {
        return RowVector(size, DoubleArray(size) {- data[it]})
    }

    operator fun plus(other: RowVector): RowVector {
        return if (size != other.size) {
            throw IllegalArgumentException("RowVector.plus: Two vectors should have the same size.")
        } else {
            val newData = DoubleArray(size) {
                data[it] + other.data[it]
            }
            RowVector(size, newData)
        }
    }

    operator fun minus(other: RowVector): RowVector {
        return if (size != other.size) {
            throw IllegalArgumentException("RowVector.minus: Two vectors should have the same size.")
        } else {
            val newData = DoubleArray(size) {
                data[it] - other.data[it]
            }
            RowVector(size, newData)
        }
    }

    override operator fun times(other: Matrix): RowVector {
        return if (size != other.rows) {
            throw IllegalArgumentException("RowVector.times: Illegal Matrix multiplication.")
        } else {
            val newData = DoubleArray(other.cols) {
                var sum = 0.0
                for (i in 0 until size) {
                    sum += this[i] * other[i, it]
                }
                sum
            }
            RowVector(other.cols, newData)
        }
    }

    override operator fun times(other: Number): RowVector {
        val newData = DoubleArray(size) {
            other.toDouble() * this[it]
        }
        return RowVector(size, newData)
    }

    override operator fun div(other: Number): RowVector {
        val newData = DoubleArray(size) {
            this[it] / other.toDouble()
        }
        return RowVector(size, newData)
    }

    operator fun plusAssign(other: RowVector) {
        if (size != other.size) {
            throw IllegalArgumentException("RowVector.plusAssign: Two vectors should have the same size.")
        } else {
            for (i in 0 until size) {
                data[i] += other.data[i]
            }
        }
    }

    operator fun minusAssign(other: RowVector) {
        if (size != other.size) {
            throw IllegalArgumentException("RowVector.plusAssign: Two vectors should have the same size.")
        } else {
            for (i in 0 until size) {
                data[i] -= other.data[i]
            }
        }
    }

    override fun transpose(): ColumnVector {
        return ColumnVector(size, data)
    }

    fun getSubvector(indexStart: Int, indexEnd: Int): RowVector {
        return if (indexStart < 0 || indexStart >= indexEnd || indexEnd > size) {
            throw IllegalArgumentException("RowVector.Subvector: Index out of bound")
        } else {
            val newSize = indexEnd - indexStart
            val newData = DoubleArray(newSize) {
                this[indexStart + it]
            }
            RowVector(newSize, newData)
        }
    }

    fun setSubvector(indexStart: Int, indexEnd: Int, other: RowVector) {
        val newSize = indexEnd - indexStart
        if (indexStart < 0 || indexStart >= indexEnd || indexEnd > size || newSize != other.size) {
            throw IllegalArgumentException("RowVector.Subvector: Index out of bound")
        } else {
            other.data.forEachIndexed { index, element ->
                this[indexStart + index] = element
            }
        }
    }

    fun eltwiseMul(other: RowVector): RowVector {
        if (size != other.size)
            throw IllegalArgumentException("RowVector.eltwiseMul: Both operands must have the same size")
        return RowVector(size, DoubleArray(size) { this[it] * other[it] })
    }

    fun dotProduct(other: RowVector): Double {
        if (size != other.size)
            throw IllegalArgumentException("RowVector.dotProduct: Both operands must have the same size")
        var sum = 0.0
        for (i in 0 until size) {
            sum += this[i] * other[i]
        }
        return sum
    }

    fun dotProduct(other: ColumnVector): Double {
        if (size != other.size)
            throw IllegalArgumentException("RowVector.dotProduct: Both operands must have the same size")
        var sum = 0.0
        for (i in 0 until size) {
            sum += this[i] * other[i]
        }
        return sum
    }

    fun crossProduct(other: RowVector): RowVector {
        if (size != 3 || other.size != 3)
            throw IllegalArgumentException("ColumnVector.dotProduct: Both operands must be 3 dimensional vectors")
        else {
            return RowVector(size, doubleArrayOf(
                this[1] * other[2] - this[2] * other[1],
                this[2] * other[0] - this[0] * other[2],
                this[0] * other[1] - this[1] * other[0]
            ))
        }
    }

    fun replicate(length: Int): Matrix {
        if (length < 1) throw IllegalArgumentException("RowVector.replicate: length must be greater than 0.")
        return Matrix(length, size, DoubleArray(length * size) {
            val colIndex = it % size
            this[colIndex]
        })
    }

    override fun map(lambda: (e: Double) -> Number): RowVector {
        return RowVector(size, DoubleArray(size) {
            lambda(this[it]).toDouble()
        })
    }
}

operator fun Number.times(other: RowVector): RowVector {
    val newData = DoubleArray(other.size) {
        this.toDouble() * other[it]
    }
    return RowVector(other.size, newData)
}