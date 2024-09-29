package fr.tp.inf112.projects.robotsim.model.shapes;

import java.util.LinkedHashSet;
import java.util.Set;

import fr.tp.inf112.projects.canvas.model.PolygonShape;
import fr.tp.inf112.projects.canvas.model.Vertex;

public class BasicPolygonShape extends PositionedShape implements PolygonShape {
	
	private static final long serialVersionUID = -1764316101910546849L;

	private final Set<Vertex> vertices;
	
	public BasicPolygonShape() {
		super(0, 0);
		
		this.vertices = new LinkedHashSet<>();
	}
	
	@Override
	public Set<Vertex> getVertices() {
		return vertices;
	}
	
	public boolean addVertex(final Vertex vertex) {
		final boolean added = getVertices().add(vertex);
		
		updatePosition();
		
		return added;
	}
	
	private void updatePosition() {
		int minxCoordinate = Integer.MAX_VALUE;
		int minyCoordinate = minxCoordinate;
		
		for (final Vertex vertex : getVertices()) {
			minxCoordinate = Math.min(minxCoordinate, vertex.getxCoordinate());
			minyCoordinate = Math.min(minyCoordinate, vertex.getyCoordinate());
		}
		
		setxCoordinate(minxCoordinate);
		setyCoordinate(minyCoordinate);
	}
	
	@Override
	public int getWidth() {
		int minCoordinate = Integer.MAX_VALUE;
		int maxCoordinate = 0;
		
		for (final Vertex vertex : getVertices()) {
			final int coordinate = vertex.getxCoordinate();

			minCoordinate = Math.min(minCoordinate, coordinate);
			maxCoordinate = Math.max(maxCoordinate, coordinate);
		}
		
		return maxCoordinate - minCoordinate;
	}

	@Override
	public int getHeight() {
		int minCoordinate = Integer.MAX_VALUE;
		int maxCoordinate = 0;
		
		for (final Vertex vertex : getVertices()) {
			final int coordinate = vertex.getyCoordinate();

			minCoordinate = Math.min(minCoordinate, coordinate);
			maxCoordinate = Math.max(maxCoordinate, coordinate);
		}
		
		return maxCoordinate - minCoordinate;
	}
}
