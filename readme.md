# Matrix class for Kotlin
## initialization
### Matrix
```kotlin
val mat = Matrix(4, 3)
println(mat)
```
```
[ 0.0 0.0 0.0 ]
[ 0.0 0.0 0.0 ]
[ 0.0 0.0 0.0 ]
[ 0.0 0.0 0.0 ]
```
Supports initialization with `DoubleArray` `FloatArray`, `LongArray`, or `IntArray`.
```kotlin
val mat = Matrix(4, 4, doubleArrayOf(
        1.0, 3.0, 5.0, 9.0,
        1.0, 3.0, 1.0, 7.0,
        4.0, 3.0, 9.0, 7.0,
        5.0, 2.0, 0.0, 9.0
    ))
println(mat)
```
```
[ 1.0 3.0 5.0 9.0 ]
[ 1.0 3.0 1.0 7.0 ]
[ 4.0 3.0 9.0 7.0 ]
[ 5.0 2.0 0.0 9.0 ]
```
### Vector
Supports both column vector and row vector.
```kotlin
val colVec = ColumnVector(3)
val rowVec = RowVector(3, doubleArrayof(1, 2, 3))
println(colVec)
println(rowVec)
```
```
[ 0.0 ]
[ 0.0 ]
[ 0.0 ]

[ 1.0 2.0 3.0 ]
```

## Supported operations
### Matrix
* Basic operations
    * Matrix + Matrix
    * Matrix - Matrix
    * Matrix * Matrix
    * Matrix * (Double, Long, Int, Float)
    * Get an element by `val e = mat[i, j]`
    * Set an element by `mat[i, j] = e`

* Additional operations
    * Construct an identity matrix by `val eye = Matrix.identityMatrix(size)`
    * Transpose a matrix by `val matT = mat.transpose()`
    * Get a squared Frobenius norm by `val squaredNorm = mat.frobeniusNormSquared()`
    * Calculate a determinant( O(n<sup>3</sup>) ) by `val det = mat.determinant()`
    * Get an adjoint matrix by `val adjMat = mat.adjointMatrix()`
    * Get an inverse matrix by `val invMat = mat.inverseMatrix()`
    * Get a submatrix by `val subMat = mat.getSubmatrix(rowIndexStart, rowIndexEnd, colIndexStart, colIndexEnd)`
    * Set a submatrix by `mat.getSubmatrix(rowIndexStart, rowIndexEnd, colIndexStart, colIndexEnd, newMat)`
    * Get a cofactor matrix by `val cofactorMat = mat.cofactorMatrix()`
    * Do a row switching operation by `val newMat = mat.switchRow(rowIndex1, rowIndex2)`
    * Concat to another matrix by `val concatMat = mat.concat(otherMat, dim)`
    * Get a col/row vector-wise norm<sup>2</sup> array by `val normMat = mat.colVecNormSq()`
    * Sum up all elements by `val sum = mat.sum()`
    * Get an element-wise product by `val newMat = mat1.eltwiseMul(mat2)`

### Vector
* Basic operations
    * Vector + Vector
    * Vector - Vector
    * vector * (Double, Long, Int, Float)
    * Get an element by `val e = vec[i]`
    * Set an element by `vec[i] = e`

* Additional operations
    * Transpose a vector by `val vecT = vec.transpose()`
    * Get a squared Frobenius norm by `val squaredNorm = mat.frobeniusNormSquared()`
    * Get a subvector by `val subVec = mat.getSubvector(IndexStart, IndexEnd)`
    * Set a subvector by `mat.getSubvector(IndexStart, IndexEnd, newVec)`
    * Concat to another matrix or vector by `val concatMat = vec.concat(otherMat, dim)`
    * Sum up all elements by `val sum = vec.sum()`
    * Get an element-wise product by `val newVec = vec1.eltwiseMul(vec2)`
    * Dot product by `val dotProd = vec1.dotProduct(vec2)`