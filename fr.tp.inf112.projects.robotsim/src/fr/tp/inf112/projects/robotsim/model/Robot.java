package fr.tp.inf112.projects.robotsim.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.tp.inf112.projects.canvas.model.Style;
import fr.tp.inf112.projects.canvas.model.impl.RGBColor;
import fr.tp.inf112.projects.robotsim.model.motion.Motion;
import fr.tp.inf112.projects.robotsim.model.path.FactoryPathFinder;
import fr.tp.inf112.projects.robotsim.model.shapes.CircularShape;
import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;
import fr.tp.inf112.projects.robotsim.model.shapes.RectangularShape;

public class Robot extends Component {
	
	private static final long serialVersionUID = -1218857231970296747L;

	private static final Style STYLE = new ComponentStyle(RGBColor.GREEN, RGBColor.BLACK, 3.0f, null);

	private static final Style BLOCKED_STYLE = new ComponentStyle(RGBColor.RED, RGBColor.BLACK, 3.0f, new float[]{4.0f});

	private Battery battery;
	
	private int speed;
	
	private List<Component> targetComponents;
	
	@JsonIgnore
	private transient Iterator<Component> targetComponentsIterator;
	
	private Component currTargetComponent;
	
	@JsonIgnore
	private transient Iterator<Position> currentPathPositionsIter;
	
	public transient boolean blocked;
	
	private Position nextPosition;
	
	private FactoryPathFinder pathFinder;

	public Robot(final Factory factory,
				 final FactoryPathFinder pathFinder,
				 final CircularShape shape,
				 final Battery battery,
				 final String name ) {
		super(factory, shape, name);
		
		this.pathFinder = pathFinder;
		
		this.battery = battery;
		
		targetComponents = new ArrayList<>();
		currTargetComponent = null;
		currentPathPositionsIter = null;
		speed = 5;
		blocked = false;
		nextPosition = null;
	}
	
	public Robot() {
		targetComponents = new ArrayList<>();
	}
	
	public List<Component> getTargetComponents() {
		return this.targetComponents;
	}
	
 	@Override
	public String toString() {
		return super.toString() + " battery=" + battery + "]";
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(final int speed) {
		this.speed = speed;
	}
	
	public Battery getBattery() {
		return battery;
	}
	
	public boolean addTargetComponent(final Component targetComponent) {
		return targetComponents.add(targetComponent);
	}
	
	public boolean removeTargetComponent(final Component targetComponent) {
		return targetComponents.remove(targetComponent);
	}
	
	@Override
	@JsonIgnore
	public boolean isMobile() {
		return true;
	}

	@Override
	public boolean behave() {
		if (targetComponents.isEmpty()) {
			return false;
		}
		
		if (currTargetComponent == null || hasReachedCurrentTarget()) {
			currTargetComponent = nextTargetComponentToVisit();
		}
		
		computePathToCurrentTargetComponent();

		return moveToNextPathPosition() != 0;
	}
		
	private Component nextTargetComponentToVisit() {
		if (targetComponentsIterator == null || !targetComponentsIterator.hasNext()) {
			targetComponentsIterator = targetComponents.iterator();
		}
		
		return targetComponentsIterator.hasNext() ? targetComponentsIterator.next() : null;
	}
	
	private int moveToNextPathPosition() {
		final Motion motion = computeMotion();
		int displacement = motion == null ? 0 : motion.moveToTarget();
		if (displacement != 0) {
			notifyObservers();
		}
		else if (isLivelyLocked()) {
			displacement = calculateDisplacement();
		}
		return displacement;
	}

	private synchronized int calculateDisplacement() {
		final Position freeNeighbouringPosition = findFreeNeighbouringPosition();
		int displacement = 0;
		
		if (freeNeighbouringPosition != null) {
			nextPosition = freeNeighbouringPosition;
			displacement = moveToNextPathPosition();
			
			// calculating the path from the new position
			computePathToCurrentTargetComponent();
		}
		return displacement;
	}
	
	private Position findFreeNeighbouringPosition() {
		// current position
		final PositionedShape shape = new RectangularShape(nextPosition.getxCoordinate(),
				   nextPosition.getyCoordinate(),
				   2,
				   2);
		
		int x = shape.getxCoordinate();
		int y = shape.getyCoordinate();
		
		// moving to the right
		shape.setyCoordinate(y + speed);
		if(!getFactory().hasObstacleAt(shape))
			return shape.getPosition();
		
		// moving to the left
		shape.setyCoordinate(y - speed);
		if(!getFactory().hasObstacleAt(shape))
			return shape.getPosition();
		
		shape.setyCoordinate(y);
		
		shape.setxCoordinate(x + speed);
		if(!getFactory().hasObstacleAt(shape))
			return shape.getPosition();
		
		// moving backwards
		shape.setxCoordinate(x - speed);
		if(!getFactory().hasObstacleAt(shape))
			return shape.getPosition();
		
		return null;
	}

	private void computePathToCurrentTargetComponent() {
		final List<Position> currentPathPositions = pathFinder.findPath(this, currTargetComponent);
		currentPathPositionsIter = currentPathPositions.iterator();
	}
	
	private Motion computeMotion() {
		if (!currentPathPositionsIter.hasNext()) {

			// There is no free path to the target
			blocked = true;
			
			return null;
		}
		
		final Position nextPosition = this.nextPosition == null ? currentPathPositionsIter.next() : this.nextPosition;
		final PositionedShape shape = new RectangularShape(nextPosition.getxCoordinate(),
				   										   nextPosition.getyCoordinate(),
				   										   2,
				   										   2);
		if (getFactory().hasMobileComponentAt(shape, this)) {
			this.nextPosition = nextPosition;
			
			return null;
		}

		this.nextPosition = null;
		
		return new Motion(getPosition(), nextPosition);
	}
	
	
	@JsonIgnore
	public boolean isLivelyLocked() {
		final Position nextPosition = this.nextPosition;
		if (nextPosition == null) {
			return false;
		}
		final PositionedShape shape = new RectangularShape(nextPosition.getxCoordinate(),
				   nextPosition.getyCoordinate(),
				   2,
				   2);
		
		return getFactory().hasMobileComponentAt(shape, this);
	}
	
	@JsonIgnore
	private boolean hasReachedCurrentTarget() {
		return getPositionedShape().overlays(currTargetComponent.getPositionedShape());
	}
	
	@Override
	public boolean canBeOverlayed(final PositionedShape shape) {
		return true;
	}
	
	@Override
	@JsonIgnore
	public Style getStyle() {
		return blocked ? BLOCKED_STYLE : STYLE;
	}
}
