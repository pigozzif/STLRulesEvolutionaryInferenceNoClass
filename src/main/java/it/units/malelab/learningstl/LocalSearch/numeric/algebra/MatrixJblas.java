package it.units.malelab.learningstl.LocalSearch.numeric.algebra;

import org.jblas.DoubleMatrix;
import org.jblas.Singular;

/**
 * Created by ssilvetti on 05/01/17.
 */

final class MatrixJblas implements IMatrix {

    final private DoubleMatrix matrixObject;

    protected MatrixJblas(DoubleMatrix matrixObject) {
        this.matrixObject = matrixObject;
    }

    public DoubleMatrix getMatrixObject() {
        return matrixObject;
    }

    @Override
    public String toString() {
        return matrixObject.toString();
    }

    @Override
    public double[] getData() {
        return matrixObject.data;
    }

    @Override
    public int getRows() {
        return matrixObject.rows;
    }

    @Override
    public int getColumns() {
        return matrixObject.columns;
    }

    @Override
    public int getLength() {
        return matrixObject.length;
    }

    @Override
    public double get(int i, int j) {
        return matrixObject.get(i, j);
    }

    @Override
    public void put(int i, int j, double v) {
        matrixObject.put(i, j, v);
    }

    @Override
    public double get(int i) {
        return matrixObject.get(i);
    }

    @Override
    public void put(int i, double v) {
        matrixObject.put(i, v);
    }

    @Override
    public IMatrix getColumn(int i) {
        return new MatrixJblas(matrixObject.getColumn(i));
    }

    @Override
    public void putColumn(int i, IMatrix col) {
        matrixObject.putColumn(i, ((MatrixJblas) col).getMatrixObject());
    }

    @Override
    public IMatrix getRow(int i) {
        return new MatrixJblas(matrixObject.getRow(i));
    }

    @Override
    public void putRow(int i, IMatrix row) {
        matrixObject.putRow(i, ((MatrixJblas) row).getMatrixObject());
    }

    @Override
    public void copy(IMatrix arg) {
        matrixObject.copy(((MatrixJblas) arg).matrixObject);
    }

    @Override
    public IMatrix duplicate() {
        return new MatrixJblas(matrixObject.dup());
    }

    @Override
    public IMatrix transpose() {
        return new MatrixJblas(matrixObject.transpose());
    }

    @Override
    public int rank() {
        final DoubleMatrix v = Singular.SVDValues(matrixObject);
        if (!v.isVector())
            throw new IllegalStateException();
        final double machineEpsilon = Math.ulp(1.0);
        int nonzero = 0;
        for (int i = 0; i < v.getLength(); i++)
            if (Math.abs(v.get(i)) > machineEpsilon)
                nonzero++;
        return nonzero;
    }

    @Override
    public IMatrix add(IMatrix arg) {
        return add((MatrixJblas) arg);
    }

    public IMatrix add(MatrixJblas arg) {
        return new MatrixJblas(matrixObject.add(arg.matrixObject));
    }

    @Override
    public IMatrix add(double arg) {
        return new MatrixJblas(matrixObject.add(arg));
    }

    @Override
    public IMatrix sub(IMatrix arg) {
        return sub((MatrixJblas) arg);
    }

    public IMatrix sub(MatrixJblas arg) {
        return new MatrixJblas(matrixObject.sub(arg.matrixObject));
    }

    @Override
    public IMatrix sub(double arg) {
        return new MatrixJblas(matrixObject.sub(arg));
    }

    @Override
    public IMatrix rsub(IMatrix arg) {
        return rsub((MatrixJblas) arg);
    }

    public IMatrix rsub(MatrixJblas arg) {
        return new MatrixJblas(matrixObject.rsub(arg.matrixObject));
    }

    @Override
    public IMatrix rsub(double arg) {
        return new MatrixJblas(matrixObject.rsub(arg));
    }

    @Override
    public IMatrix mul(IMatrix arg) {
        return mul((MatrixJblas) arg);
    }

    private IMatrix mul(MatrixJblas arg) {
        return new MatrixJblas(matrixObject.mul(arg.matrixObject));
    }

    @Override
    public IMatrix mul(double arg) {
        return new MatrixJblas(matrixObject.mul(arg));
    }

    @Override
    public IMatrix mmul(IMatrix arg) {
        return mmul((MatrixJblas) arg);
    }

    public IMatrix mmul(MatrixJblas arg) {
        return new MatrixJblas(matrixObject.mmul(arg.matrixObject));
    }

    @Override
    public double dot(IMatrix arg) {
        return dot((MatrixJblas) arg);
    }

    public double dot(MatrixJblas arg) {
        return matrixObject.dot(arg.matrixObject);
    }

    @Override
    public IMatrix div(IMatrix arg) {
        return div((MatrixJblas) arg);
    }

    public IMatrix div(MatrixJblas arg) {
        return new MatrixJblas(matrixObject.div(arg.matrixObject));
    }

    @Override
    public IMatrix div(double arg) {
        return new MatrixJblas(matrixObject.div(arg));
    }

    @Override
    public IMatrix rdiv(IMatrix arg) {
        return rdiv((MatrixJblas) arg);
    }

    public IMatrix rdiv(MatrixJblas arg) {
        return new MatrixJblas(matrixObject.rdiv(arg.matrixObject));
    }

    @Override
    public IMatrix rdiv(double arg) {
        return new MatrixJblas(matrixObject.rdiv(arg));
    }

    @Override
    public IMatrix neg() {
        return new MatrixJblas(matrixObject.neg());
    }

    @Override
    public double sum() {
        return matrixObject.sum();
    }

    @Override
    public IMatrix diag() {
        return new MatrixJblas(matrixObject.diag());
    }

    @Override
    public IMatrix repmat(int rows, int columns) {
        return new MatrixJblas(matrixObject.repmat(rows, columns));
    }

    @Override
    public double max() {
        return matrixObject.max();
    }

    @Override
    public double min() {
        return matrixObject.min();
    }

    @Override
    public IMatrix sort() {
        return new MatrixJblas(matrixObject.sort());
    }
}