import java.text.DecimalFormat
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin

open class Matrix(val rows: Int, val cols: Int, val data: DoubleArray = DoubleArray(rows * cols) { 0.0 }) {

    constructor(rows2: Int, cols2: Int, data2: LongArray) : this(
        rows2, cols2, DoubleArray(rows2 * cols2) { data2[it].toDouble() }
    )

    constructor(rows2: Int, cols2: Int, data2: FloatArray) : this(
        rows2, cols2, DoubleArray(rows2 * cols2) { data2[it].toDouble() }
    )

    constructor(rows2: Int, cols2: Int, data2: IntArray) : this(
        rows2, cols2, DoubleArray(rows2 * cols2) { data2[it].toDouble() }
    )

    constructor(rows2: Int, cols2: Int, lambda: (i: Int, j: Int) -> Number) : this(rows2, cols2,
        DoubleArray(rows2 * cols2) {
        val rowIndex = it / cols2
        val colIndex = it % cols2
        lambda(rowIndex, colIndex).toDouble()
    })

    operator fun get(rowIndex: Int, colIndex: Int) : Double {
        if (rowIndex < 0 || colIndex < 0 || rowIndex >= rows || colIndex >= cols) {
            throw IllegalArgumentException("Matrix.get: Index out of bound")
        } else {
            return data[rowIndex * cols + colIndex]
        }
    }

    operator fun set(rowIndex: Int, colIndex: Int, value: Double){
        if (rowIndex < 0 || colIndex < 0 || rowIndex >= rows || colIndex >= cols) {
            throw IllegalArgumentException("Matrix.set: Index out of bound")
        } else {
            data[rowIndex * cols + colIndex] = value
        }
    }

    open operator fun unaryPlus() = this

    open operator fun unaryMinus(): Matrix {
        return Matrix(rows, cols, DoubleArray(rows * cols) {- data[it]})
    }

    operator fun plus(other: Matrix): Matrix {
        return if (rows != other.rows || cols != other.cols) {
            throw IllegalArgumentException("Matrix.plus: Two matrices should have the same shape.")
        } else {
            val newData = DoubleArray(rows * cols) {
                data[it] + other.data[it]
            }
            Matrix(rows, cols, newData)
        }
    }

    operator fun minus(other: Matrix): Matrix {
        return if (rows != other.rows || cols != other.cols) {
            throw IllegalArgumentException("Matrix.minus: Two matrices should have the same shape.")
        } else {
            val newData = DoubleArray(rows * cols) {
                data[it] - other.data[it]
            }
            Matrix(rows, cols, newData)
        }
    }

    open operator fun times(other: Matrix): Matrix {
        return if (cols != other.rows) {
            throw IllegalArgumentException("Matrix.times: Illegal Matrix multiplication.")
        } else {
            val newData = DoubleArray(rows * other.cols) {
                val rowIndex = it / other.cols
                val colIndex = it % other.cols
                var sum = 0.0
                for (i in 0 until this.cols) {
                    sum += this[rowIndex, i] * other[i, colIndex]
                }
                sum
            }
            Matrix(rows, other.cols, newData)
        }
    }

    operator fun times(other: ColumnVector): ColumnVector {
        return if (cols != other.size) {
            throw IllegalArgumentException("Matrix.times: Illegal Matrix multiplication.")
        } else {
            val newData = DoubleArray(rows * 1) {
                var sum = 0.0
                for (i in 0 until this.cols) {
                    sum += this[it, i] * other[i]
                }
                sum
            }
            ColumnVector(rows, newData)
        }
    }

    open operator fun times(other: Number): Matrix {
        val newData = DoubleArray(rows * cols) {
            val rowIndex = it / cols
            val colIndex = it % cols
            other.toDouble() * this[rowIndex, colIndex]
        }
        return Matrix(rows, rows, newData)
    }

    open operator fun div(other: Number): Matrix {
        val newData = DoubleArray(rows * cols) {
            val rowIndex = it / cols
            val colIndex = it % cols
            this[rowIndex, colIndex] / other.toDouble()
        }
        return Matrix(rows, rows, newData)
    }

    operator fun plusAssign(other: Matrix) {
        if (rows != other.rows || cols != other.cols) {
            throw IllegalArgumentException("Matrix.plusAssign: Two matrices should have the same shape.")
        } else {
            for (i in 0 until rows * cols) {
                data[i] += other.data[i]
            }
        }
    }

