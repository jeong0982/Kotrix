# Matrix class for Kotlin
## initialization
### Matrix
```kotlin
val mat = Matrix(4, 3)
println(mat)
```
```
[  0.00  0.00  0.00  ]
[  0.00  0.00  0.00  ]
[  0.00  0.00  0.00  ]
[  0.00  0.00  0.00  ]
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
[  1.00  3.00  5.00  9.00  ]
[  1.00  3.00  1.00  7.00  ]
[  4.00  3.00  9.00  7.00  ]
[  5.00  2.00  0.00  9.00  ]
```
Supports initialization with a lambda function
```kotlin
val mat = Matrix(3, 3) { i, j -> i + j }
println(mat)
```
```
[  0.00  1.00  2.00  ]
[  1.00  2.00  3.00  ]
[  2.00  3.00  4.00  ]
```
### Vector
Supports both column vector and row vector.
```kotlin
val colVec = ColumnVector(3)
val rowVec = RowVector(3, doubleArrayOf(1.0, 2.0, 3.0))
println(colVec)
println(rowVec)
```
```
[  0.00  ]
[  0.00  ]
[  0.00  ]

[  1.00  2.00  3.00  ]
```
Supports initialization with a lambda function
```kotlin
val vec = ColumnVector(3) { i -> i * i }
println(vec)
```
```
[  0.00  ]
[  1.00  ]
[  4.00  ]
```

## Supported operations
### Matrix
* Basic operations
    * +Matrix, -Matrix
    * Matrix + Matrix
    * Matrix - Matrix
    * Matrix * Matrix
    * Matrix * Vector
    * Matrix * (Double, Long, Int, Float)
    * (Double, Long, Int, Float) * Matrix
    * Matrix / (Double, Long, Int, Float)
    * Matrix += Matrix
    * Matrix -= Matrix
    * Matrix *= (Double, Long, Int, Float)
    * Matrix /= (Double, Long, Int, Float)
    * Get an element `val e = mat[i, j]`
    * Set an element `mat[i, j] = e`
  

* Matrix creations
    * Identity matrix `val eye = Matrix.identityMatrix(size)`
    * Zero matrix `val zeros = Matrix.zeros(m, n)`
    * Matrix of Ones `val ones = Matrix.ones(m, n)`
    * 2d rotation matrix `val rotMat2d = Matrix.rotationMatrix2d(theta)`
    * 3d rotation matrix along x/y/z axis `val rotMat3d = Matrix.rotationMatrix3dX/Y/Z(theta)`
    * Euler rotation matrix (z-x'-z'' sequence) `val rotMatEuler = Matrix.eulerRotationMatrix(alpha, beta, gamma)`


* Additional operations
    * Transpose a matrix `val matT = mat.transpose()`
    * Get a squared Frobenius norm `val squaredNorm = mat.frobeniusNormSquared()`
    * Calculate a determinant( O(n<sup>3</sup>) ) `val det = mat.determinant()`
    * Get an adjoint matrix `val adjMat = mat.adjointMatrix()`
    * Get an inverse matrix `val invMat = mat.inverseMatrix()`
    * Get a submatrix `val subMat = mat.getSubmatrix(rowIndexStart, rowIndexEnd, colIndexStart, colIndexEnd)`
    * Set a submatrix `mat.getSubmatrix(rowIndexStart, rowIndexEnd, colIndexStart, colIndexEnd, newMat)`
    * Get a cofactor matrix `val cofactorMat = mat.cofactorMatrix()`
    * Do a row switching operation `val newMat = mat.switchRow(rowIndex1, rowIndex2)`
    * Concat to another matrix `val concatMat = mat.concat(otherMat, dim)`
    * Get a col/row vector-wise norm<sup>2</sup> vector `val normVec = mat.colVecNormSq()`
    * Sum up all elements `val sum = mat.sum()`
    * Get an element-wise product `val newMat = mat1.eltwiseMul(mat2)`
    * Get a column-wise mean `val colWiseMeanVec = mat.columnWiseMean()`
    * Get a row-wise mean `val rowWiseMeanVec = mat.rowWiseMean()`
    * Make a mapped matrix `val sinMat = mat.map {elem -> sin(elem)}`
    * Reshape `val newMat = mat.reshape(3, -1)`

### Vector
* Basic operations
    * +Vector, -Vector
    * Vector + Vector
    * Vector - Vector
    * Vector * Matrix
    * Vector * (Double, Long, Int, Float)
    * (Double, Long, Int, Float) * Vector
    * Vector / (Double, Long, Int, Float)
    * Vector += Vector
    * Vector -= Vector
    * Vector *= (Double, Long, Int, Float)
    * Vector /= (Double, Long, Int, Float)
    * Get an element `val e = vec[i]`
    * Set an element `vec[i] = e`

* Additional operations
    * Transpose a vector `val vecT = vec.transpose()`
    * Get a squared Frobenius norm `val squaredNorm = mat.frobeniusNormSquared()`
    * Get a subvector `val subVec = mat.getSubvector(IndexStart, IndexEnd)`
    * Set a subvector `mat.getSubvector(IndexStart, IndexEnd, newVec)`
    * Concat to another matrix or vector `val concatMat = vec.concat(otherMat, dim)`
    * Sum up all elements `val sum = vec.sum()`
    * Get an element-wise product `val newVec = vec1.eltwiseMul(vec2)`
    * Outer product `val mat = colVec * rowVec`
    * Dot product `val dotProd = vec1.dotProduct(vec2)`
    * Cross product `val crossProdVec = vec1.crossProduct(vec2)`
    * Replicate a vector to make a matrix `val mat = vec.replicate(length)`
    * Make a mapped vector `val sinVec = vec.map {elem -> sin(elem)}`
    * Reshape `val newMat = vec.reshape(3, 2)`