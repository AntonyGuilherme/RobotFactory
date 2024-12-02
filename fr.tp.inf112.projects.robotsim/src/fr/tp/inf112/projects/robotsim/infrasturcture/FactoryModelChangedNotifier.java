package fr.tp.inf112.projects.robotsim.infrasturcture;

public interface FactoryModelChangedNotifier {
	void notifyObservers();
	boolean addObserver();
	boolean removeObserver();
}
