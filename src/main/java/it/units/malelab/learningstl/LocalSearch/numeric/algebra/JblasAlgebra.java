package it.units.malelab.learningstl.LocalSearch.numeric.algebra;

import org.jblas.*;
import org.jblas.Decompose.LUDecomposition;
import org.jblas.exceptions.LapackPositivityException;

public class JblasAlgebra implements IAlgebra {

    @Override
    public IMatrix createMatrix(double[] data) {
        return new MatrixJblas(new DoubleMatrix(data));
    }

    @Override
    public IMatrix createMatrix(double[][] data) {
        return new MatrixJblas(new DoubleMatrix(data));
    }

    @Override
    public IMatrix createZeros(int n, int m) {
        return new MatrixJblas(DoubleMatrix.zeros(n, m));
    }

    @Override
    public IMatrix createOnes(int n, int m) {
        return new MatrixJblas(DoubleMatrix.ones(n, m));
    }

    @Override
    public IMatrix createEye(int n) {
        return new MatrixJblas(DoubleMatrix.eye(n));
    }

    @Override
    public IMatrix createDiag(double[] data) {
        return new MatrixJblas(DoubleMatrix.diag(new DoubleMatrix(data)));
    }

    @Override
    public IMatrix invert(IMatrix arg) {
        return solve(arg, createEye(arg.getRows()));
    }

    @Override
    public IMatrix invertPositive(IMatrix arg) throws NonPosDefMatrixException {
        return solvePositive(arg, createEye(arg.getRows()));
    }

    @Override
    public IMatrix solve(IMatrix A, IMatrix B) {
        final DoubleMatrix a = ((MatrixJblas) A).getMatrixObject();
        final DoubleMatrix b = ((MatrixJblas) B).getMatrixObject();
        final DoubleMatrix solution = Solve.solve(a, b);
        return new MatrixJblas(solution);
    }

    @Override
    public IMatrix solvePositive(IMatrix A, IMatrix B)
            throws NonPosDefMatrixException {
        // The 'dposv' function writes the solution on 'b'
        final DoubleMatrix a = ((MatrixJblas) A).getMatrixObject().dup();
        final DoubleMatrix b = ((MatrixJblas) B).getMatrixObject().dup();
        try {
            NativeBlas.dposv('U', a.rows, b.columns, a.data, 0, a.rows, b.data,
                    0, b.rows);
        } catch (LapackPositivityException e) {
            throw new NonPosDefMatrixException();
        }
        return new MatrixJblas(b);
    }

    @Override
    public void solvePositiveInPlace(IMatrix A, IMatrix B)
            throws NonPosDefMatrixException {
        // The 'dposv' function writes the solution on 'b'
        // and the Cholesky factorisation on 'a'
        // (only if 'a' is the upper part of the original positive definite
        // matrix)
        final DoubleMatrix a = ((MatrixJblas) A).getMatrixObject();
        final DoubleMatrix b = ((MatrixJblas) B).getMatrixObject();
        for (int i = 0; i < a.rows; i++)
            for (int j = 0; j < i; j++)
                a.put(i, j, 0);
        try {
            NativeBlas.dposv('U', a.rows, b.columns, a.data, 0, a.rows, b.data,
                    0, b.rows);
        } catch (LapackPositivityException e) {
            throw new NonPosDefMatrixException();
        }
    }

    @Override
    public IMatrix cholesky(IMatrix arg) throws NonPosDefMatrixException {
        final DoubleMatrix mat = ((MatrixJblas) arg).getMatrixObject();
        try {
            return new MatrixJblas(Decompose.cholesky(mat));
        } catch (Exception e) {
            throw new NonPosDefMatrixException();
        }
    }

    @Override
    public IMatrix[] svd(IMatrix arg) {
        final DoubleMatrix mat = ((MatrixJblas) arg).getMatrixObject();
        final IMatrix[] usv = new IMatrix[3];
        final DoubleMatrix[] results = Singular.fullSVD(mat);
        for (int i = 0; i < usv.length; i++)
            usv[i] = new MatrixJblas(results[i]);
        return usv;
    }

    @Override
    public double determinant(IMatrix A) {
        final DoubleMatrix a = ((MatrixJblas) A).getMatrixObject();
        LUDecomposition<DoubleMatrix> lu = Decompose.lu(a);
        // det(A) = det(L) * det(U) * det(P)
        return lu.l.diag().prod() * lu.u.diag().prod() * lu.p.diag().prod();
    }

}
