package ch.epfl.javanco.utils;

import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.javanco.xml.NetworkAttribute;

public class JavancoMatrixes {

	public static Matrix<Integer> toIntegerMatrix(Matrix<NetworkAttribute> matrix) {
		Matrix<Integer> result = new Matrix<Integer>(matrix.size());
		for (int i = 0 ; i < matrix.size() ; i++) {
			for (int j = 0 ; j < matrix.size() ; j++) {
				NetworkAttribute at = matrix.getMatrixElement(i,j);
				if (at != null) {
					int value = at.intValue();
					result.setMatrixElement(i,j,value);
				}
			}
		}
		return result;
	}

	public static int[][] toIntMatrix(Matrix<NetworkAttribute> matrix) {
		int[][] result = new int[matrix.size()][matrix.size()];
		for (int i = 0 ; i < matrix.size() ; i++) {
			for (int j = 0 ; j < matrix.size() ; j++) {
				NetworkAttribute at = matrix.getMatrixElement(i,j);
				if (at != null) {
					int value = at.intValue();
					result[i][j] = value;
				}
			}
		}
		return result;
	}

	public static Matrix<Float> toFloatingMatrix(Matrix<NetworkAttribute> matrix) {
		Matrix<Float> result = new Matrix<Float>(matrix.size());
		for (int i = 0 ; i < matrix.size() ; i++) {
			for (int j = 0 ; j < matrix.size() ; j++) {
				NetworkAttribute at = matrix.getMatrixElement(i,j);
				if (at != null) {
					float value = at.floatValue();
					result.setMatrixElement(i,j,value);
				}
			}
		}
		return result;
	}

	public static float[][] toFloatMatrix(Matrix<NetworkAttribute> matrix) {
		float[][] result = new float[matrix.size()][matrix.size()];
		for (int i = 0 ; i < matrix.size() ; i++) {
			for (int j = 0 ; j < matrix.size() ; j++) {
				NetworkAttribute at = matrix.getMatrixElement(i,j);
				if (at != null) {
					float value = at.floatValue();
					result[i][j] = value;
				}
			}
		}
		return result;
	}

	public static Matrix<Double> toDoubleMatrix(Matrix<NetworkAttribute> matrix) {
		Matrix<Double> result = new Matrix<Double>(matrix.size());
		for (int i = 0 ; i < matrix.size() ; i++) {
			for (int j = 0 ; j < matrix.size() ; j++) {
				NetworkAttribute at = matrix.getMatrixElement(i,j);
				if (at != null) {
					try {
						double value = at.doubleValue();
						result.setMatrixElement(i,j,value);
					}
					catch (NumberFormatException e) {}
				}
			}
		}
		return result;
	}

	public static double[][] todoubleMatrix(Matrix<NetworkAttribute> matrix) {
		double[][] result = new double[matrix.size()][matrix.size()];
		for (int i = 0 ; i < matrix.size() ; i++) {
			for (int j = 0 ; j < matrix.size() ; j++) {
				NetworkAttribute at = matrix.getMatrixElement(i,j);
				if (at != null) {
					double value = at.doubleValue();
					result[i][j] = value;
				}
			}
		}
		return result;
	}

	/*	public static void setSymmetry(Matrix<Number> mat) {
		for (int i = 0 ; i < matrix.size() ; i++) {
			for (int j = 0 ; j < matrix.size() ; j++) {
				Number n = mat.getMatrixElement(i,j);
				if (n.doubleValue() == 0) {

				}
			}
		}
	}*/
}
