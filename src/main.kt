import java.util.*
import kotlin.math.PI

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

    val rowVec = ColumnVector(8, doubleArrayOf(1.0, 10.0, 100.0, 1000.0, -1.0, -10.0, -100.0, -1000.0))
    println(rowVec)
}