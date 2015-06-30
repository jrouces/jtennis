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


/**
 * Esta clase sustituye a la basada en JMF
 * TODO implementar
 * 
 * La clase Audio ofrece un servicio de reproducción de audio independiente de
 * la lógica del juego, basado en un constructor que realiza ciertas tareas
 * relacionadas con el rendimiento y unos métodos estáticos que permitan ser
 * llamados desde cualquier parte del código sin necesidad de una referencia.
 * Aunque se hable de un constructor, no existen atributos no estáticos y por
 * tanto es inútil guardar referencia a un objeto de esta clase, de modo que
 * este actúa como un método estático más
 * */
public class AudioFx {

	//private static URL u1, u2, u3, u4;
	//private static Player p1, p2, p3, p4;
	private static boolean silencio;

	/**
	 * Constructor cuya llamada es necesaria por lo menos una vez antes de
	 * cualquier uso de la clase
	 */
	public static void init() {
		try {
			
			// Class.getResource needs / prefix, ClassLoader.getResource not.
			ClassLoader classLoader = AudioFx.class.getClassLoader();
						
//			URL u1 = classLoader.getResource("resources/click1.wav");
//			p1 = Manager.createRealizedPlayer(u1);
//			URL u2 = new File("./resources/click2.wav").toURI().toURL();
//			p2 = Manager.createRealizedPlayer(Manager.createDataSource(u2));
//			URL u3 = new File("./resources/pong1.wav").toURI().toURL();
//			p3 = Manager.createRealizedPlayer(Manager.createDataSource(u3));
//			URL u4 = new File("./resources/pong2.wav").toURI().toURL();
//			p4 = Manager.createRealizedPlayer(Manager.createDataSource(u4));
			
			
			silencio = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reproducción de pistas con selección mediante flag, y opción de volumen
	 * con rango 0-1 FLAGS: 1= click agudo 2= click grave 3= golpe agudo 4=golpe
	 * grave
	 */
	public static void reproduce(int identificador, double intensidad) {
		try {
			if (silencio == false) {
				switch (identificador) {
				case 1:
					//play(p1, intensidad);
					break;
				case 2:
					//play(p2, intensidad);
					break;
				case 3:
					//play(p3, intensidad);
					break;
				default:
					//play(p4, intensidad);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	/**
//	 * Plays and rewinds.
//	 * 
//	 * @param player
//	 * @param intensidad
//	 */
//	private static void play(Player player, double intensidad) {
//		GainControl controlVol = player.getGainControl();
//		// Divide by two to avoid exceeding the limit
//		controlVol.setLevel((float) intensidad / 2);
//		player.start();
//		player.setMediaTime(new Time(0));
//	}

	/**
	 * Método de alternancia entre el estado de silencio y el de reproducción
	 * normal
	 */
	public static void cambiaSilencio() {
		silencio = !silencio;
	}

}





