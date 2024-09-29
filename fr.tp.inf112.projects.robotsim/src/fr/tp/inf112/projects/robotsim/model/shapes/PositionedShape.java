package fr.tp.inf112.projects.robotsim.model.shapes;

import java.io.Serializable;

import fr.tp.inf112.projects.canvas.model.Shape;
import fr.tp.inf112.projects.robotsim.model.Position;

public abstract class PositionedShape implements Shape, Serializable {

	private static final long serialVersionUID = 2217860927757709195L;

	private static float intersectionLength(final int coordinate1,
											final int width1,
											final int coordinate2,
											final int width2) {
		if (coordinate1 <= coordinate2) {
			if (coordinate1 + width1 >= coordinate2) {
				return coordinate1 + width1 - coordinate2;
			}
			
			return 0;
		}

		if (coordinate2 + width2 >= coordinate1) {
			return coordinate2 + width2 - coordinate1;
		}
		
		return 0;
	}

	private final Position position;
	
	protected PositionedShape(final int xCoordinate,
							  final int yCoordinate) {
		this.position = new Position(xCoordinate, yCoordinate);
	}

	public abstract int getWidth();

	public abstract int getHeight();
	
	public boolean overlays(final PositionedShape shape) {
		return getOverlayedSurface(shape) > 0.0f;
	}
	
	public float getOverlayedSurface(final PositionedShape shape) {
		return xIntersectionLength(shape) * yIntersectionLength(shape);
	}
	
	protected float xIntersectionLength(final PositionedShape shape) {
		return intersectionLength(getxCoordinate(), getWidth(), shape.getxCoordinate(), shape.getWidth());
	}

	protected float yIntersectionLength(final PositionedShape shape) {
		return intersectionLength(getyCoordinate(), getHeight(), shape.getyCoordinate(), shape.getHeight());
	}
	
	public Position getPosition() {
		return position;
	}

	public int getxCoordinate() {
		return getPosition().getxCoordinate();
	}

	public boolean setxCoordinate(final int xCoordinate) {
		return getPosition().setxCoordinate(xCoordinate);
	}
	
	public int getyCoordinate() {
		return getPosition().getyCoordinate();
	}

	public boolean setyCoordinate(final int yCoordinate) {
		return getPosition().setyCoordinate(yCoordinate);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " at " + String.valueOf(getPosition());
	}
}
