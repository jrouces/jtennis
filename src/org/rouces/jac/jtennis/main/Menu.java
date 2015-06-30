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

/**
 * La clase Menu se encarga de todo lo relacionado con el menú previo del juego.
 */
public class Menu {

	static int numeroSets, tipoCampo, dificultad;
	JFrame ventana;
	JPanel panelMenu;
	JButton boton1Set, boton3Set, boton5Set, botonHierba, botonTierra, botonPista, botonFacil, botonMedia, botonDificil, botonJugar, botonSalir,
			botonInstrucciones;
	JButton botonMarcadoSets, botonMarcadoPista, botonMarcadoDificultad;
	Color marcado, desmarcado;

	/** Constructor de la clase */
	public Menu(JFrame ventana) {

		try {

			this.ventana = ventana;

			numeroSets = 0;
			tipoCampo = 0;
			dificultad = 0;

			panelMenu = new JPanel();
			//panelMenu.setBackground(Ventana.colorFondoGeneral);

			marcado = new Color(200, 200, 200);
			desmarcado = panelMenu.getBackground();

			//Panel del menu en modo ristra vertical
			panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));

			//Creo paneles, utilizando el flow horizontal por defecto
			JPanel panelTitulo = new JPanel();
			JPanel panelNumSets = new JPanel();
			JPanel panelBotonesNumSets = new JPanel();
			JPanel panelTipoPista = new JPanel();
			JPanel panelBotonesTipoPista = new JPanel();
			JPanel panelDificultad = new JPanel();
			JPanel panelBotonesDificultad = new JPanel();
			JPanel panelBotoneraAcciones = new JPanel();
			//panelBotoneraAcciones.setLayout(null);
			//panelBotoneraAcciones.setSize(800,100);

			//Declaro dimensiones de uso común
			Dimension dimensionBoton = new Dimension(150, 50);
			Dimension interBoton = new Dimension(5, 10);
			Dimension dimensionBot2 = new Dimension(100, 50);

			//Declaro manejador de todos los botones
			ManejadorBotones mj = new ManejadorBotones();

			//Creo el contenido de paneles horizontales

			JLabel titulo = new JLabel("JTenis");
			titulo.setFont(new Font("Copperplate Gothic Bold", Font.TRUETYPE_FONT, 50));

			JLabel etiquetaSet = new JLabel("Número de sets");
			etiquetaSet.setFont(new Font("Arial Rounded MT Bold", Font.TRUETYPE_FONT, 18));
			//etiquetaSet.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);

			boton1Set = new JButton("1");
			boton3Set = new JButton("3");
			boton5Set = new JButton("5");
			boton1Set.setFont(new Font("Arial", Font.BOLD, 25));
			boton3Set.setFont(new Font("Arial", Font.BOLD, 25));
			boton5Set.setFont(new Font("Arial", Font.BOLD, 25));
			boton1Set.setContentAreaFilled(false);
			boton3Set.setContentAreaFilled(false);
			boton5Set.setContentAreaFilled(false);
			boton1Set.setOpaque(true);
			boton3Set.setOpaque(true);
			boton5Set.setOpaque(true);
			boton1Set.setPreferredSize(dimensionBoton);
			boton3Set.setPreferredSize(dimensionBoton);
			boton5Set.setPreferredSize(dimensionBoton);
			boton1Set.setFocusPainted(false);
			boton3Set.setFocusPainted(false);
			boton5Set.setFocusPainted(false);
			boton1Set.addActionListener(mj);
			boton3Set.addActionListener(mj);
			boton5Set.addActionListener(mj);

			JLabel etiquetaPista = new JLabel("Tipo de pista");
			etiquetaPista.setFont(new Font("Arial Rounded MT Bold", Font.TRUETYPE_FONT, 18));

			botonHierba = new JButton("Hierba");
			botonTierra = new JButton("Tierra");
			botonPista = new JButton("Goma");
			botonHierba.setFont(new Font("Arial", Font.BOLD, 25));
			botonTierra.setFont(new Font("Arial", Font.BOLD, 25));
			botonPista.setFont(new Font("Arial", Font.BOLD, 25));
			botonHierba.setForeground(new Color(0, 160, 0));
			botonTierra.setForeground(new Color(140, 40, 40));
			botonPista.setForeground(new Color(0, 0, 220));
			botonHierba.setContentAreaFilled(false);
			botonTierra.setContentAreaFilled(false);
			botonPista.setContentAreaFilled(false);
			botonHierba.setOpaque(true);
			botonTierra.setOpaque(true);
			botonPista.setOpaque(true);
			botonHierba.setPreferredSize(dimensionBoton);
			botonTierra.setPreferredSize(dimensionBoton);
			botonPista.setPreferredSize(dimensionBoton);
			botonHierba.setFocusPainted(false);
			botonTierra.setFocusPainted(false);
			botonPista.setFocusPainted(false);

