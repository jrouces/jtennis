JTennis - A Java tennis game with a very simple 3D engine.
=========================================================================

Written by Jacobo Rouces <jacobo@rouces.org>.

This is a Tennis game that I wrote in Java when I was a student at a programming 101 course. It is very simple but can be fun to play or tweak, so years later I have decided to release it under a GPL v3 license. Please be aware that the code is very naive and inefficient (when I wrote it, I didn't know what a balanced binary tree was), and it is entirely written in Spanish. 

The main class is Ventana. The only dependency is JMF. It is tested on Java 1.6.

3D engine
---------

It implements a very simple 3d engine from scratch, which was never meant to be efficient but rather showcase OOP concepts applied to basic linear algebra concepts. 

Control
-------

The player movements are controlled with typical arrow keys and the ball, when hit, is sent with a speed vector that is a weighted sum of the incoming speed vector and the 3D force vector produced, the latter being obtained from the 2D speed vector of the mouse in the moment of the impact, composed with a tilt angle that can be controlled with the mouse wheel at any point of the game (so it is a bit like hitting the ball with the mouse while holding it on a tilted surface that is controled with the scroll wheel). There is a constant but rudimentary visual feedback for these components. 

The method was meant to provide richer control than old-fashioned combinations of buttons, and exploit the analog nature of the mouse, while not needing any complex 3D input device. It is the part of the game that I considered really innovative. I am not an expert in computer games, but during my occasional experience in the nineties and early 2000s, I did not encounter a similar control system.

Artificial "Intelligence"
------------------------

The machine knows the physics of the ball and can predict its deterministic trajectory assuming no further intervention from a human player. Different levels of difficulty add different levels of noise to impair this "cognitive" ability from the machine, but its physical abilities remain at the same level as the human player's. In the hardest mode, where the noise introduced is very small, the machine becomes practically impossible to beat.
