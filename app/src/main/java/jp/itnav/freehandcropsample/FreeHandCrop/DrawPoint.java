package jp.itnav.freehandcropsample.FreeHandCrop;

import android.graphics.Matrix;
import android.graphics.Point;

public class DrawPoint {
    Matrix Imatrix;
    Point controlPoint = new Point(-1, -1);
    Matrix matrix;
    /* renamed from: x */
    int f9x = 0;
    /* renamed from: y */
    int f10y = 0;

    public DrawPoint(Matrix matrix, Matrix Imatrix, int x, int y) {
        setMatrix(matrix);
        setX(x);
        setY(y);
        setIMatrix(Imatrix);
    }

    public DrawPoint(Matrix matrix, Matrix Imatrix, int x, int y, Point controlPoint) {
        setMatrix(matrix);
        setX(x);
        setY(y);
        setIMatrix(Imatrix);
        setcontrolPoint(controlPoint);
    }

    public Point getcontrolPoint() {
        return this.controlPoint;
    }

    public void setcontrolPoint(Point cpoint) {
        this.controlPoint = cpoint;
    }

    public Matrix getMatrix() {
        return this.matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Matrix getIMatrix() {
        return this.Imatrix;
    }

    public void setIMatrix(Matrix Imatrix) {
        this.Imatrix = Imatrix;
    }

    public int getX() {
        return this.f9x;
    }

    public void setX(int x) {
        this.f9x = x;
    }

    public int getY() {
        return this.f10y;
    }

    public void setY(int y) {
        this.f10y = y;
    }
}
