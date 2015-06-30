/*******************************************************************************
 * Copyright (c) 2007 Jacobo Rouces <jacobo@rouces.org>.
 * 
 * This file is part of JTennis.
 * 
 * JTennis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JTennis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 *  along with JTennis.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.rouces.jac.jtennis.algebra;

/**
 * La clase Matr3 ofrece la posibilidad de crear instancias con estructura de
 * matriz double 3x3, con métodos que implementan las operaciones de inversión y
 * multiplicación por vector. Se opera con vectores columna.
 * 
 */
public class Matr3 implements Cloneable {

	private final static int N = 3;
	private double[][] matriz = new double[N][N]; //[número de fila] [número de columna]

	/** Constructor por vectores columna */
	public Matr3(Vect3 columna1, Vect3 columna2, Vect3 columna3) {

		matriz[0][0] = columna1.dameX();
		matriz[1][0] = columna1.dameY();
		matriz[2][0] = columna1.dameZ();
		matriz[0][1] = columna2.dameX();
		matriz[1][1] = columna2.dameY();
		matriz[2][1] = columna2.dameZ();
		matriz[0][2] = columna3.dameX();
		matriz[1][2] = columna3.dameY();
		matriz[2][2] = columna3.dameZ();
		/* no lo hacemos con doble blucle porque no conocemos un modo simple de construir
		llamadas genéricas del estilo 'columnai' */

	}

	/**
	 * Método que devuelve el vector resultante de la multiplicación de la
	 * matriz por el vector de entrada
	 */
	public Vect3 multiplicaPorVector(Vect3 vector) {

		double[] resultado = new double[N];

		for (int i = 0; i < N; i++) {
			resultado[i] = matriz[i][0] * vector.dameX() + matriz[i][1] * vector.dameY() + matriz[i][2] * vector.dameZ();
		}
		/* lo hacemos con un solo bucle por un motivo similar al del constructor*/

		return new Vect3(resultado[0], resultado[1], resultado[2]);
	}

//	/**
//	 * Método que devuelve la matriz inversa. No maneja los errores n/0
//	 * derivados de introducir matrices no singulares
//	 */
//	public Matr3 inversa() {
//		try {
//			Matr3 original = (Matr3) this.clone();
//
//			Matr3 identidad = new Matr3(new Vect3(1, 0, 0), new Vect3(0, 1, 0), new Vect3(0, 0, 1));
//
//			for (int i = 0; i < N; i++) { //bucle cuyo índice selecciona columnas donde se hacen ceros
//
//				double pivote = original.dameCoeficiente(i, i);
//				original.multiplicaFila(i, 1 / pivote);
//				identidad.multiplicaFila(i, 1 / pivote);
//
//				for (int j = i + 1; j < N; j++) { //bucle que hace ceros
//					double factor = original.dameCoeficiente(j, i);
//					original.sumaFila(j, i, -factor);
//					identidad.sumaFila(j, i, -factor);
//				}
//			}
//			/* ya hemos transformado la matriz original
//			en una triangular superior con unos en la diagonal */
//
//			for (int i = N - 1; i >= 0; i--) { //bucle cuyo índice selecciona columnas donde se hacen ceros
//
//				for (int j = i - 1; j >= 0; j--) {
//					double factor = original.dameCoeficiente(j, i);
//					original.sumaFila(j, i, -factor);
//					identidad.sumaFila(j, i, -factor);
//				}
//
//			}
//
//			/* ya hemos transformado la matriz original
//			en la identidad, luego la identidad original
//			es la inversa de la matriz original*/
//
//			return identidad;
//
//		} catch (Exception e) {
//		}
//		return null;
//	}
	
	/**
	 * Método que devuelve la matriz traspuesta
	 */
	public Matr3 traspuesta() {
		return new Matr3(
				new Vect3(matriz[0][0], matriz[0][1], matriz[0][2]), 
				new Vect3(matriz[1][0], matriz[1][1], matriz[1][2]), 
				new Vect3(matriz[2][0], matriz[2][1], matriz[2][2]));

	}

	public double dameCoeficiente(int fila, int columna) {
		return matriz[fila][columna];
	}

	private void multiplicaFila(int fila, double factor) {
		for (int i = 0; i < N; i++) {
			matriz[fila][i] = matriz[fila][i] * factor;
		}
	}

	private void sumaFila(int filaSumada, int filaSumadora, double factor) {
		for (int i = 0; i < N; i++) {
			matriz[filaSumada][i] += matriz[filaSumadora][i] * factor;
		}
	}

	//*****************************************************************************
	//***********************  MÉTODOS DE PRUEBA  *********************************
	//*****************************************************************************

	public void imprimeMatriz() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++)
				System.out.print(matriz[i][j] + "  ");
			System.out.println(" ");
			System.out.println(" ");
		}
		System.out.println(" ");
	}

}
