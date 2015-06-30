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
package org.rouces.jac.jtennis.main;

import javax.media.*;
import javax.media.protocol.*;
import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * La clase Audio ofrece un servicio de reproducci�n de audio independiente de
 * la l�gica del juego, basado en un constructor que realiza ciertas tareas
 * relacionadas con el rendimiento y unos m�todos est�ticos que permitan ser
 * llamados desde cualquier parte del c�digo sin necesidad de una referencia.
 * Aunque se hable de un constructor, no existen atributos no est�ticos y por
 * tanto es in�til guardar referencia a un objeto de esta clase, de modo que
 * este act�a como un m�todo est�tico m�s
 * */
public class Audio {

	private static Player p1, p2, p3, p4;
	private static boolean silencio;

	/**
	 * Constructor cuya llamada es necesaria por lo menos una vez antes de
	 * cualquier uso de la clase
	 */
	public static void init() {
		try {
			
			// Class.getResource needs / prefix, ClassLoader.getResource not.
			ClassLoader classLoader = Audio.class.getClassLoader();
			URL u1 = classLoader.getResource("resources/click1.wav");
			// Parece que JMF no carga bien los archivos dentro del JAR. Pero el PNG de la intro s� carga bien.
			//System.out.println(u1);
			p1 = Manager.createRealizedPlayer(u1);
			URL u2 = classLoader.getResource("resources/click2.wav");
			p2 = Manager.createRealizedPlayer(u2);
			URL u3 = classLoader.getResource("resources/pong1.wav");
			p3 = Manager.createRealizedPlayer(u3);
			URL u4 = classLoader.getResource("resources/pong2.wav");
			p4 = Manager.createRealizedPlayer(u4);
			
			silencio = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reproducci�n de pistas con selecci�n mediante flag, y opci�n de volumen
	 * con rango 0-1 FLAGS: 1= click agudo 2= click grave 3= golpe agudo 4=golpe
	 * grave
	 */
	public static void reproduce(int identificador, double intensidad) {
		try {
			if (silencio == false) {
				switch (identificador) {
				case 1:
					play(p1, intensidad);
					break;
				case 2:
					play(p2, intensidad);
					break;
				case 3:
					play(p3, intensidad);
					break;
				default:
					play(p4, intensidad);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Plays and rewinds.
	 * 
	 * @param player
	 * @param intensidad
	 */
	private static void play(Player player, double intensidad) {
		GainControl controlVol = player.getGainControl();
		// Divide by two to avoid exceeding the limit
		controlVol.setLevel((float) intensidad / 2);
		player.start();
		player.setMediaTime(new Time(0));
	}

	/**
	 * M�todo de alternancia entre el estado de silencio y el de reproducci�n
	 * normal
	 */
	public static void cambiaSilencio() {
		silencio = !silencio;
	}

}





