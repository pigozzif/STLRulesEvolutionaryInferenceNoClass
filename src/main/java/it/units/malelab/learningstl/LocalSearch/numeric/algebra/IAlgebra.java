package it.units.malelab.learningstl.LocalSearch.numeric.algebra;

public interface IAlgebra {

    IMatrix createMatrix(double[] data);

    IMatrix createMatrix(double[][] data);

    IMatrix createZeros(int n, int m);

    IMatrix createOnes(int n, int m);

    IMatrix createEye(int n);

    IMatrix createDiag(double[] data);

    IMatrix invert(IMatrix arg);

    IMatrix invertPositive(IMatrix arg) throws NonPosDefMatrixException;

    IMatrix solve(IMatrix A, IMatrix B);

    IMatrix solvePositive(IMatrix A, IMatrix B)
            throws NonPosDefMatrixException;

    void solvePositiveInPlace(IMatrix A, IMatrix B)
            throws NonPosDefMatrixException;

    IMatrix cholesky(IMatrix arg) throws NonPosDefMatrixException;

    IMatrix[] svd(IMatrix arg);

    double determinant(IMatrix A);

}
