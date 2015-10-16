package eu.sealsproject.domain.oet.recommendation.Jama;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.Locale;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.net.URL;

import javax.sound.midi.SysexMessage;

import eu.sealsproject.domain.oet.recommendation.Jama.util.Maths;
import eu.sealsproject.domain.oet.recommendation.util.map.MatrixMapping;

/**
 * Jama = Java Matrix class.
 * <P>
 * The Java Matrix Class provides the fundamental operations of numerical linear
 * algebra. Various constructors create Matrices from two dimensional arrays of
 * double precision floating point numbers. Various "gets" and "sets" provide
 * access to submatrices and matrix elements. Several methods implement basic
 * matrix arithmetic, including matrix addition and multiplication, matrix
 * norms, and element-by-element array operations. Methods for reading and
 * printing matrices are also included. All the operations in this version of
 * the Matrix Class involve real matrices. Complex matrices may be handled in a
 * future version.
 * <P>
 * Five fundamental matrix decompositions, which consist of pairs or triples of
 * matrices, permutation vectors, and the like, produce results in five
 * decomposition classes. These decompositions are accessed by the Matrix class
 * to compute solutions of simultaneous linear equations, determinants, inverses
 * and other matrix functions. The five decompositions are:
 * <P>
 * <UL>
 * <LI>Cholesky Decomposition of symmetric, positive definite matrices.
 * <LI>LU Decomposition of rectangular matrices.
 * <LI>QR Decomposition of rectangular matrices.
 * <LI>Singular Value Decomposition of rectangular matrices.
 * <LI>Eigenvalue Decomposition of both symmetric and nonsymmetric square
 * matrices.
 * </UL>
 * <DL>
 * <DT><B>Example of use:</B></DT>
 * <P>
 * <DD>Solve a linear system A x = b and compute the residual norm, ||b - A x||.
 * <P>
 * 
 * <PRE>
 * double[][] vals = { { 1., 2., 3 }, { 4., 5., 6. }, { 7., 8., 10. } };
 * Matrix A = new Matrix(vals);
 * Matrix b = Matrix.random(3, 1);
 * Matrix x = A.solve(b);
 * Matrix r = A.times(x).minus(b);
 * double rnorm = r.normInf();
 * </PRE>
 * 
 * </DD>
 * </DL>
 * 
 * @author The MathWorks, Inc. and the National Institute of Standards and
 *         Technology.
 * @version 5 August 1998
 */

public class Matrix implements Cloneable, Serializable {

	/*
	 * ------------------------ Class variables ------------------------
	 */

	/**
	 * Array for internal storage of elements.
	 * 
	 * @serial internal array storage.
	 */
	private double[][] A;

	/**
	 * Row dimension
	 * 
	 * @serial row dimension.
	 * 
	 */
	private int rowDimension;
	
	/**
	 * Column dimension
	 * @serial column dimension.
	 */
	private int columnDimension;

	/*
	 * ------------------------ Constructors ------------------------
	 */

	/**
	 * Construct an m-by-n matrix of zeros.
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 */

	public Matrix(int m, int n) {
		this.rowDimension = m;
		this.columnDimension = n;
		A = new double[m][n];
	}

	/**
	 * Construct an m-by-n constant matrix.
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @param s
	 *            Fill the matrix with this scalar value.
	 */

