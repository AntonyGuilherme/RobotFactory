package fr.tp.inf112.projects.robotsim.model.path;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.Position;
import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;
import fr.tp.inf112.projects.robotsim.model.shapes.RectangularShape;

public abstract class AbstractFactoryPathFinder<Graph, Vertex> implements FactoryPathFinder, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3864762720560889146L;

	private final Factory factoryModel;
	
	private final int resolution;
	
	private transient Graph graph;

	public AbstractFactoryPathFinder(final Factory factoryModel,
									 final int resolution) {
		this.factoryModel = factoryModel;
		this.resolution = resolution;
		graph = null;
	}

	public Factory getFactoryModel() {
		return factoryModel;
	}

	public int getResolution() {
		return resolution;
	}
	
	protected Graph getGraph() {
		return graph;
	}
	
	protected void buildGraph() {
		if (getGraph() == null) {
			graph = newGraph();
			final int xSize = getFactoryModel().getWidth() / getResolution();
			final int ySize = getFactoryModel().getHeight() / getResolution();
	
			for (int xIndex = 0; xIndex < xSize; xIndex++) {
				for (int yIndex = 0; yIndex < ySize; yIndex++) {
					final int xCoordinate = xIndex * getResolution();
					final int yCoordinate = yIndex * getResolution();
					addVertex(xCoordinate, yCoordinate);
				}
			}
			
			final Iterator<? extends Vertex> vertexesIterator = getGraphVertexesIterator();
			
			while (vertexesIterator.hasNext()) {
				final Vertex vertex = vertexesIterator.next();
				final Set<Vertex> successors = getSuccessors(getxCoordinate(vertex), getyCoordinate(vertex));

				for (final Vertex succVertex : successors) {
					addEdge(vertex, succVertex);
				}
			}
			
			System.out.println(graph.toString());
		}
	}
	
	protected abstract Graph newGraph();

	protected abstract boolean addVertex(int xCoordinate,
										 int yCoordinate);

	protected abstract boolean addEdge(Vertex vertex1,
									   Vertex vertex2);
	
	protected abstract int getxCoordinate(Vertex vertex);

	protected abstract int getyCoordinate(Vertex vertex);

	protected Set<Vertex> getSuccessors(final int xCoordinate,
									    final int yCoordinate) {
		final int xIndex = xCoordinate / getResolution();
		final int yIndex = yCoordinate / getResolution();
		
		final Set<Vertex> successors = new HashSet<>();

		Vertex succVertex = getBackwardyVertex(xIndex, yIndex);
		
		if (succVertex != null) {
			successors.add(succVertex);
		}

//		succVertex = getForwardxBackwardyVertex(xIndex, yIndex);
//		
//		if (succVertex != null) {
//			successors.add(succVertex);
//		}

		succVertex = getForwardxVertex(xIndex, yIndex);
		
		if (succVertex != null) {
			successors.add(succVertex);
		}

//		succVertex = getForwardxForwardyVertex(xIndex, yIndex);
//		
//		if (succVertex != null) {
//			successors.add(succVertex);
//		}

		succVertex = getForwardyVertex(xIndex, yIndex);
		
		if (succVertex != null) {
			successors.add(succVertex);
		}

//		succVertex = getBackwardxForwardyVertex(xIndex, yIndex);
//		
//		if (succVertex != null) {
//			successors.add(succVertex);
//		}

		succVertex = getBackwardxVertex(xIndex, yIndex);
		
		if (succVertex != null) {
			successors.add(succVertex);
		}

//		succVertex = getBackwardxBackwardyVertex(xIndex, yIndex);
//		
//		if (succVertex != null) {
//			successors.add(succVertex);
//		}

		return successors;
	}
	
	private Vertex getBackwardxVertex(final int xIndex,
								      final int yIndex) {
		final int searchedxIndex = xIndex - 1;
		
		if (searchedxIndex >= 0) {
			return getFreeVertex(searchedxIndex, yIndex);
		}
		
		return null;
	}
	
	private Vertex getBackwardyVertex(final int xIndex,
									  final int yIndex) {
		final int searchedyIndex = yIndex - 1;
		
		if (searchedyIndex >= 0) {
			return getFreeVertex(xIndex, searchedyIndex);	
		}
		
		return null;
	}
	
	private Vertex getForwardxVertex(final int xIndex,
									 final int yIndex) {
		final int searchedxIndex = xIndex + 1;
		
		if (searchedxIndex < getFactoryModel().getWidth() / getResolution()) {
			return getFreeVertex(searchedxIndex, yIndex);
		}
		
		return null;
	}
	
	private Vertex getForwardyVertex(final int xIndex,
									 final int yIndex) {
		final int searchedyIndex = yIndex + 1;
			
		if (searchedyIndex < getFactoryModel().getHeight() / getResolution()) {
			return getFreeVertex(xIndex, searchedyIndex);
		}
		
		return null;
	}
	
	protected Vertex getFreeVertex(final int xIndex,
								   final int yIndex) {
		final int resolution = getResolution();
		final int xCoordinate = xIndex * resolution;
		final int yCoordinate = yIndex * resolution;
		
		final PositionedShape shape = new RectangularShape(xCoordinate, yCoordinate, resolution, resolution);
		
		if (!getFactoryModel().hasObstacleAt(shape)) {
			return getVertex(xIndex, yIndex);
		}
		
		return null;
	}
	
	protected abstract Vertex getVertex(final int xIndex,
								 		final int yIndex);
	
	protected Vertex getVertex(final Position position) {
		float currentMaxOverlayedSurface = 0.0f;
		Vertex maxOverlayedSurfaceVertex = null; 
		final PositionedShape shape = new RectangularShape(position.getxCoordinate(), 
														   position.getyCoordinate(),
														   resolution,
														   resolution);
		
		final  Iterator<? extends Vertex> vertexesIterator = getGraphVertexesIterator();
		
		while (vertexesIterator.hasNext()) {
			final Vertex vertex = vertexesIterator.next();
			final float overlayedSurface = overlayedSurface(vertex, shape);
			
			if (overlayedSurface  > currentMaxOverlayedSurface) {
				currentMaxOverlayedSurface = overlayedSurface;
				maxOverlayedSurfaceVertex = vertex;
			}
		}
		
		return maxOverlayedSurfaceVertex;
	}
	
	protected abstract float overlayedSurface(Vertex vertex, 
											  PositionedShape shape);
	
	protected abstract Iterator<? extends Vertex> getGraphVertexesIterator();
}
