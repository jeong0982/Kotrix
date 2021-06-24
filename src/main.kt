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
    println(mat1.concat(mat2, 0))
}