import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Test
fun main(args: Array<String>) {
    val mat1 = Matrix(4,4, doubleArrayOf(
        1.0, 3.0, 5.0, 9.0,
        1.0, 3.0, 1.0, 7.0,
        4.0, 3.0, 9.0, 7.0,
        5.0, 2.0, 0.0, 9.0
    ))
    val mat2 = Matrix(4,4, doubleArrayOf(
        1.0, 2.0, 3.0, 4.0,
        2.0, 3.0, 4.0, 5.0,
        3.0, 4.0, 5.0, 6.0,
        4.0, 5.0, 6.0, 7.0
    ))
//    val r = Random()
//    val mat3 = Matrix(100, 100, DoubleArray(100*100) { r.nextDouble() })

//    val mat4 = Matrix(2, 2, longArrayOf(0, 1, 2, 3))
//    val colVec = ColumnVector(3, doubleArrayOf(2.0, 3.0, 4.0))
//    val rowVec = RowVector(2, doubleArrayOf(1.0, 2.0))

//    println(mat1)
//    println(mat1 + mat2)
//    println(mat1 - mat2)
//    println(mat1 * mat2)
//    println(mat1 * 2.0)
//    println(mat2[1, 1])
//    mat2[1, 1] = 8.0
//    println(mat2)

//    println(Matrix.identityMatrix(3))
//    println(mat1.transpose())
//    println(mat1.frobeniusNormSquared())
//    println(mat1.determinant())
//    println(mat1.adjointMatrix())
//    println(mat1.inverseMatrix() * mat1)
//    val mat1Sub = mat1.getSubmatrix(1, 3, 1, 3)
//    println(mat1Sub)
//    mat2.setSubmatrix(1, 3, 1, 3, mat1Sub)
//    println(mat2)
//    println(mat2.cofactorMatrix(1, 1))
//    println(mat2.switchRow(1, 3))
//    println(mat1.concat(mat2, 0))
//    println(mat2.colVecNormSq())
//    println(mat2.rowVecNormSq())
//    println(mat2.sum())

//    println(mat3.determinant())
//    println(colVec)
//    println(rowVec)
//    println(colVec.dotProduct(colVec))
//    println(colVec.eltwiseMul(colVec))
//    println(mat1.eltwiseMul(mat1))
//    colVec.setSubvector(0,2,rowVec.transpose())
//    println(colVec)

//    println(colVec.replicate(3))
//    println(rowVec.replicate(5))
//    println(colVec * colVec.transpose())

//    val colVec = ColumnVector(8, doubleArrayOf(1.0, 10.0, 100.0, 1000.0, -1.0, -10.0, -100.0, -1000.0))
//    println(8*colVec)
//    println(colVec/5)
//
//
//    println(mat1 / 2.0)
//    println(2.0 * mat1)
//
//    val alpha1 = PI / 4.0
//    val beta1 = PI / 4.0
//    val gamma1 = PI / 4.0
//
//    val error1 = Matrix(3, 3, doubleArrayOf(
//        cos(alpha1) * cos(gamma1) - cos(beta1) * sin(alpha1) * sin(gamma1),
//        -cos(alpha1) * sin(gamma1) - cos(beta1) * cos(gamma1) * sin(alpha1),
//        sin(alpha1) * sin(beta1),
//        cos(gamma1) * sin(alpha1) + cos(alpha1) * cos(beta1) * sin(gamma1),
//        cos(alpha1) * cos(beta1) * cos(gamma1) - sin(alpha1) * sin(gamma1),
//        -cos(alpha1) * sin(gamma1),
//        sin(beta1) * sin(gamma1),
//        cos(gamma1) * sin(beta1),
//        cos(beta1)
//    ))
//    println(error1)
//    println(Matrix.eulerRotationMatrix3d(alpha1, beta1, gamma1))

//    val mat = Matrix(3, 4) { i, j -> i + j }
//    val matt = Matrix(3, 3) { _, _ -> 1 }
//    println(mat.reshape(2, -1))

//    val vec = ColumnVector(3) { i -> i * i }
//    vec *= 3
//    println(vec)

//    val tensor = Tensor(6, intArrayOf(2,2,2,2,2,2), IntArray(2*2*2*2*2*2){it})
//    val tensor1 = Tensor(2, intArrayOf(2, 3), IntArray(2*3){it})
//    val tensor2 = Tensor(2, intArrayOf(3, 2), IntArray(2*3){it})
//    println(tensor1)
//    println()
//    println(tensor2)
//    println()
//    println(tensor2 * tensor)

//    println(mat1)
//    println()
//    println(mat2)
//    mat1 *= 2
//    println()
//    mat1[intArrayOf(2,2)] = 123
//    println(mat1[intArrayOf(2,2)])
//    println(tensor[tensor.dataIndexToTensorIndices(36)])
//    val tensor3 = Tensor.stack(arrayListOf(tensor2, tensor2, tensor2))
//    println(tensor3)
//    println(Tensor.stack(arrayListOf(tensor3, tensor3, tensor3)))

//    val tensor4 = Tensor(intArrayOf(2,2,2), IntArray(8) {it})
//    val tensor5 = Tensor(intArrayOf(3,2,2), IntArray(12) {-it})
//
//    println(tensor4)
//    println(tensor5)
//    println(tensor4.concat(tensor5, 0))

    val z1 = ComplexDouble(1, 1)
    val z2 = ComplexDouble(1, -1)
    println(z1)
    println(z2)
    println(z1 / z2)
}