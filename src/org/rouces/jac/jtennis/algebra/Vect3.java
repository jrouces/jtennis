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
 * La clase Vect3 extiende Vect2 y ofrece métodos análogos para la
 * representación de vectores en un espacio euclídeo de tres dimensiones.
 * 
 * 
 */
public class Vect3 extends Vect2 implements Vector {

	private double z;

	/** Constructor con paso de coordenadas */
	public Vect3(double x, double y, double z) {
		super(x, y);
		this.z = z;
	}

	public double dameZ() {
		return z;
	}

	public void ponZ(double z) {
		this.z = z;
	}

	/** Método estático de suma */
	public static Vect3 suma(Vect3 uno, Vect3 otro) {
		// Ver comentarios en método análogo de Vect2
		return new Vect3(uno.dameX() + otro.dameX(), uno.dameY() + otro.dameY(), uno.dameZ() + otro.dameZ());
	}

	/** Método estático de multiplicación por escalar */
	public static Vect3 mult(double mult, Vect3 v) {
		// Ver comentarios en método análogo de Vect2
		return new Vect3(v.dameX() * mult, v.dameY() * mult, v.dameZ() * mult);
	}

	/** Método que devuelve la norma del vector sobre el que se invoca */
	public double norma() {
		// Ver comentarios en método análogo de Vect2
		return Math.sqrt(Math.pow(dameX(), 2) + Math.pow(dameY(), 2) + Math.pow(dameZ(), 2));
	}

	//*****************************************************************************
	//***********************  MÉTODOS DE PRUEBA  *********************************
	//*****************************************************************************

	public void imprimeVector() {

		System.out.println(" ");
		System.out.print(x + "  " + y + "  " + z);
		System.out.println(" ");

	}

}
