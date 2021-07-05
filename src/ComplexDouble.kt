import kotlin.math.*

class ComplexDouble (real: Number, imaginary: Number) {
    val re = real.toDouble()
    val im = imaginary.toDouble()
    val abs by lazy { sqrt(re.pow(2) + im.pow(2)) }
    val arg by lazy { atan2(im, re) }

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

    override fun toString(): String {
        return if (im >= 0) "${re.toFormattedString()}+${im.toFormattedString()}i"
        else "${re.toFormattedString()}-${(-im).toFormattedString()}i"
    }

    companion object {
        fun polarForm(r: Double, theta: Double) = ComplexDouble(r*cos(theta), r*sin(theta))
    }
}

val Number.i: ComplexDouble
    get() = ComplexDouble(0, toDouble())

val Number.r: ComplexDouble
    get() = ComplexDouble(toDouble(), 0)

operator fun Number.plus(other: ComplexDouble) = other + this

operator fun Number.minus(other: ComplexDouble) = -other + this

operator fun Number.times(other: ComplexDouble) = other * this

operator fun Number.div(other: ComplexDouble) = this.r / other