			botonHierba.addActionListener(mj);
			botonTierra.addActionListener(mj);
			botonPista.addActionListener(mj);

			JLabel etiquetaDificultad = new JLabel("Dificultad");
			etiquetaDificultad.setFont(new Font("Arial Rounded MT Bold", Font.TRUETYPE_FONT, 18));

			botonFacil = new JButton("Fácil");
			botonMedia = new JButton("Media");
			botonDificil = new JButton("Difícil");
			botonFacil.setFont(new Font("Arial", Font.PLAIN, 20));
			botonFacil.setForeground(new Color(100, 100, 100));
			botonMedia.setFont(new Font("Arial", Font.PLAIN, 20));
			botonDificil.setFont(new Font("Arial", Font.BOLD, 20));
			botonFacil.setContentAreaFilled(false);
			botonMedia.setContentAreaFilled(false);
			botonDificil.setContentAreaFilled(false);
			botonFacil.setOpaque(true);
			botonMedia.setOpaque(true);
			botonDificil.setOpaque(true);
			botonFacil.setPreferredSize(dimensionBoton);
			botonMedia.setPreferredSize(dimensionBoton);
			botonDificil.setPreferredSize(dimensionBoton);
			botonFacil.setFocusPainted(false);
			botonMedia.setFocusPainted(false);
			botonDificil.setFocusPainted(false);
			botonFacil.addActionListener(mj);
			botonMedia.addActionListener(mj);
			botonDificil.addActionListener(mj);

			botonJugar = new JButton("JUGAR");
			botonJugar.setContentAreaFilled(false);
			botonJugar.setPreferredSize(dimensionBoton);
			botonJugar.setFont(new Font("Arial", Font.BOLD, 18));
			botonJugar.setFocusPainted(false);
			botonJugar.addActionListener(mj);

			botonSalir = new JButton("SALIR");
			botonSalir.setPreferredSize(dimensionBot2);
			botonSalir.setContentAreaFilled(false);
			botonSalir.addActionListener(mj);

			botonInstrucciones = new JButton("?");
			botonInstrucciones.setPreferredSize(dimensionBot2);
			botonInstrucciones.setContentAreaFilled(false);
			botonInstrucciones.addActionListener(mj);

			//Termino de crear el contenido de los paneles horizontales

			//Añado el contenido de los paneles horizontales a los paneles horizontales.

			panelTitulo.add(titulo);

			panelNumSets.add(etiquetaSet);

			panelBotonesNumSets.add(boton1Set);
			panelBotonesNumSets.add(Box.createRigidArea(interBoton));
			panelBotonesNumSets.add(boton3Set);
			panelBotonesNumSets.add(Box.createRigidArea(interBoton));
			panelBotonesNumSets.add(boton5Set);

			panelTipoPista.add(etiquetaPista);

			panelBotonesTipoPista.add(botonHierba);
			panelBotonesTipoPista.add(Box.createRigidArea(interBoton));
			panelBotonesTipoPista.add(botonTierra);
			panelBotonesTipoPista.add(Box.createRigidArea(interBoton));
			panelBotonesTipoPista.add(botonPista);

			panelDificultad.add(etiquetaDificultad);

			panelBotonesDificultad.add(botonFacil);
			panelBotonesDificultad.add(Box.createRigidArea(interBoton));
			panelBotonesDificultad.add(botonMedia);
			panelBotonesDificultad.add(Box.createRigidArea(interBoton));
			panelBotonesDificultad.add(botonDificil);

			Dimension dimensionBot3 = new Dimension((int) ((800 - dimensionBot2.getWidth() * 2 - dimensionBoton.getWidth()) / 2.5), 10);
			panelBotoneraAcciones.add(botonSalir);
			panelBotoneraAcciones.add(Box.createRigidArea(dimensionBot3));
			panelBotoneraAcciones.add(botonJugar);
			panelBotoneraAcciones.add(Box.createRigidArea(dimensionBot3));
			panelBotoneraAcciones.add(botonInstrucciones);

