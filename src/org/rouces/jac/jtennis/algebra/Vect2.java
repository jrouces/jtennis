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
 * vectores en el plano eucl�deo, con sus operaciones elementales de suma,
 * multiplicaci�n por escalar y c�lculo de norma.
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

	/** M�todo est�tico de suma */
	public static Vect2 suma(Vect2 uno, Vect2 otro) {
		/* Lo hacemos est�tico para que la representaci�n sea m�s sim�trica
		y porque aunque parece m�s sencillo implementarlo como m�todo
		que se invoque como un sumando pasando el otro como par�metro,
		no es natural si se tiene en cuenta que la suma pertenece
		a la propia estructura de vectores, representada aqu� por la clase. */
		return new Vect2(uno.x + otro.x, uno.y + otro.y);
	}

	/** M�todo est�tico de multiplicaci�n por escalar */
	public static Vect2 mult(double mult, Vect2 v) {
		/* Para la multiplicaci�n por escalar s� podr�a ser m�s c�modo a la vista
		el uso de un m�todo de clase invocado sobre el vector a multiplicar y con
		el escalar como par�metro, pero lo hacemos est�tico por evitar confusiones
		con el anterior. */
		return new Vect2(v.dameX() * mult, v.dameY() * mult);
	}

	/** M�todo que devuelve la norma del vector sobre el que se invoca */
	public double norma() {
		/* Aqu� no lo hacemos est�tico porque existe la posibilidad de librarse de
		todos los par�metros, y no es una operaci�n an�loga a las anteriores */
		return Math.sqrt(Math.pow(dameX(), 2) + Math.pow(dameY(), 2));
	}

	//*****************************************************************************
	//***********************  M�TODOS DE PRUEBA  *********************************
	//*****************************************************************************

	public void imprimeVector() {

		System.out.println(" ");
		System.out.print(x + "  " + y);
		System.out.println(" ");

	}

}
