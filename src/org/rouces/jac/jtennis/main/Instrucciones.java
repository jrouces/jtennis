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
 * La clase instrucciones sirve para que su constructor añada un panel con
 * instrucciones y un botón de salida sobre la ventana cuya referencia se pasa
 * como parámetro
 */
public class Instrucciones {

	JFrame ventana;
	JPanel panelInstrucciones, panelTexto, panelBoton, panelTitulo;
	JButton botonVolver;
	JLabel etiqueta;
	JLabel titulo;

	/** Constructor de instrucciones, única lógica de la clase */
	public Instrucciones(JFrame ventanaa) {

		this.ventana = ventanaa;

		panelInstrucciones = new JPanel();
		panelInstrucciones.setLayout(new BoxLayout(panelInstrucciones, BoxLayout.Y_AXIS));

		panelTexto = new JPanel();
		panelBoton = new JPanel();
		panelTitulo = new JPanel();

		botonVolver = new JButton("VOLVER");
		botonVolver.setPreferredSize(new Dimension(100, 50));
		botonVolver.setContentAreaFilled(false);

		//Clase anónima escuchadora del único botón, que borra lo crado y vuelve a construír un menú
		botonVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Audio.reproduce(2, 0.5);
				ventana.getContentPane().remove(panelInstrucciones);
				new Menu(ventana);
			}
		});

		etiqueta = new JLabel

		(
				"<html><center>   MUEVE A TU JUGADOR CON LAS TECLAS A-S-D-W <br> O CON LAS CLÁSICAS DE DIRECCIÓN  <center><br><br><center>   CAMBIA LA PENDIENTE DE LANZAMIENTO DE LA PELOTA CON <br> EL SCROLL DEL RATÓN O SUS DOS BOTONES.   </center><br><br><center>   AJUSTA EL VECTOR DE LANZAMIENTO MOVIENDO EL RATÓN. <br> TARDARÁS UN POCO EN COGER PRÁCTICA   </center><br><br><center>   P = PAUSA &#09; K = SALIR &#09; M = MUTE &#09; +/- = MOVER CÁMARA   </center><br>     </html>");

		etiqueta.setFont(new Font("Arial Rounded MT Bold", Font.TRUETYPE_FONT, 18));

		titulo = new JLabel("JTenis");
		titulo.setFont(new Font("Copperplate Gothic Bold", Font.TRUETYPE_FONT, 50));

		panelTitulo.add(titulo);
		panelTexto.add(etiqueta);
		panelBoton.add(botonVolver);

		panelInstrucciones.add(Box.createRigidArea(new Dimension(50, 10)));
		panelInstrucciones.add(panelTitulo);
		panelInstrucciones.add(Box.createRigidArea(new Dimension(50, 10)));
		panelInstrucciones.add(panelTexto);
		panelInstrucciones.add(panelBoton);

		ventana.add(panelInstrucciones);
		ventana.setVisible(true);

	}

}
