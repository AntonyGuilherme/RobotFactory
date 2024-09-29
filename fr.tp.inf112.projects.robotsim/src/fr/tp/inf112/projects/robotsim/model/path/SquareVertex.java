package fr.tp.inf112.projects.robotsim.model.path;

import fr.tp.inf112.projects.graph.impl.GridVertex;
import fr.tp.inf112.projects.robotsim.model.Position;
import fr.tp.inf112.projects.robotsim.model.shapes.RectangularShape;

public class SquareVertex extends GridVertex {
	
	private final RectangularShape shape;

	public SquareVertex(final String label,
						final int xCoordinate,
						final int yCoordinate,
						final int size) {
		super(label, xCoordinate, yCoordinate);

		this.shape = new RectangularShape(xCoordinate, yCoordinate, size, size);
	}

	public RectangularShape getShape() {
		return shape;
	}
	
	public Position getPosition() {
		return getShape() == null ? null : getShape().getPosition();
	}
}
