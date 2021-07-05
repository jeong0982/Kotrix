package utils

import kotlin.math.*

class ComplexDouble (real: Number, imaginary: Number) {
    var re = real.toDouble()
    var im = imaginary.toDouble()

    operator fun unaryPlus() = this

    operator fun unaryMinus() = ComplexDouble(-re, -im)

    operator fun plus(other: ComplexDouble) = ComplexDouble(re + other.re, im + other.im)

    operator fun plus(other: Number) = ComplexDouble(re + other.toDouble(), im)

    operator fun minus(other: ComplexDouble) = ComplexDouble(re - other.re, im - other.im)

    operator fun minus(other: Number) = ComplexDouble(re - other.toDouble(), im)

    operator fun times(other: ComplexDouble) = ComplexDouble(re*other.re - im*other.im, re*other.im + im*other.re)

    operator fun times(other: Number) = ComplexDouble(re*other.toDouble(), im*other.toDouble())

    operator fun div(other: ComplexDouble) = this * other.conj() / (other.re.pow(2) + other.im.pow(2))

    operator fun div(other: Number) = ComplexDouble(re/other.toDouble(), im/other.toDouble())

    fun conj() = ComplexDouble(re, -im)

    fun abs() = sqrt(re.pow(2) + im.pow(2))

    fun arg() = atan2(im, re)

    override fun toString(): String {
        return if (im >= 0) " ${re.toFormattedString()}+${im.toFormattedString()}i "
        else " ${re.toFormattedString()}-${(-im).toFormattedString()}i "
    }

    companion object {
        fun polarForm(r: Double, theta: Double) = ComplexDouble(r*cos(theta), r*sin(theta))
    }
}
