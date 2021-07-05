package utils

import realTensor.ColumnVector
import realTensor.Matrix
import realTensor.RowVector
import realTensor.Tensor


val Number.i: ComplexDouble
    get() = ComplexDouble(0, toDouble())

val Number.r: ComplexDouble
    get() = ComplexDouble(toDouble(), 0)


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

operator fun Number.times(other: Tensor): Tensor {
    val newData = DoubleArray(other.size) { other.data[it] * this.toDouble() }
    return Tensor(other.shape, newData)
}

operator fun Number.times(other: Matrix): Matrix {
    val newData = DoubleArray(other.rows * other.cols) {
        val rowIndex = it / other.cols
        val colIndex = it % other.cols
        this.toDouble() * other[rowIndex, colIndex]
    }
    return Matrix(other.rows, other.cols, newData)
}

operator fun Number.times(other: RowVector): RowVector {
    val newData = DoubleArray(other.length) {
        this.toDouble() * other[it]
    }
    return RowVector(other.length, newData)
}

operator fun Number.times(other: ColumnVector): ColumnVector {
    val newData = DoubleArray(other.length) {
        this.toDouble() * other[it]
    }
    return ColumnVector(other.length, newData)
}

operator fun Number.plus(other: ComplexDouble) = other + this

operator fun Number.minus(other: ComplexDouble) = -other + this

operator fun Number.times(other: ComplexDouble) = other * this

operator fun Number.div(other: ComplexDouble) = this.r / other

fun Number.toComplexDouble() = this.r
