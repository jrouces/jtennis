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
 * El interfaz Vector define los métodos públicos no estáticos comunes entre
 * todas las clases que representen tipos de vector, excluyendo a los dame y
 * pon. Esto es así porque se considera que aunque haya clases que compartan
 * algunos de estos, los hay que no pertenecen a todas, y esa discriminación no
 * parece aconsejable.
 * 
 */
interface Vector {

	/** Método que devuelve la norma del vector sobre el que se invoca */
	public double norma();

	// En posibles extensiones futuras podrían definirse otros.

}
