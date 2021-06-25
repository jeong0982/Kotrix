# Matrix class for Kotlin
## How to initialize
### Matrix
#### With no data
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
#### With initial data
Supports initialization with `DoubleArray` or `LongArray`.
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
### Basic operations
* Matrix + Matrix
* Matrix - Matrix
* Matrix * Matrix
* Matrix * Double
* Get an element by `val e = mat[i, j]`
* Set an element by `mat[i, j] = e`

### Additional operations
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
