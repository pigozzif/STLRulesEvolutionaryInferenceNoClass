package it.units.malelab.learningstl.LocalSearch.numeric.algebra;

public interface IMatrix {

    double[] getData();

    int getRows();

    int getColumns();

    int getLength();

    double get(int i, int j);

    void put(int i, int j, double v);

    double get(int i);

    void put(int i, double v);

    IMatrix getColumn(int i);

    void putColumn(int i, IMatrix column);

    IMatrix getRow(int i);

    void putRow(int i, IMatrix row);

    void copy(IMatrix arg);

    IMatrix duplicate();

    IMatrix transpose();

    int rank();

    IMatrix add(IMatrix arg);

    IMatrix add(double arg);

    IMatrix sub(IMatrix arg);

    IMatrix sub(double arg);

    IMatrix rsub(IMatrix arg);

    IMatrix rsub(double arg);

    IMatrix mul(IMatrix arg);

    IMatrix mul(double arg);

    IMatrix mmul(IMatrix arg);

    double dot(IMatrix arg);

    IMatrix div(IMatrix arg);

    IMatrix div(double arg);

    IMatrix rdiv(IMatrix arg);

    IMatrix rdiv(double arg);

    IMatrix neg();

    double sum();

    IMatrix diag();

    IMatrix repmat(int rows, int columns);

    double max();

    double min();

    IMatrix sort();

}
