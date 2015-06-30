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
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

/**
 * Clase main, cuya instancia representará la ventana del programa a lo largo de
 * toda su evolución
 * 
 * 
 * TODO Add defaults
 * FIXME Externalize strings and add English
 * TODO Implement as applet?
 * TODO Resolver que el JMF no carga el WAV en el JAR.
 */
public class Ventana extends JFrame {

	//public static Color colorFondoGeneral = new Color(240,240,240);

	public Ventana() {
		super("JTenis");
		setSize(800, 600);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		//Audio.init(); // El audio introduce algunos cuelgues que reducen fluidez al juego. Solución no encontrada todavía :(
	}

	/** Pantalla de introducción, con degradado final */
	public void intro() {

		try {

			JPanel panelIntro = new JPanel();
			ClassLoader classLoader = getClass().getClassLoader();
			URL url = classLoader.getResource("resources/introTenis.png");
			//System.out.println(url);
			ImageIcon imagenIntro = new ImageIcon(url);
			//ImageIcon imagenIntro = new ImageIcon("./resources/introTenis.png");
			JLabel etiqueta = new JLabel(imagenIntro);
			etiqueta.setOpaque(true);

			panelIntro.add(etiqueta);
			getContentPane().add(panelIntro);
			setVisible(true);

			Thread.sleep(2000);

			// Fade progresivo. Pero borra la imagen.
			/*
			JPanel mascara = new JPanel();
			mascara.setBackground(new Color(238, 238, 238, 0));
			getContentPane().add(mascara);
			for (int i = 1; i < 180; i++) {
				Thread.sleep(6);
				panelIntro.setForeground(new Color(238, 238, 238, i));
				setVisible(true);
			}
			*/
			
			getContentPane().removeAll();
			//setVisible(true);

		} catch (Exception e) {
		}

	}

	/** Método main que lanza el programa */
	public static void main(String[] args) {

		Ventana ventana = new Ventana();
		ventana.intro();
		new Menu(ventana);

		// ejecuciones instantáneas
		//new Partida(1,2,3,ventana);

	}

}
