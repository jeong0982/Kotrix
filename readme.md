# Matrix class for Kotlin
## How to initialize
### With no data
```kotlin
val mat = Matrix(4, 3)
println(mat)
```
#### result
```
[ 0.0 0.0 0.0 ]
[ 0.0 0.0 0.0 ]
[ 0.0 0.0 0.0 ]
[ 0.0 0.0 0.0 ]
```
### With initial data
```kotlin
val mat = Matrix(4, 4, doubleArrayOf(
        1.0, 3.0, 5.0, 9.0,
        1.0, 3.0, 1.0, 7.0,
        4.0, 3.0, 9.0, 7.0,
        5.0, 2.0, 0.0, 9.0
    ))
println(mat)
```
#### result
```
[ 1.0 3.0 5.0 9.0 ]
[ 1.0 3.0 1.0 7.0 ]
[ 4.0 3.0 9.0 7.0 ]
[ 5.0 2.0 0.0 9.0 ]
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
* Construct an identity matrix by `val eye = Matrix.identityMatrix()`
* Transpose a matrix by `val matT = mat.transpose()`
* Get a squared Frobenius norm by `val squaredNorm = mat.frobeniusNormSquared()`
* Calculate a determinant( O(n<sup>3</sup>) ) by `val det = mat.determinant()`
* Get an adjoint matrix by `val adjMat = mat.adjointMatrix()`
* Get an inverse matrix by `val invMat = mat.inverseMatrix()`
* Get a submatrix by `val subMat = mat.getSubmatrix(rowIndexStart, rowIndexEnd, colIndexStart, colIndexEnd)`
* Set a submatrix by `mat.getSubmatrix(rowIndexStart, rowIndexEnd, colIndexStart, colIndexEnd, newMat)`
* Get a cofactor matrix by `val cofactorMat = mat.cofactorMatrix()`
* Do a row switching operation by `val newMat = mat.switchRow(rowIndex1, rowIndex2)`