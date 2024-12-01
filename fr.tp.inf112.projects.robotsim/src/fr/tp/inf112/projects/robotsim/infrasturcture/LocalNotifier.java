package fr.tp.inf112.projects.robotsim.infrasturcture;

import java.util.LinkedList;
import java.util.List;

import fr.tp.inf112.projects.canvas.controller.Observer;

public class LocalNotifier implements FactoryModelChangedNotifier {
	
	private List<Observer> observers = new LinkedList<Observer>();

	@Override
	public void notifyObservers() {
		for (Observer observer : observers) {
			observer.modelChanged();
		}
	}

	@Override
	public boolean addObserver(Observer observer) {
		return observers.add(observer);
	}

	@Override
	public boolean removeObserver(Observer observer) {
		return observers.remove(observer);
	}
}
