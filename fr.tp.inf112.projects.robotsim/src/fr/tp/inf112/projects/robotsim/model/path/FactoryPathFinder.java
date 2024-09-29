package fr.tp.inf112.projects.robotsim.model.path;

import java.util.List;

import fr.tp.inf112.projects.robotsim.model.Component;
import fr.tp.inf112.projects.robotsim.model.Position;

public interface FactoryPathFinder {

	List<Position> findPath(Component sourceComponent,
							Component targetComponent);
}