			//Termino de añadir el contenido de los paneles horizontales a los paneles horizontales.

			//Añado los paneles hotizontales al panel vertical de menu

			panelMenu.add(Box.createRigidArea(new Dimension(50, 10)));
			panelMenu.add(panelTitulo);
			panelMenu.add(Box.createRigidArea(new Dimension(50, 10)));
			panelMenu.add(panelNumSets);
			panelMenu.add(panelBotonesNumSets);
			panelMenu.add(panelTipoPista);
			panelMenu.add(panelBotonesTipoPista);
			panelMenu.add(panelDificultad);
			panelMenu.add(panelBotonesDificultad);
			panelMenu.add(Box.createRigidArea(new Dimension(50, 40)));
			panelMenu.add(panelBotoneraAcciones);

			//Termino de añadir los paneles hotizontales al panel vertical de menu

			ventana.getContentPane().add(panelMenu);
			ventana.setVisible(true);

		} catch (Exception e) {
		}

	} //fin del constructor

	/** Clase interna que maneja todos los botones del menú */
	class ManejadorBotones implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			JButton boton = (JButton) e.getSource();
			String etiqueta = boton.getText();

			if (etiqueta == "SALIR")
				Audio.reproduce(2, 0.5);
			else
				Audio.reproduce(1, 0.5);

			if (etiqueta == "1") {
				numeroSets = 1;
				if (botonMarcadoSets != null)
					botonMarcadoSets.setBackground(desmarcado);
				botonMarcadoSets = boton1Set;
				botonMarcadoSets.setBackground(marcado);
			}
			if (etiqueta == "3") {
				numeroSets = 3;
				if (botonMarcadoSets != null)
					botonMarcadoSets.setBackground(desmarcado);
				botonMarcadoSets = boton3Set;
				botonMarcadoSets.setBackground(marcado);
			}
			if (etiqueta == "5") {
				numeroSets = 5;
				if (botonMarcadoSets != null)
					botonMarcadoSets.setBackground(desmarcado);
				botonMarcadoSets = boton5Set;
				botonMarcadoSets.setBackground(marcado);
			}

			if (etiqueta == "Hierba") {
				tipoCampo = 1;
				if (botonMarcadoPista != null)
					botonMarcadoPista.setBackground(desmarcado);
				botonMarcadoPista = botonHierba;
				botonMarcadoPista.setBackground(marcado);
			}
			if (etiqueta == "Tierra") {
				tipoCampo = 2;
				if (botonMarcadoPista != null)
					botonMarcadoPista.setBackground(desmarcado);
				botonMarcadoPista = botonTierra;
				botonMarcadoPista.setBackground(marcado);
			}
			if (etiqueta == "Goma") {
				tipoCampo = 3;
				if (botonMarcadoPista != null)
					botonMarcadoPista.setBackground(desmarcado);
				botonMarcadoPista = botonPista;
				botonMarcadoPista.setBackground(marcado);
			}

			if (etiqueta == "Fácil") {
				dificultad = 1;
				if (botonMarcadoDificultad != null)
					botonMarcadoDificultad.setBackground(desmarcado);
				botonMarcadoDificultad = botonFacil;
				botonMarcadoDificultad.setBackground(marcado);
			}
			if (etiqueta == "Media") {
				dificultad = 2;
				if (botonMarcadoDificultad != null)
					botonMarcadoDificultad.setBackground(desmarcado);
				botonMarcadoDificultad = botonMedia;
				botonMarcadoDificultad.setBackground(marcado);
			}
			if (etiqueta == "Difícil") {
				dificultad = 3;
				if (botonMarcadoDificultad != null)
					botonMarcadoDificultad.setBackground(desmarcado);
				botonMarcadoDificultad = botonDificil;
				botonMarcadoDificultad.setBackground(marcado);
			}

			if (etiqueta == "JUGAR") {
				if ((numeroSets != 0) && (tipoCampo != 0) && (dificultad != 0)) {
					//ventana.getContentPane().remove(panelMenu);
					ventana.getContentPane().removeAll();
					//ventana.repaint();
					new Partida(numeroSets, tipoCampo, dificultad, ventana);
				}
			}

			if (etiqueta == "SALIR") {
				System.exit(0);
				//Leo que use esto con cuidado, pero no sé...
			}
			if (etiqueta == "?") {
				ventana.getContentPane().remove(panelMenu);
				new Instrucciones(ventana);
			}

		}

	}

}
