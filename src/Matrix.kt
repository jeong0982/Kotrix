import kotlin.math.pow
import kotlin.math.round

open class Matrix(val rows: Int, val cols: Int, val data: DoubleArray = DoubleArray(rows * cols) { 0.0 }) {
    companion object {
        fun identityMatrix(dim: Int) : Matrix {
            val newData = DoubleArray(dim * dim) {
                val rowIndex = it / dim
                val colIndex = it % dim
                if (rowIndex == colIndex) 1.0 else 0.0
            }
            return Matrix(dim, dim, newData)
        }
    }

    constructor(rows2: Int, cols2: Int, data2: LongArray) : this(
        rows2, cols2, DoubleArray(rows2 * cols2) { data2[it].toDouble() }
    )

    operator fun plus(other: Matrix) : Matrix {
        return if (rows != other.rows || cols != other.cols) {
            throw IllegalArgumentException("Matrix.plus: Two matrices should have the same shape.")
        } else {
            val newData = DoubleArray(rows * cols) {
                data[it] + other.data[it]
            }
            Matrix(rows, cols, newData)
        }
    }

    operator fun minus(other: Matrix) : Matrix {
        return if (rows != other.rows || cols != other.cols) {
            throw IllegalArgumentException("Matrix.minus: Two matrices should have the same shape.")
        } else {
            val newData = DoubleArray(rows * cols) {
                data[it] - other.data[it]
            }
            Matrix(rows, cols, newData)
        }
    }

    operator fun times(other: Matrix) : Matrix {
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

    operator fun times(other: Double) : Matrix {
        val newData = DoubleArray(rows * cols) {
            val rowIndex = it / cols
            val colIndex = it % cols
            other * this[rowIndex, colIndex]
        }
        return Matrix(rows, rows, newData)
    }

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

    fun transpose() : Matrix {
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

    fun getSubmatrix(rowIndex1: Int, rowIndex2: Int, colIndex1: Int, colIndex2: Int): Matrix {
        return if (rowIndex1 < 0 || colIndex1 < 0 || rowIndex1 >= rowIndex2 || colIndex1 >= colIndex2
            || rowIndex2 > rows || colIndex2 > cols) {
            throw IllegalArgumentException("Matrix.Submatrix: Index out of bound")
        } else {
            val newRows = rowIndex2 - rowIndex1
            val newCols = colIndex2 - colIndex1
            val newData = DoubleArray(newRows * newCols) {
                val newRowIndex = it / newCols
                val newColIndex = it % newCols
                this[rowIndex1 + newRowIndex, colIndex1 + newColIndex]
            }
            Matrix(newRows, newCols, newData)
        }
    }

    fun setSubmatrix(rowIndex1: Int, rowIndex2: Int, colIndex1: Int, colIndex2: Int, other: Matrix) {
        val newRows = rowIndex2 - rowIndex1
        val newCols = colIndex2 - colIndex1
        if (rowIndex1 < 0 || colIndex1 < 0 || rowIndex1 >= rowIndex2 || colIndex1 >= colIndex2
            || rowIndex2 > rows || colIndex2 > cols || newRows != other.rows || newCols != other.cols) {
            throw IllegalArgumentException("Matrix.Submatrix: Index out of bound")
        } else {
            other.data.forEachIndexed { index, element ->
                val otherRowIndex = index / other.cols
                val otherColIndex = index % other.cols
                this[rowIndex1 + otherRowIndex, colIndex1 + otherColIndex] = element
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

    fun colVecNormSq(): Matrix {
        val newData = DoubleArray(1 * cols) {
            var norm = 0.0
            for (rowIndex in 0 until rows) {
                norm += this[rowIndex, it].pow(2)
            }
            norm
        }
        return Matrix(1, cols, newData)
    }

    fun rowVecNormSq(): Matrix {
        val newData = DoubleArray(rows * 1) {
            var norm = 0.0
            for (colIndex in 0 until cols) {
                norm += this[it, colIndex].pow(2)
            }
            norm
        }
        return Matrix(rows, 1, newData)
    }

    fun sum(): Double {
        var sum = 0.0
        data.forEach { sum += it }
        return sum
    }

    override fun toString(): String {
        var result = ""
        for (i in 0 until rows) {
            result += "[ "
            for (j in 0 until cols) {
                result += (round((this[i, j] * 10)) / 10.0).toString() + " "
            }
            result += "]\n"
        }
        return result
    }
}

class ColumnVector(val size: Int, data: DoubleArray = DoubleArray(size){0.0}) : Matrix(size, 1, data) {
    constructor(size: Int, data: LongArray) : this(size, DoubleArray(size) { data[it].toDouble() })
}

class RowVector(val size: Int, data: DoubleArray = DoubleArray(size){0.0}): Matrix(1, size, data) {
    constructor(size: Int, data: LongArray) : this(size, DoubleArray(size) { data[it].toDouble() })
}