    operator fun minusAssign(other: Matrix) {
        if (rows != other.rows || cols != other.cols) {
            throw IllegalArgumentException("Matrix.minusAssign: Two matrices should have the same shape.")
        } else {
            for (i in 0 until rows * cols) {
                data[i] -= other.data[i]
            }
        }
    }

    operator fun timesAssign(other: Number) {
        for (i in 0 until rows * cols) {
            data[i] *= other.toDouble()
        }
    }

    operator fun divAssign(other: Number) {
        for (i in 0 until rows * cols) {
            data[i] /= other.toDouble()
        }
    }

    open fun transpose(): Matrix {
        val newData = DoubleArray(rows * cols) {
            val transposedRowIndex = it / rows
            val transposedColIndex = it % rows
            this[transposedColIndex, transposedRowIndex]
        }
        return Matrix(cols, rows, newData)
    }

    fun frobeniusNormSquared(): Double {
        var frbNorm = 0.0
        data.forEach {
            frbNorm += it.pow(2)
        }
        return frbNorm
    }

    fun determinant(): Double { // using LU decomposition through recursion, O(n^3)
        if (rows != cols) throw IllegalArgumentException("Matrix.determinant: Only available for square matrices")
        when (rows) {
            1 -> return this[0, 0]
            2 -> return this[0, 0] * this[1, 1] - this[0, 1] * this[1, 0]
            else -> {
                var sign = 1
                var switchIndex = 0
                val firstColumn = DoubleArray(rows) { this[it, 0] }
                var a = 0.0

                for (elem in firstColumn) {
                    if (elem != 0.0) {
                        a = elem
                        break
                    } else {
                        sign *= -1
                        switchIndex += 1
                    }
                }
                return if (a == 0.0) 0.0 // 첫 번째 열이 모두 0이다.
                else {
                    val matP1A = switchRow(0, switchIndex)
                    val v = matP1A.getSubmatrix(1, rows, 0, 1)
                    val wT = matP1A.getSubmatrix(0, 1, 1, cols)
                    val c = 1 / a
                    val matAPrime = matP1A.getSubmatrix(1, rows, 1, cols)
                    sign * a * (matAPrime - (v * wT) * c).determinant()
                }
            }
        }
    }

    fun adjointMatrix() : Matrix {
        if (rows != cols) throw IllegalArgumentException("Matrix.adjointMatrix: Only available for square matrices")
        val newData = DoubleArray(rows * cols) {
            val rowIndex = it / cols
            val colIndex = it % cols
            val sign = (-1.0).pow(rowIndex + colIndex)
            val cofactorDet = this.cofactorMatrix(rowIndex, colIndex).determinant()
            sign * cofactorDet
        }
        return Matrix(rows, cols, newData).transpose()
    }

    fun inverseMatrix() : Matrix {
        if (rows != cols) throw IllegalArgumentException("Matrix.inverseMatrix: Only available for square matrices")
        val det = this.determinant()
        return if (det == 0.0) Matrix.identityMatrix(rows)
        else this.adjointMatrix() * (det.pow(-1))
    }

    open fun getSubmatrix(rowIndexStart: Int, rowIndexEnd: Int, colIndexStart: Int, colIndexEnd: Int): Matrix {
        return if (rowIndexStart < 0 || colIndexStart < 0 || rowIndexStart >= rowIndexEnd || colIndexStart >= colIndexEnd
            || rowIndexEnd > rows || colIndexEnd > cols) {
            throw IllegalArgumentException("Matrix.Submatrix: Index out of bound")
        } else {
            val newRows = rowIndexEnd - rowIndexStart
            val newCols = colIndexEnd - colIndexStart
            val newData = DoubleArray(newRows * newCols) {
                val newRowIndex = it / newCols
                val newColIndex = it % newCols
                this[rowIndexStart + newRowIndex, colIndexStart + newColIndex]
            }
            Matrix(newRows, newCols, newData)
        }
    }