	public Matrix(int m, int n, double s) {
		this.rowDimension = m;
		this.columnDimension = n;
		A = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = s;
			}
		}
	}

	/**
	 * Construct a matrix from a 2-D array.
	 * 
	 * @param A
	 *            Two-dimensional array of doubles.
	 * @exception IllegalArgumentException
	 *                All rows must have the same length
	 * @see #constructWithCopy
	 */

	public Matrix(double[][] A) {
		rowDimension = A.length;
		columnDimension = A[0].length;
		for (int i = 0; i < rowDimension; i++) {
			if (A[i].length != columnDimension) {
				throw new IllegalArgumentException(
						"All rows must have the same length.");
			}
		}
		this.A = A;
	}

	/**
	 * Construct a matrix quickly without checking arguments.
	 * 
	 * @param A
	 *            Two-dimensional array of doubles.
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 */

	public Matrix(double[][] A, int m, int n) {
		this.A = A;
		this.rowDimension = m;
		this.columnDimension = n;
	}

	/**
	 * Construct a matrix from a one-dimensional packed array
	 * 
	 * @param vals
	 *            One-dimensional array of doubles, packed by columns (ala
	 *            Fortran).
	 * @param m
	 *            Number of rows.
	 * @exception IllegalArgumentException
	 *                Array length must be a multiple of m.
	 */

	public Matrix(double vals[], int m) {
		this.rowDimension = m;
		columnDimension = (m != 0 ? vals.length / m : 0);
		if (m * columnDimension != vals.length) {
			throw new IllegalArgumentException(
					"Array length must be a multiple of m.");
		}
		A = new double[m][columnDimension];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < columnDimension; j++) {
				A[i][j] = vals[i + j * m];
			}
		}
	}

	/*
	 * ------------------------ Public Methods ------------------------
	 */

	/**
	 * Construct a matrix from a copy of a 2-D array.
	 * 
	 * @param A
	 *            Two-dimensional array of doubles.
	 * @exception IllegalArgumentException
	 *                All rows must have the same length
	 */

	public static Matrix constructWithCopy(double[][] A) {
		int m = A.length;
		int n = A[0].length;
		Matrix X = new Matrix(m, n);
		double[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			if (A[i].length != n) {
				throw new IllegalArgumentException(
						"All rows must have the same length.");
			}
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j];
			}
		}
		return X;
	}

	/**
	 * Make a deep copy of a matrix
	 */

	public Matrix copy() {
		Matrix X = new Matrix(rowDimension, columnDimension);
		double[][] C = X.getArray();
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				C[i][j] = A[i][j];
			}
		}
		X.setMapping(this.getMapping());
		return X;
	}

	/**
	 * Clone the Matrix object.
	 */

	public Object clone() {
		return this.copy();
	}

	/**
	 * Access the internal two-dimensional array.
	 * 
	 * @return Pointer to the two-dimensional array of matrix elements.
	 */

	public double[][] getArray() {
		return A;
	}

	/**
	 * Copy the internal two-dimensional array.
	 * 
	 * @return Two-dimensional array copy of matrix elements.
	 */

	public double[][] getArrayCopy() {
		double[][] C = new double[rowDimension][columnDimension];
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				C[i][j] = A[i][j];
			}
		}
		return C;
	}

	/**
	 * Make a one-dimensional column packed copy of the internal array.
	 * 
	 * @return Matrix elements packed in a one-dimensional array by columns.
	 */

	public double[] getColumnPackedCopy() {
		double[] vals = new double[rowDimension * columnDimension];
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				vals[i + j * rowDimension] = A[i][j];
			}
		}
		return vals;
	}

	/**
	 * Make a one-dimensional row packed copy of the internal array.
	 * 
	 * @return Matrix elements packed in a one-dimensional array by rows.
	 */

	public double[] getRowPackedCopy() {
		double[] vals = new double[rowDimension * columnDimension];
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				vals[i * columnDimension + j] = A[i][j];
			}
		}
		return vals;
	}

	/**
	 * Get row dimension.
	 * 
	 * @return m, the number of rows.
	 */

	public int getRowDimension() {
		return rowDimension;
	}

	/**
	 * Get column dimension.
	 * 
	 * @return n, the number of columns.
	 */

	public int getColumnDimension() {
		return columnDimension;
	}

	/**
	 * Get a single element.
	 * 
	 * @param i
	 *            Row index.
	 * @param j
	 *            Column index.
	 * @return A(i,j)
	 * @exception ArrayIndexOutOfBoundsException
	 */

	public double get(int i, int j) {
		return A[i][j];
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @return A(i0:i1,j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public Matrix getMatrix(int i0, int i1, int j0, int j1) {
		Matrix X = new Matrix(i1 - i0 + 1, j1 - j0 + 1);
		double[][] B = X.getArray();
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = j0; j <= j1; j++) {
					B[i - i0][j - j0] = A[i][j];
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @return A(r(:),c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public Matrix getMatrix(int[] r, int[] c) {
		Matrix X = new Matrix(r.length, c.length);
		double[][] B = X.getArray();
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = 0; j < c.length; j++) {
					B[i][j] = A[r[i]][c[j]];
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param c
	 *            Array of column indices.
	 * @return A(i0:i1,c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public Matrix getMatrix(int i0, int i1, int[] c) {
		Matrix X = new Matrix(i1 - i0 + 1, c.length);
		double[][] B = X.getArray();
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = 0; j < c.length; j++) {
					B[i - i0][j] = A[i][c[j]];
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param i0
	 *            Initial column index
	 * @param i1
	 *            Final column index
	 * @return A(r(:),j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public Matrix getMatrix(int[] r, int j0, int j1) {
		Matrix X = new Matrix(r.length, j1 - j0 + 1);
		double[][] B = X.getArray();
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = j0; j <= j1; j++) {
					B[i][j - j0] = A[r[i]][j];
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

	/**
	 * Set a single element.
	 * 
	 * @param i
	 *            Row index.
	 * @param j
	 *            Column index.
	 * @param s
	 *            A(i,j).
	 * @exception ArrayIndexOutOfBoundsException
	 */

	public void set(int i, int j, double s) {
		A[i][j] = s;
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @param X
	 *            A(i0:i1,j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public void setMatrix(int i0, int i1, int j0, int j1, Matrix X) {
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = j0; j <= j1; j++) {
					A[i][j] = X.get(i - i0, j - j0);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @param X
	 *            A(r(:),c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public void setMatrix(int[] r, int[] c, Matrix X) {
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = 0; j < c.length; j++) {
					A[r[i]][c[j]] = X.get(i, j);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @param X
	 *            A(r(:),j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public void setMatrix(int[] r, int j0, int j1, Matrix X) {
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = j0; j <= j1; j++) {
					A[r[i]][j] = X.get(i, j - j0);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param c
	 *            Array of column indices.
	 * @param X
	 *            A(i0:i1,c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public void setMatrix(int i0, int i1, int[] c, Matrix X) {
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = 0; j < c.length; j++) {
					A[i][c[j]] = X.get(i - i0, j);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Matrix transpose.
	 * 
	 * @return A'
	 */

	public Matrix transpose() {
		Matrix X = new Matrix(columnDimension, rowDimension);
		double[][] C = X.getArray();
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				C[j][i] = A[i][j];
			}
		}
		return X;
	}

	/**
	 * One norm
	 * 
	 * @return maximum column sum.
	 */

	public double norm1() {
		double f = 0;
		for (int j = 0; j < columnDimension; j++) {
			double s = 0;
			for (int i = 0; i < rowDimension; i++) {
				s += Math.abs(A[i][j]);
			}
			f = Math.max(f, s);
		}
		return f;
	}

	/**
	 * Two norm
	 * 
	 * @return maximum singular value.
	 */

	public double norm2() {
		return (new SingularValueDecomposition(this).norm2());
	}

	/**
	 * Infinity norm
	 * 
	 * @return maximum row sum.
	 */

	public double normInf() {
		double f = 0;
		for (int i = 0; i < rowDimension; i++) {
			double s = 0;
			for (int j = 0; j < columnDimension; j++) {
				s += Math.abs(A[i][j]);
			}
			f = Math.max(f, s);
		}
		return f;
	}

	/**
	 * Frobenius norm
	 * 
	 * @return sqrt of sum of squares of all elements.
	 */

	public double normF() {
		double f = 0;
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				f = Maths.hypot(f, A[i][j]);
			}
		}
		return f;
	}

	/**
	 * Unary minus
	 * 
	 * @return -A
	 */

	public Matrix uminus() {
		Matrix X = new Matrix(rowDimension, columnDimension);
		double[][] C = X.getArray();
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				C[i][j] = -A[i][j];
			}
		}
		return X;
	}

	/**
	 * C = A + B
	 * 
	 * @param B
	 *            another matrix
	 * @return A + B
	 */

	public Matrix plus(Matrix B) {
		checkMatrixDimensions(B);
		Matrix X = new Matrix(rowDimension, columnDimension);
		double[][] C = X.getArray();
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				C[i][j] = A[i][j] + B.A[i][j];
			}
		}
		return X;
	}

	/**
	 * A = A + B
	 * 
	 * @param B
	 *            another matrix
	 * @return A + B
	 */

	public Matrix plusEquals(Matrix B) {
		checkMatrixDimensions(B);
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				A[i][j] = A[i][j] + B.A[i][j];
			}
		}
		return this;
	}

	/**
	 * C = A - B
	 * 
	 * @param B
	 *            another matrix
	 * @return A - B
	 */

	public Matrix minus(Matrix B) {
		checkMatrixDimensions(B);
		Matrix X = new Matrix(rowDimension, columnDimension);
		double[][] C = X.getArray();
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				C[i][j] = A[i][j] - B.A[i][j];
			}
		}
		return X;
	}

	/**
	 * A = A - B
	 * 
	 * @param B
	 *            another matrix
	 * @return A - B
	 */

	public Matrix minusEquals(Matrix B) {
		checkMatrixDimensions(B);
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				A[i][j] = A[i][j] - B.A[i][j];
			}
		}
		return this;
	}

	/**
	 * Element-by-element multiplication, C = A.*B
	 * 
	 * @param B
	 *            another matrix
	 * @return A.*B
	 */

	public Matrix arrayTimes(Matrix B) {
		checkMatrixDimensions(B);
		Matrix X = new Matrix(rowDimension, columnDimension);
		double[][] C = X.getArray();
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				C[i][j] = A[i][j] * B.A[i][j];
			}
		}
		return X;
	}

	/**
	 * Element-by-element multiplication in place, A = A.*B
	 * 
	 * @param B
	 *            another matrix
	 * @return A.*B
	 */

	public Matrix arrayTimesEquals(Matrix B) {
		checkMatrixDimensions(B);
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				A[i][j] = A[i][j] * B.A[i][j];
			}
		}
		return this;
	}

	/**
	 * Element-by-element right division, C = A./B
	 * 
	 * @param B
	 *            another matrix
	 * @return A./B
	 */

	public Matrix arrayRightDivide(Matrix B) {
		checkMatrixDimensions(B);
		Matrix X = new Matrix(rowDimension, columnDimension);
		double[][] C = X.getArray();
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				C[i][j] = A[i][j] / B.A[i][j];
			}
		}
		return X;
	}

	/**
	 * Element-by-element right division in place, A = A./B
	 * 
	 * @param B
	 *            another matrix
	 * @return A./B
	 */

	public Matrix arrayRightDivideEquals(Matrix B) {
		checkMatrixDimensions(B);
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				A[i][j] = A[i][j] / B.A[i][j];
			}
		}
		return this;
	}

	/**
	 * Element-by-element left division, C = A.\B
	 * 
	 * @param B
	 *            another matrix
	 * @return A.\B
	 */

	public Matrix arrayLeftDivide(Matrix B) {
		checkMatrixDimensions(B);
		Matrix X = new Matrix(rowDimension, columnDimension);
		double[][] C = X.getArray();
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				C[i][j] = B.A[i][j] / A[i][j];
			}
		}
		return X;
	}

	/**
	 * Element-by-element left division in place, A = A.\B
	 * 
	 * @param B
	 *            another matrix
	 * @return A.\B
	 */

	public Matrix arrayLeftDivideEquals(Matrix B) {
		checkMatrixDimensions(B);
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				A[i][j] = B.A[i][j] / A[i][j];
			}
		}
		return this;
	}

	/**
	 * Multiply a matrix by a scalar, C = s*A
	 * 
	 * @param s
	 *            scalar
	 * @return s*A
	 */

	public Matrix times(double s) {
		Matrix X = new Matrix(rowDimension, columnDimension);
		double[][] C = X.getArray();
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				C[i][j] = s * A[i][j];
			}
		}
		return X;
	}

	/**
	 * Multiply a matrix by a scalar in place, A = s*A
	 * 
	 * @param s
	 *            scalar
	 * @return replace A by s*A
	 */

	public Matrix timesEquals(double s) {
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				A[i][j] = s * A[i][j];
			}
		}
		return this;
	}

	/**
	 * Linear algebraic matrix multiplication, A * B
	 * 
	 * @param B
	 *            another matrix
	 * @return Matrix product, A * B
	 * @exception IllegalArgumentException
	 *                Matrix inner dimensions must agree.
	 */

	public Matrix times(Matrix B) {
		if (B.rowDimension != columnDimension) {
			throw new IllegalArgumentException(
					"Matrix inner dimensions must agree.");
		}
		Matrix X = new Matrix(rowDimension, B.columnDimension);
		X.setMapping(this.getMapping());
		double[][] C = X.getArray();
		double[] Bcolj = new double[columnDimension];
		for (int j = 0; j < B.columnDimension; j++) {
			for (int k = 0; k < columnDimension; k++) {
				Bcolj[k] = B.A[k][j];
			}
			for (int i = 0; i < rowDimension; i++) {
				double[] Arowi = A[i];
				double s = 0;
				for (int k = 0; k < columnDimension; k++) {
					s += Arowi[k] * Bcolj[k];
				}
				C[i][j] = s;
			}
		}
		return X;
	}

	/**
	 * LU Decomposition
	 * 
	 * @return LUDecomposition
	 * @see LUDecomposition
	 */

	public LUDecomposition lu() {
		return new LUDecomposition(this);
	}

	/**
	 * QR Decomposition
	 * 
	 * @return QRDecomposition
	 * @see QRDecomposition
	 */

	public QRDecomposition qr() {
		return new QRDecomposition(this);
	}

	/**
	 * Cholesky Decomposition
	 * 
	 * @return CholeskyDecomposition
	 * @see CholeskyDecomposition
	 */

	public CholeskyDecomposition chol() {
		return new CholeskyDecomposition(this);
	}

	/**
	 * Singular Value Decomposition
	 * 
	 * @return SingularValueDecomposition
	 * @see SingularValueDecomposition
	 */

	public SingularValueDecomposition svd() {
		return new SingularValueDecomposition(this);
	}

	/**
	 * Eigenvalue Decomposition
	 * 
	 * @return EigenvalueDecomposition
	 * @see EigenvalueDecomposition
	 */

	public EigenvalueDecomposition eig() {
		return new EigenvalueDecomposition(this);
	}

	/**
	 * Solve A*X = B
	 * 
	 * @param B
	 *            right hand side
	 * @return solution if A is square, least squares solution otherwise
	 */

	public Matrix solve(Matrix B) {
		return (rowDimension == columnDimension ? (new LUDecomposition(this)).solve(B)
				: (new QRDecomposition(this)).solve(B));
	}

	/**
	 * Solve X*A = B, which is also A'*X' = B'
	 * 
	 * @param B
	 *            right hand side
	 * @return solution if A is square, least squares solution otherwise.
	 */

	public Matrix solveTranspose(Matrix B) {
		return transpose().solve(B.transpose());
	}

	/**
	 * Matrix inverse or pseudoinverse
	 * 
	 * @return inverse(A) if A is square, pseudoinverse otherwise.
	 */

	public Matrix inverse() {
		return solve(identity(rowDimension, rowDimension));
	}

	/**
	 * Matrix determinant
	 * 
	 * @return determinant
	 */

	public double det() {
		return new LUDecomposition(this).det();
	}

	/**
	 * Matrix rank
	 * 
	 * @return effective numerical rank, obtained from SVD.
	 */

	public int rank() {
		return new SingularValueDecomposition(this).rank();
	}

	/**
	 * Matrix condition (2 norm)
	 * 
	 * @return ratio of largest to smallest singular value.
	 */

	public double cond() {
		return new SingularValueDecomposition(this).cond();
	}

	/**
	 * Matrix trace.
	 * 
	 * @return sum of the diagonal elements.
	 */

	public double trace() {
		double t = 0;
		for (int i = 0; i < Math.min(rowDimension, columnDimension); i++) {
			t += A[i][i];
		}
		return t;
	}

	/**
	 * Generate matrix with random elements
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @return An m-by-n matrix with uniformly distributed random elements.
	 */

	public static Matrix random(int m, int n) {
		Matrix A = new Matrix(m, n);
		double[][] X = A.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				X[i][j] = Math.random();
			}
		}
		return A;
	}

	/**
	 * Generate identity matrix
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @return An m-by-n matrix with ones on the diagonal and zeros elsewhere.
	 */

	public static Matrix identity(int m, int n) {
		Matrix A = new Matrix(m, n);
		double[][] X = A.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				X[i][j] = (i == j ? 1.0 : 0.0);
			}
		}
		return A;
	}

	/**
	 * Print the matrix to stdout. Line the elements up in columns with a
	 * Fortran-like 'Fw.d' style format.
	 * 
	 * @param w
	 *            Column width.
	 * @param d
	 *            Number of digits after the decimal.
	 */

	public void print(int w, int d) {
		print(new PrintWriter(System.out, true), w, d);
	}

	/**
	 * Print the matrix to the output stream. Line the elements up in columns
	 * with a Fortran-like 'Fw.d' style format.
	 * 
	 * @param output
	 *            Output stream.
	 * @param w
	 *            Column width.
	 * @param d
	 *            Number of digits after the decimal.
	 */

	public void print(PrintWriter output, int w, int d) {
		DecimalFormat format = new DecimalFormat();
		format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(d);
		format.setMinimumFractionDigits(d);
		format.setGroupingUsed(false);
		print(output, format, w + 2);
	}

	/**
	 * Print the matrix to stdout. Line the elements up in columns. Use the
	 * format object, and right justify within columns of width characters. Note
	 * that is the matrix is to be read back in, you probably will want to use a
	 * NumberFormat that is set to US Locale.
	 * 
	 * @param format
	 *            A Formatting object for individual elements.
	 * @param width
	 *            Field width for each column.
	 * @see java.text.DecimalFormat#setDecimalFormatSymbols
	 */

	public void print(NumberFormat format, int width) {
		print(new PrintWriter(System.out, true), format, width);
	}

	// DecimalFormat is a little disappointing coming from Fortran or C's
	// printf.
	// Since it doesn't pad on the left, the elements will come out different
	// widths. Consequently, we'll pass the desired column width in as an
	// argument and do the extra padding ourselves.

	/**
	 * Print the matrix to the output stream. Line the elements up in columns.
	 * Use the format object, and right justify within columns of width
	 * characters. Note that is the matrix is to be read back in, you probably
	 * will want to use a NumberFormat that is set to US Locale.
	 * 
	 * @param output
	 *            the output stream.
	 * @param format
	 *            A formatting object to format the matrix elements
	 * @param width
	 *            Column width.
	 * @see java.text.DecimalFormat#setDecimalFormatSymbols
	 */

	public void print(PrintWriter output, NumberFormat format, int width) {
		output.println(); // start on new line.
		for (int i = 0; i < rowDimension; i++) {
			for (int j = 0; j < columnDimension; j++) {
				String s = format.format(A[i][j]); // format the number
				int padding = Math.max(1, width - s.length()); // At _least_ 1
																// space
				for (int k = 0; k < padding; k++)
					output.print(' ');
				output.print(s);
			}
			output.println();
		}
		output.println(); // end with blank line.
	}

	/**
	 * Read a matrix from a stream. The format is the same the print method, so
	 * printed matrices can be read back in (provided they were printed using US
	 * Locale). Elements are separated by whitespace, all the elements for each
	 * row appear on a single line, the last row is followed by a blank line.
	 * 
	 * @param input
	 *            the input stream.
	 */

	public static Matrix read(BufferedReader input) throws java.io.IOException {
		StreamTokenizer tokenizer = new StreamTokenizer(input);

		// Although StreamTokenizer will parse numbers, it doesn't recognize
		// scientific notation (E or D); however, Double.valueOf does.
		// The strategy here is to disable StreamTokenizer's number parsing.
		// We'll only get whitespace delimited words, EOL's and EOF's.
		// These words should all be numbers, for Double.valueOf to parse.

		tokenizer.resetSyntax();
		tokenizer.wordChars(0, 255);
		tokenizer.whitespaceChars(0, ' ');
		tokenizer.eolIsSignificant(true);
		java.util.Vector v = new java.util.Vector();

		// Ignore initial empty lines
		while (tokenizer.nextToken() == StreamTokenizer.TT_EOL)
			;
		if (tokenizer.ttype == StreamTokenizer.TT_EOF)
			throw new java.io.IOException("Unexpected EOF on matrix read.");
		do {
			v.addElement(Double.valueOf(tokenizer.sval)); // Read & store 1st
															// row.
		} while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);

		int n = v.size(); // Now we've got the number of columns!
		double row[] = new double[n];
		for (int j = 0; j < n; j++)
			// extract the elements of the 1st row.
			row[j] = ((Double) v.elementAt(j)).doubleValue();
		v.removeAllElements();
		v.addElement(row); // Start storing rows instead of columns.
		while (tokenizer.nextToken() == StreamTokenizer.TT_WORD) {
			// While non-empty lines
			v.addElement(row = new double[n]);
			int j = 0;
			do {
				if (j >= n)
					throw new java.io.IOException("Row " + v.size()
							+ " is too long.");
				row[j++] = Double.valueOf(tokenizer.sval).doubleValue();
			} while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);
			if (j < n)
				throw new java.io.IOException("Row " + v.size()
						+ " is too short.");
		}
		int m = v.size(); // Now we've got the number of rows.
		double[][] A = new double[m][];
		v.copyInto(A); // copy the rows out of the vector
		return new Matrix(A);
	}

	/*
	 * ------------------------ Private Methods ------------------------
	 */

	/** Check if size(A) == size(B) **/

	private void checkMatrixDimensions(Matrix B) {
		if (B.rowDimension != rowDimension || B.columnDimension != columnDimension) {
			throw new IllegalArgumentException("Matrix dimensions must agree.");
		}
	}

	/*
	 * ------------------------ Added - AHP/ANP related methods
	 * ------------------------ author: Filip
	 */

	/**
	 * Id of a matrix. For a pair wise comparison matrix, it represents the
	 * quality model characteristic to which the matrix is related.
	 */
	private String id;

	/**
	 * Matrix mapping. Maps every row and column of a matrix to a characteristic
	 * from a quality model
	 */
	private MatrixMapping mapping;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public MatrixMapping getMapping() {
		return mapping;
	}

	public void setMapping(MatrixMapping mapping) {
		this.mapping = mapping;
	}

	/**
	 * Writes a matrix to a file
	 * 
	 * @param fileName
	 */
	public void serialize(String fileName) {
		
//		URL url = Thread.currentThread().getContextClassLoader()
//		.getResource(fileName);
//		String path = url.getFile();
//		// remove white spaces encoded with %20
//		path = path.replaceAll("%20", " ");
		
		File dataFile = new File(fileName);
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(dataFile));
			oos.writeObject(this);
			oos.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error in serialization");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error in serialization");
			e.printStackTrace();
		}
	}

	/**
	 * Reads a matrix from a file
	 * 
	 * @param fileName
	 * @return
	 */
	public static Matrix deserialize(String fileName) {
		URL url = Thread.currentThread().getContextClassLoader()
		.getResource(fileName);
		String path = url.getFile();
		// remove white spaces encoded with %20
		path = path.replaceAll("%20", " ");
		
		File dataFile = new File(path);
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile));
			Matrix mm = (Matrix) ois.readObject();
			ois.close();
			return mm;
		} catch (FileNotFoundException e) {
			System.err.println("Error in deserialization");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error in deserialization");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Error in deserialization");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns a matrix with normalized columns, i.e. a matrix in which the sum
	 * in every column equals 1.
	 * 
	 * @return
	 */
	public Matrix normalizeColumns() {
		// create a new matrix object to fill
		double[][] mat = new double[getRowDimension()][getColumnDimension()];
		// iterate over every column
		for (int i = 0; i < getColumnDimension(); i++) {
			double columnSum = 0;
			// for a given column iterate over all rows to calculate column sum
			for (int j = 0; j < getRowDimension(); j++) {
				columnSum += get(j, i);
			}
			// iterate over all rows in a column to divide each cell value with
			// column sum
			for (int j = 0; j < getRowDimension(); j++) {
				if (columnSum != 0)
					mat[j][i] = get(j, i) / columnSum;
			}
		}
//		return new Matrix(mat);
		this.A = mat;
		return this;
	}

	/**
	 * Gets a one column matrix which entries are weights of the row elements
	 * 
	 * @return
	 */
	public Matrix getWeights() {
		int rowDimension = getRowDimension();
		// first, the columns are normalized in  anew matrix
		Matrix n = (Matrix)this.clone();
		Matrix normalized = n.normalizeColumns();
		// the vector which represents weights
		double[] eigenVector = new double[rowDimension];
		for (int i = 0; i < normalized.getRowDimension(); i++) {
			double rowSum = 0;
			for (int j = 0; j < normalized.getColumnDimension(); j++) {
				rowSum += normalized.get(i, j);
			}
			eigenVector[i] = rowSum / rowDimension;
		}
		return new Matrix(eigenVector, rowDimension);
	}

	/**
	 * Returns the matrix put to the power of n. In some cases, it is a limit
	 * supermatrix
	 * 
	 * @param n
	 *            - the maximum power to raise the matrix
	 * @return
	 */
	public Matrix calculateMatrixPower(int n) {
		Matrix mat = copy();
		for (int i = 0; i < n - 1; i++) {
			mat = mat.times(this);
			if (mat.isConvergent()) {
				System.err.println("The matrix converged at " + i + ". step");
				return mat;
			}
		}
		System.err.println("The matrix did not fully converge!");
		return mat;
	}

	/**
	 * Returns the matrix put to the power of 10001
	 * 
	 * @return
	 */
	public Matrix calculateMatrixPower() {
		return calculateMatrixPower(10001);
	}

	/**
	 * Checks if the matrix is convergent, i.e. if all columns are identical
	 * 
	 * @param mat
	 * @return
	 */
	public boolean isConvergent() {
		for (int i = 0; i < getRowDimension(); i++) {
			for (int j = 0; j < getColumnDimension() - 1; j++) {
				DecimalFormat format = new DecimalFormat();
				format.setMinimumIntegerDigits(1);
				format.setMaximumFractionDigits(5);
				format.setMinimumFractionDigits(5);
				double d1 = get(i, j);
				double d2 = get(i, j + 1);
				if (!format.format(d1).equalsIgnoreCase(format.format(d2)))
					return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the matrix is stochastic, i.e. if the sum of every column
	 * equals one
	 * 
	 * @param mat
	 * @return
	 */
	public boolean isStohastic() {
		boolean bool = true;
		for (int i = 0; i < getColumnDimension(); i++) {
			double sum = 0;
			for (int j = 0; j < getRowDimension(); j++) {
				sum += get(j, i);
			}
			if ((sum <= 0.99) || (sum >= 1.01)) {
				if(sum != 0){
					System.err.println(i + 1 + ". column sum " + sum);
					bool = false;
				}
				System.err.println(i + 1 + ". column sum " + sum);
			}
		}
		return bool;
	}

	/**
	 * Calculates the limit supermatrix based on the calculus algorithm, in
	 * which the convergence is checked by normalizing every power of a
	 * supermatrix
	 * 
	 * @return
	 */
	private Matrix calculusTypeLimitMatrixI() {

		int[] noSinks = removeSinkIndexes();
		Matrix mat = this.copy();
		for (int i = 0; i < 10001; i++) {
			mat = mat.times(this);
			Matrix c = mat.copy();
			Matrix nc = normalizeAndConverge(c, noSinks);
			if (nc != null) {
				System.err.println("The matrix converged at " + i + ". step");
				return nc;
			}
		}
		System.err.println("The matrix did not fully converge!");
		return mat;
	}

	/**
	 * Normalizes the matrix and checks if it converges
	 * 
	 * @param mat
	 *            matrix to be normalized
	 * @param indexes
	 *            of zero columns to be excluded from checking
	 * @return
	 */
	public Matrix normalizeAndConverge(Matrix mat, int[] columns) {
		Matrix reduced = mat.getMatrix(0, mat.getRowDimension() - 1, columns);
		reduced.setMapping(mat.getMapping());
//		Matrix reduced = mat.getMatrix(columns, columns);
		if (reduced.normalizeColumns().isConvergent())
			return reduced.normalizeColumns();
		return null;
	}

	/**
	 * Normalizes the matrix and checks if it converges
	 * 
	 * @param mat
	 *            matrix to be normalized
	 * @return
	 */
	public Matrix normalizeAndConverge(Matrix mat) {
		int[] noSinks = removeSinkIndexes();
		Matrix reduced = mat.getMatrix(0, mat.getRowDimension() - 1, noSinks);
		if (reduced.normalizeColumns().isConvergent())
			return reduced.normalizeColumns();
		return null;

	}

	/**
	 * Returns the indexes of zero columns
	 * 
	 * @return
	 */
	public int[] removeSinkIndexes() {
		LinkedList<Integer> zeroColumns = new LinkedList<Integer>();
		for (int i = 0; i < getColumnDimension(); i++) {
			double sum = 0;
			for (int j = 0; j < getRowDimension(); j++) {
				sum += get(j, i);
			}
			if (sum == 0) {
				zeroColumns.add(i);
			}
		}

		int[] columns = new int[getColumnDimension() - zeroColumns.size()];
		int counter = 0;
		for (int i = 0; i < getColumnDimension(); i++) {
			if (zeroColumns.contains(i))
				continue;
			else
				columns[counter++] = i;
		}
		return columns;
	}

	/**
	 * Calculates the limit supermatrix based on the calculus algorithm, in
	 * which it searches for loops
	 * 
	 * @return
	 */
	private Matrix calculusTypeLimitMatrixII() {
		Matrix mat = this.copy();
		Matrix start = mat.times(mat);
		Matrix temp;
		Matrix next = null;

		for (int i = 0; i < 1001; i++) {
			next = start.times(mat);

			if (start.equalMatrix(next)) {
				System.err.println("Loop found at " + i);
				return normalizeAndAverage(start, next);
			}
			temp = next;
			start = temp;
		}
		
		return calculusTypeLimitMatrixI();
	}
	
	/**
	 * Returns the limit matrix
	 * @return
	 */
	public Matrix calculateLimitMatrix(){
		return calculusTypeLimitMatrixII();
	}
	

	/**
	 * Normalizes and averages two matrices
	 * 
	 * @param start
	 *            matrix to be normalized
	 * @param next
	 *            matrix to be normalized
	 * @return
	 */
	private Matrix normalizeAndAverage(Matrix start, Matrix next) {
		Matrix mat1 = start.normalizeColumns();
		Matrix mat2 = next.normalizeColumns();
		Matrix limit = new Matrix(start.getRowDimension(), start
				.getColumnDimension());
		limit.setMapping(start.getMapping());
		for (int i = 0; i < mat1.getColumnDimension(); i++) {
			for (int j = 0; j < mat1.getRowDimension(); j++) {
				limit.set(j, i, (mat1.get(j, i) + mat2.get(j, i)) / 2);
			}
		}
		limit.setMapping(this.getMapping());
		return limit;
	}

	/**
	 * Checks if all entries in the supermatrix are zeros
	 * 
	 * @return
	 */
	public boolean isZero() {
		DecimalFormat format = new DecimalFormat();
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(5);
		format.setMinimumFractionDigits(5);
		for (int i = 0; i < getColumnDimension(); i++) {
			for (int j = 0; j < getRowDimension(); j++) {
				if (!format.format(get(j, i)).equalsIgnoreCase(
						format.format(0.00000))) {
					System.err.println(i + " " + j + " "
							+ format.format(get(j, i)));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if the matrix is equal to other matrix
	 * 
	 * @param next
	 *            matrix to check with
	 * @return
	 */
	public boolean equalMatrix(Matrix next) {
		if (this.getRowDimension() != next.getRowDimension())
			return false;
		if (this.getColumnDimension() != next.getColumnDimension())
			return false;

		DecimalFormat format = new DecimalFormat();
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(5);
		format.setMinimumFractionDigits(5);
		for (int i = 0; i < getColumnDimension(); i++) {
			for (int j = 0; j < getRowDimension(); j++) {
				if (!format.format(get(j, i)).equalsIgnoreCase(
						format.format(next.get(j, i))))
					return false;
			}
		}
		return true;
	}

	/**
	 * Returns the consistency ratio (CR) of the matrix
	 * 
	 * @return
	 */
	public double getConsistencyRatio() {
		DecimalFormat format = new DecimalFormat();
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(5);
		format.setMinimumFractionDigits(5);
		String cr = format.format(getConsistencyIndex()
				/ getRandomConsistencyIndex());
		return Double.parseDouble(cr);
	}

	/**
	 * Returns the consistency index (CI)of the matrix
	 * 
	 * @return
	 */
	protected double getConsistencyIndex() {
		double matrixSize = getRowDimension();
		return (getMaximumEigenValue() - matrixSize) / (matrixSize - 1);
	}

	/**
	 * Returns the random consistency index (RCI)of the matrix
	 * 
	 * @return
	 */
	public double getRandomConsistencyIndex() {
		switch (getRowDimension()) {
		case 2:
			return 0;
		case 3:
			return 0.58;
		case 4:
			return 0.9;
		case 5:
			return 1.12;
		case 6:
			return 1.24;
		case 7:
			return 1.32;
		case 8:
			return 1.41;
		case 9:
			return 1.45;
		case 10:
			return 1.49;
		case 11:
			return 1.51;
		}
		return 0;
	}

	/**
	 * Returns the maximum eigenvalue of the matrix
	 * 
	 * @return
	 */
	public double getMaximumEigenValue() {
		Matrix mat = getDiagonalEigenValueMatrix();
		double maxEigenValue = -10;
		for (int i = 0; i < mat.getRowDimension(); i++) {
			if (mat.get(i, i) > maxEigenValue)
				maxEigenValue = mat.get(i, i);
		}
		return maxEigenValue;
	}

	/**
	 * Returns the Diagonal EigenValue Matrix
	 * 
	 * @return
	 */
	public Matrix getDiagonalEigenValueMatrix() {
		return eig().getD();
	}

	/**
	 * Returns the EigenVector Matrix
	 * 
	 * @return
	 */
	public Matrix getEigenVectorMatrix() {
		return eig().getV();
	}

	/**
	 * Extends the matrix dimensions by n rows and n columns
	 * The entries of the matrix are preserved, and new entries are zeroes
	 * @param i
	 */
	public void extend(int n) {
		double[][] extended = new double[this.rowDimension+n][this.columnDimension+n];
		for (int i = 0; i < this.rowDimension; i++) {
			for (int j = 0; j < this.columnDimension; j++) {
				extended[i][j] = this.A[i][j];
			}
		}
		this.A = extended;
		this.rowDimension = this.rowDimension+n;
		this.columnDimension = this.columnDimension+n;
	}

	/**
	 * 
	 * @param firstRowToFill Initial row index
	 * @param lastRowToFill	Final row index
	 * @param columnToFill	Column index
	 * @param matrix	Matrix to insert
	 */
	public void setMatrixColumn(int firstRowToFill, int lastRowToFill, int columnToFill, Matrix matrix) {
		setMatrix(firstRowToFill, lastRowToFill, columnToFill, columnToFill, matrix);		
	}
	
	/**
	 * 
	 * @param rowToFill Row index
	 * @param firstColumnToFill Initial column index
	 * @param LastColumnToFill Final column index
	 * @param matrix Matrix to insert
	 */
	public void setMatrixRow(int rowToFill, int firstColumnToFill, int LastColumnToFill, Matrix matrix) {
		setMatrix(rowToFill, rowToFill, firstColumnToFill, LastColumnToFill, matrix);		
	}

	/**
	 * 
	 * @param requirementsPositions Array of row indices.
	 * @param k Column index
	 * @param mat Matrix to insert
	 */
	public void setMatrixColumn(int[] requirementsPositions, int k, Matrix mat) {
		int[] positions = {k};
		setMatrix(requirementsPositions, positions, mat);	
	}

}
