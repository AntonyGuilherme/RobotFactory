package fr.tp.inf112.projects.robotsim.model.path;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.tp.inf112.projects.graph.DijkstraAlgorithm;
import fr.tp.inf112.projects.graph.Edge;
import fr.tp.inf112.projects.graph.Vertex;
import fr.tp.inf112.projects.graph.impl.GridEdge;
import fr.tp.inf112.projects.graph.impl.GridGraph;
import fr.tp.inf112.projects.graph.impl.GridVertex;
import fr.tp.inf112.projects.robotsim.model.Component;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.Position;
import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;

public class CustomDijkstraFactoryPathFinder extends AbstractFactoryPathFinder<GridGraph, SquareVertex> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6996131946200605552L;

	public CustomDijkstraFactoryPathFinder(final Factory factoryModel,
									 	   final int resolution) {
		super(factoryModel, resolution);
	}

	@Override
	public List<Position> findPath(final Component sourceComponent,
								   final Component targetComponent) {
		buildGraph();
		
		final Position sourcePosition = sourceComponent.getPosition();
		final Position targetPosition = targetComponent.getPosition();
		
		final Vertex startVertex = getVertex(sourcePosition);
		assert startVertex != null : "Start vertex should never be null!";

		final Vertex targetVertex = getVertex(targetPosition);
		assert targetVertex != null : "Target vertex should never be null!";
		
		final GridGraph graph = getGraph();
		graph.setTargetVertex((GridVertex) targetVertex);
		
		final List<Vertex> shortestPath = DijkstraAlgorithm.findShortestPath(graph, startVertex, targetVertex);
		final List<Position> shortestPathPositions = new ArrayList<>();
		
		for (final Vertex vertex : shortestPath) {
			if (vertex != startVertex) {
				shortestPathPositions.add(( (SquareVertex) vertex ).getPosition());
			}
		}

		return shortestPathPositions;
	}

	@Override
	protected SquareVertex getVertex(final int xIndex, 
									 final int yIndex) {
		final Iterator<SquareVertex> iterator = getGraphVertexesIterator();
		final int resolution = getResolution();
		final Position position = new Position(xIndex * resolution, yIndex * resolution);

		while (iterator.hasNext()) {
			final SquareVertex squareVertex = iterator.next();
			
			if (position.equals(squareVertex.getPosition())) {
				return squareVertex;
			}
		}
		
		return null;
	}

	@Override
	protected float overlayedSurface(final SquareVertex vertex, 
									 final PositionedShape shape) {
		return vertex.getShape().getOverlayedSurface(shape);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Iterator<SquareVertex> getGraphVertexesIterator() {
		return (Iterator) getGraph().getVertexes().iterator();
	}

	@Override
	protected GridGraph newGraph() {
		return new GridGraph();
	}

	@Override
	protected boolean addVertex(final int xCoordinate,
								final int yCoordinate) {
		final int resolution = getResolution();
		final Vertex vertex = new SquareVertex("(" + xCoordinate / resolution + ", "+ yCoordinate / resolution + ")",
											   xCoordinate,
											   yCoordinate,
											   resolution);
		return getGraph().addVertex(vertex);
	}

	@Override
	protected boolean addEdge(final SquareVertex vertex1,
							  final SquareVertex vertex2) {
		final GridGraph graph = getGraph();
		final Edge edge = new GridEdge(graph, vertex1, vertex2, 1);
		vertex1.addEdge(edge);
		
		return graph.addEdge(edge);
	}

	@Override
	protected int getxCoordinate(final SquareVertex vertex) {
		return vertex.getxCoordinate();
	}

	@Override
	protected int getyCoordinate(final SquareVertex vertex) {
		return vertex.getyCoordinate();
	}
}