    open fun setSubmatrix(rowIndexStart: Int, rowIndexEnd: Int, colIndexStart: Int, colIndexEnd: Int, other: Matrix) {
        val newRows = rowIndexEnd - rowIndexStart
        val newCols = colIndexEnd - colIndexStart
        if (rowIndexStart < 0 || colIndexStart < 0 || rowIndexStart >= rowIndexEnd || colIndexStart >= colIndexEnd
            || rowIndexEnd > rows || colIndexEnd > cols || newRows != other.rows || newCols != other.cols) {
            throw IllegalArgumentException("Matrix.Submatrix: Index out of bound")
        } else {
            other.data.forEachIndexed { index, element ->
                val otherRowIndex = index / other.cols
                val otherColIndex = index % other.cols
                this[rowIndexStart + otherRowIndex, colIndexStart + otherColIndex] = element
            }
        }
    }

    fun cofactorMatrix(rowIndex: Int, colIndex: Int) : Matrix {
        return if (rows < 2 || cols < 2 || rowIndex >= rows || colIndex >= cols) {
            throw IllegalArgumentException("Matrix.cofactorMatrix: Index out of bound")
        } else {
            val newData = DoubleArray((rows - 1) * (cols - 1)) {
                var cofactorRowIndex = it / (cols - 1)
                var cofactorColIndex = it % (cols - 1)
                if (cofactorRowIndex >= rowIndex) cofactorRowIndex += 1
                if (cofactorColIndex >= colIndex) cofactorColIndex += 1
                this[cofactorRowIndex, cofactorColIndex]
            }
            Matrix(rows - 1, cols - 1, newData)
        }
    }

    fun switchRow(rowIndex1: Int, rowIndex2: Int): Matrix {
        return if (rowIndex1 < 0 || rowIndex2 < 0 || rowIndex1 >= rows || rowIndex2 >= rows) {
            throw IllegalArgumentException("Matrix.switchRow: Index out of bound")
        } else if (rowIndex1 == rowIndex2) {
            this
        } else {
            val newData = DoubleArray(rows * cols) {
                val newRowIndex = it / cols
                val newColIndex = it % cols
                when (newRowIndex) {
                    rowIndex1 -> this[rowIndex2, newColIndex]
                    rowIndex2 -> this[rowIndex1, newColIndex]
                    else -> this[newRowIndex, newColIndex]
                }
            }
            Matrix(rows, cols, newData)
        }
    }

    fun concat(other: Matrix, dim: Int = 1) : Matrix {
        return when (dim) {
            0 -> {
                if (cols != other.cols) throw IllegalArgumentException("Matrix.concat: number of columns does not match")
                val newRows = rows + other.rows
                val newCols = cols
                val newData = DoubleArray(newRows * newCols) {
                    val newRowIndex = it / newCols
                    val newColIndex = it % newCols
                    if (newRowIndex < rows) {
                        this[newRowIndex, newColIndex]
                    } else {
                        other[newRowIndex - rows, newColIndex]
                    }
                }
                Matrix(newRows, newCols, newData)
            }
            1 -> {
                if (rows != other.rows) throw IllegalArgumentException("Matrix.concat: number of rows does not match")
                val newRows = rows
                val newCols = cols + other.cols
                val newData = DoubleArray(newRows * newCols) {
                    val newRowIndex = it / newCols
                    val newColIndex = it % newCols
                    if (newColIndex < cols) {
                        this[newRowIndex, newColIndex]
                    } else {
                        other[newRowIndex, newColIndex - cols]
                    }
                }
                Matrix(newRows, newCols, newData)
            }
            else -> throw IllegalArgumentException("Matrix.concat: dim must be 0 or 1")
        }
    }

    fun colVecNormSq(): RowVector {
        val newData = DoubleArray(1 * cols) {
            var norm = 0.0
            for (rowIndex in 0 until rows) {
                norm += this[rowIndex, it].pow(2)
            }
            norm
        }
        return RowVector(cols, newData)
    }

    fun rowVecNormSq(): ColumnVector {
        val newData = DoubleArray(rows * 1) {
            var norm = 0.0
            for (colIndex in 0 until cols) {
                norm += this[it, colIndex].pow(2)
            }
            norm
        }
        return ColumnVector(1, newData)
    }

    fun sum(): Double {
        var sum = 0.0
        data.forEach { sum += it }
        return sum
    }

    fun eltwiseMul(other: Matrix): Matrix {
        if (rows != other.rows || cols != other.cols)
            throw IllegalArgumentException("Matrix.eltwiseMul: Both operands must have the same shape")
        return Matrix(rows, cols, DoubleArray(rows * cols) {
            val rowIndex = it / cols
            val colIndex = it % cols
            this[rowIndex, colIndex] * other[rowIndex, colIndex]
        })
    }

