package fr.tp.inf112.projects.robotsim.infrasturcture;

import fr.tp.inf112.projects.canvas.controller.Observer;

public interface FactoryModelChangedNotifier {
	void notifyObservers();
	boolean addObserver(Observer observer);
	boolean removeObserver(Observer observer);
}
