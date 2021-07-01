# Matrix class for Kotlin
## Introduction
`Kotrix` is a set of classes that helps people dealing with linear algebra in `Kotlin`.

There are three classes in this repository.
`Tensor` class represents multidimensional tensors that can be added, subtracted, multiplied each other.  
`Matrix` class, which inherits `Tensor`, represents matrices(i.e., 2-dimensional tensors).  
Finally, `ColumnVector` and `RowVector` classes, which inherits `Matrix`, represent vectors.
## Class Initialization
### Tensor
Supports initialization with `DoubleArray` `FloatArray`, `LongArray`, or `IntArray`.
```kotlin
val tensor = Tensor(
  dim = 6,
  shape = intArrayOf(2, 2, 2, 2, 2, 2),
  data = IntArray(64){ it }
)
println(tensor)
```
```
[                                                                                      ]
[  [                                      ]  [                                      ]  ]
[  [  [  0.00  1.00  ]  [  4.00  5.00  ]  ]  [  [  16.0  17.0  ]  [  20.0  21.0  ]  ]  ]
[  [  [  2.00  3.00  ]  [  6.00  7.00  ]  ]  [  [  18.0  19.0  ]  [  22.0  23.0  ]  ]  ]
[  [                                      ]  [                                      ]  ]
[  [  [  8.00  9.00  ]  [  12.0  13.0  ]  ]  [  [  24.0  25.0  ]  [  28.0  29.0  ]  ]  ]
[  [  [  10.0  11.0  ]  [  14.0  15.0  ]  ]  [  [  26.0  27.0  ]  [  30.0  31.0  ]  ]  ]
[  [                                      ]  [                                      ]  ]
[                                                                                      ]
[  [                                      ]  [                                      ]  ]
[  [  [  32.0  33.0  ]  [  36.0  37.0  ]  ]  [  [  48.0  49.0  ]  [  52.0  53.0  ]  ]  ]
[  [  [  34.0  35.0  ]  [  38.0  39.0  ]  ]  [  [  50.0  51.0  ]  [  54.0  55.0  ]  ]  ]
[  [                                      ]  [                                      ]  ]
[  [  [  40.0  41.0  ]  [  44.0  45.0  ]  ]  [  [  56.0  57.0  ]  [  60.0  61.0  ]  ]  ]
[  [  [  42.0  43.0  ]  [  46.0  47.0  ]  ]  [  [  58.0  59.0  ]  [  62.0  63.0  ]  ]  ]
[  [                                      ]  [                                      ]  ]
[                                                                                      ]
```
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
### Tensor
* Basic operations 
    * +Tensor, -Tensor
    * Tensor + Tensor
    * Tensor - Tensor
    * Tensor * Tensor  (Tensor product with axis = 1)
    * Tensor * Matrix
    * Tensor * Vector
    * Tensor * (Double, Long, Int, Float)
    * (Double, Long, Int, Float) * Tensor
    * Tensor / (Double, Long, Int, Float)
    * Tensor += Tensor
    * Tensor -= Tensor
    * Tensor *= (Double, Long, Int, Float)
    * Tensor /= (Double, Long, Int, Float)
    * Get the i-th tensor of the outermost dimension `val newTensor = tensor[i: Long]`
    * Get an element with a specific indices `val e = tensor[arrayOfIndices]`
    * Set an element `tensor[arrayOfIndices] = value`
  
* Tensor creations    *(Will be added)*
* Additional operations
  * Downcast to `Matrix` class
  * Reshape     *(Will be added)*
  * Concat     *(Will be added)*
  * Stack     *(Will be added)*
### Matrix
* Basic operations
    * +Matrix, -Matrix
    * Matrix + Matrix
    * Matrix - Matrix
    * Matrix * Tensor
    * Matrix * Matrix  (Matrix multiplication)
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