    fun rowWiseMean(): ColumnVector {
        return ColumnVector(rows, DoubleArray(rows) {
            var rowSum = 0.0
            for (colIndex in 0 until cols) {
                rowSum += this[it, colIndex]
            }
            rowSum / cols
        })
    }

    fun columnWiseMean(): RowVector {
        return RowVector(cols, DoubleArray(cols) {
            var colSum = 0.0
            for (rowIndex in 0 until rows) {
                colSum += this[rowIndex, it]
            }
            colSum / rows
        })
    }

    open fun map(lambda: (e: Double) -> Number): Matrix {
        return Matrix(rows, cols, DoubleArray(rows * cols) {
            val rowIndex = it / cols
            val colIndex = it % cols
            lambda(this[rowIndex, colIndex]).toDouble()
        })
    }

    fun reshape(newRows: Int, newCols: Int): Matrix {
        return when {
            newRows >= 0 && newCols >= 0 -> {
                if (newRows * newCols != rows * cols) throw IllegalArgumentException("Matrix.reshape: Invalid shape")
                else Matrix(newRows, newCols, data)
            }
            newRows == -1 && newCols > 0 -> {
                if ((rows * cols) % newCols != 0) throw IllegalArgumentException("Matrix.reshape: Invalid shape")
                else Matrix((rows * cols) / newCols, newCols, data)
            }
            newRows > 0 && newCols == -1 -> {
                if ((rows * cols) % newRows != 0) throw IllegalArgumentException("Matrix.reshape: Invalid shape")
                else Matrix(newRows, (rows * cols) / newRows, data)
            }
            else -> throw IllegalArgumentException("Matrix.reshape: Invalid shape")
        }
    }

    override fun toString(): String {
        var result = ""
        for (i in 0 until rows) {
            result += "[ "
            for (j in 0 until cols) {
                val value = this[i, j]
                result += when {
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
            result += " ]\n"
        }
        return result
    }

    companion object {
        fun identityMatrix(dim: Int): Matrix {
            val newData = DoubleArray(dim * dim) {
                val rowIndex = it / dim
                val colIndex = it % dim
                if (rowIndex == colIndex) 1.0 else 0.0
            }
            return Matrix(dim, dim, newData)
        }

        fun zeros(n: Int, m: Int): Matrix {
            return if (n < 1 || m < 1) throw IllegalArgumentException("Matrix.zeros: n, m must be positive integers")
            else Matrix(n, m, DoubleArray(n * m) { 0.0 })
        }

        fun ones(n: Int, m: Int): Matrix {
            return if (n < 1 || m < 1) throw IllegalArgumentException("Matrix.ones: n, m must be positive integers")
            else Matrix(n, m, DoubleArray(n * m) { 1.0 })
        }

        fun rotationMatrix2d(theta: Double): Matrix {
            return Matrix(2, 2, doubleArrayOf(
                cos(theta), -sin(theta),
                sin(theta), cos(theta)
            ))
        }

        fun rotationMatrix3dX(theta: Double): Matrix {
            return Matrix(3, 3, doubleArrayOf(
                1.0,        0.0,            0.0,
                0.0,        cos(theta),     -sin(theta),
                0.0,        sin(theta),     cos(theta)
            ))
        }

        fun rotationMatrix3dY(theta: Double): Matrix {
            return Matrix(3, 3, doubleArrayOf(
                cos(theta),     0.0,        sin(theta),
                0.0,            1.0,        0.0,
                -sin(theta),    0.0,        cos(theta)
            ))
        }

        fun rotationMatrix3dZ(theta: Double): Matrix {
            return Matrix(3, 3, doubleArrayOf(
                cos(theta),     -sin(theta),    0.0,
                sin(theta),     cos(theta),     0.0,
                0.0,            0.0,            1.0
            ))
        }

        fun eulerRotationMatrix3d(alpha: Double, beta: Double, gamma: Double): Matrix {
            return rotationMatrix3dZ(alpha) * rotationMatrix3dX(beta) * rotationMatrix3dZ(gamma)
        }
    }
}

operator fun Number.times(other: Matrix): Matrix {
    val newData = DoubleArray(other.rows * other.cols) {
        val rowIndex = it / other.cols
        val colIndex = it % other.cols
        this.toDouble() * other[rowIndex, colIndex]
    }
    return Matrix(other.rows, other.cols, newData)
}