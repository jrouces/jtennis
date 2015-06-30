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
 * La clase Vect2 ofrece la posibilidad de crear instancias que representen
 * vectores en el plano euclídeo, con sus operaciones elementales de suma,
 * multiplicación por escalar y cálculo de norma.
 * 
 */
public class Vect2 implements Vector {

	protected double x, y;

	/** Constructor con paso de coordenadas */
	public Vect2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double dameX() {
		return x;
	}

	public double dameY() {
		return y;
	}

	public void ponX(double x) {
		this.x = x;
	}

	public void ponY(double y) {
		this.y = y;
	}

	/** Método estático de suma */
	public static Vect2 suma(Vect2 uno, Vect2 otro) {
		/* Lo hacemos estático para que la representación sea más simétrica
		y porque aunque parece más sencillo implementarlo como método
		que se invoque como un sumando pasando el otro como parámetro,
		no es natural si se tiene en cuenta que la suma pertenece
		a la propia estructura de vectores, representada aquí por la clase. */
		return new Vect2(uno.x + otro.x, uno.y + otro.y);
	}

	/** Método estático de multiplicación por escalar */
	public static Vect2 mult(double mult, Vect2 v) {
		/* Para la multiplicación por escalar sí podría ser más cómodo a la vista
		el uso de un método de clase invocado sobre el vector a multiplicar y con
		el escalar como parámetro, pero lo hacemos estático por evitar confusiones
		con el anterior. */
		return new Vect2(v.dameX() * mult, v.dameY() * mult);
	}

	/** Método que devuelve la norma del vector sobre el que se invoca */
	public double norma() {
		/* Aquí no lo hacemos estático porque existe la posibilidad de librarse de
		todos los parámetros, y no es una operación análoga a las anteriores */
		return Math.sqrt(Math.pow(dameX(), 2) + Math.pow(dameY(), 2));
	}

	//*****************************************************************************
	//***********************  MÉTODOS DE PRUEBA  *********************************
	//*****************************************************************************

	public void imprimeVector() {

		System.out.println(" ");
		System.out.print(x + "  " + y);
		System.out.println(" ");

	}

}
