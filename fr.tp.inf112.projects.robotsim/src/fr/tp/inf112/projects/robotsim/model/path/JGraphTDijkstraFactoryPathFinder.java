package fr.tp.inf112.projects.robotsim.model.path;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.DepthFirstIterator;

import fr.tp.inf112.projects.robotsim.model.Component;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.Position;
import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;
import fr.tp.inf112.projects.robotsim.model.shapes.RectangularShape;

public class JGraphTDijkstraFactoryPathFinder extends AbstractFactoryPathFinder<DefaultDirectedGraph<PositionedShape, DefaultEdge>, PositionedShape> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7396132432169002382L;

	public JGraphTDijkstraFactoryPathFinder(final Factory factoryModel,
											final int resolution) {
		super(factoryModel, resolution);
	}

	@Override
	public List<Position> findPath(final Component sourceComponent,
								   final Component targetComponent) {
		buildGraph();
		
		final PositionedShape sourceVertex = getVertex(sourceComponent.getPosition());
		assert sourceVertex != null : "Start vertex should never be null!";

		final PositionedShape targetVertex = getVertex(targetComponent.getPosition());
		assert targetVertex != null : "Target vertex should never be null!";
		
		final AbstractBaseGraph<PositionedShape, DefaultEdge> graph = getGraph(); 
		final GraphPath<PositionedShape, DefaultEdge> shortestPath = DijkstraShortestPath.findPathBetween(graph, sourceVertex, targetVertex);
		final List<Position> shortestPathPositions = new ArrayList<>();
		
		if (shortestPath != null) {
			for (final PositionedShape vertex : shortestPath.getVertexList()) {
				if (vertex != sourceVertex) {
					shortestPathPositions.add(vertex.getPosition());
				}
			}
		}
		
		return shortestPathPositions;
	}

	@Override
	protected PositionedShape getVertex(final int xIndex,
										final int yIndex) {
		final Iterator<PositionedShape> iterator = getGraphVertexesIterator();
		final int resolution = getResolution();
		final Position position = new Position(xIndex * resolution, yIndex * resolution);

		while (iterator.hasNext()) {
			final PositionedShape shape = iterator.next();
			
			if (position.equals(shape.getPosition())) {
				return shape;
			}
		}
		
		return null;
	}

	@Override
	protected float overlayedSurface(final PositionedShape vertex,
									 final PositionedShape shape) {
		return vertex.getOverlayedSurface(shape);
	}

	@Override
	protected Iterator<PositionedShape> getGraphVertexesIterator() {
		return new DepthFirstIterator<>(getGraph());
	}

	@Override
	protected DefaultDirectedGraph<PositionedShape, DefaultEdge> newGraph() {
		return new DefaultDirectedGraph<PositionedShape, DefaultEdge>(DefaultEdge.class);
	}

	@Override
	protected boolean addVertex(final int xCoordinate,
								final int yCoordinate) {
		final PositionedShape vertex = new RectangularShape(xCoordinate,
															yCoordinate,
															getResolution(),
															getResolution());
		return getGraph().addVertex(vertex);
	}

	@Override
	protected boolean addEdge(final PositionedShape vertex1,
							  final PositionedShape vertex2) {
		return getGraph().addEdge(vertex1, vertex2) != null;
	}

	@Override
	protected int getxCoordinate(final PositionedShape vertex) {
		return vertex.getxCoordinate();
	}

	@Override
	protected int getyCoordinate(final PositionedShape vertex) {
		return vertex.getyCoordinate();
	}
}
