package fr.tp.inf112.projects.robotsim.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

	private final Battery battery;
	
	private int speed;
	
	private final List<Component> targets;
	
	private transient Iterator<Component> targetsIter;
	
	private Component currentTarget;
	
	private transient Iterator<Position> currentPathIter;
	
	private transient boolean blocked;
	
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
		
		targets = new ArrayList<>();
		currentTarget = null;
		currentPathIter = null;
		speed = 5;
		blocked = false;
		nextPosition = null;
	}

	@Override
	public String toString() {
		return super.toString() + " battery=" + battery + "]";
	}

	protected int getSpeed() {
		return speed;
	}

	protected void setSpeed(final int speed) {
		this.speed = speed;
	}
	
	public boolean addTarget(final Component target) {
		return targets.add(target);
	}
	
	public boolean removeTarget(final Component target) {
		return targets.remove(target);
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}

	@Override
	public boolean behave() {
		final int movedDistance = moveToTarget();
		
		if (movedDistance != 0) {
			battery.consume(movedDistance);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void run() {
		
		while(isSimulationStarted()) {
			//System.out.println(String.format("Robot %s %b", this.getName(), isSimulationStarted()));
			try {
				Thread.sleep(50);
				behave();
			} catch (InterruptedException e) {
				System.out.println("The robot thread was not able to await");
			}
		}
	}
	
	private int moveToTarget() {
		if (targets.isEmpty()) {
			return 0;
		}
			
		if (targetsIter == null) {
			targetsIter = targets.iterator();
			initCurrentTarget();
		}
		
		if (hasReachedCurrentTarget()) {
			if (!targetsIter.hasNext()) {
				targetsIter = targets.iterator();
			}
			
			initCurrentTarget();
		}

		//final Motion direction = computeMotion();
		final int displacement = computeMotion();
		
		//if (direction == null) {
		//	displacement = 0;
		//}
		////else {
		//	displacement = direction.moveToTarget();
		//}
			
		notifyObservers();
			
		return displacement;
	}
	
	private void initCurrentTarget() {
		currentTarget = targetsIter.next();
		final List<Position> currentPath = pathFinder.findPath(this, currentTarget);
		currentPathIter = currentPath.iterator();
	}
	
	private int computeMotion() {
		
		// There is no free path to the target
		if (!currentPathIter.hasNext()) {
			blocked = true;
			
			return 0;
		}
		
		final Position nextPosition = this.nextPosition == null ? currentPathIter.next() : this.nextPosition;
		
		return getFactory().moveTo(this, nextPosition);
		
		//final PositionedShape shape = new RectangularShape(nextPosition.getxCoordinate(),
		//		   										   nextPosition.getyCoordinate(),
		//		   										   2,	
		//		   										   2);
		// this step is responsible to check if exists another component at the interest position
		// in this case of existence of another robot they simply stop 
		//if (getFactory().hasMobileComponentAt(shape, this)) {
		//	this.nextPosition = nextPosition;
			
			//return null;
		//}

		//this.nextPosition = null;
		
		//return new Motion(getPosition(), nextPosition);
	}
	
	private boolean hasReachedCurrentTarget() {
		return getPositionedShape().overlays(currentTarget.getPositionedShape());
	}
	
	@Override
	public boolean canBeOverlayed(final PositionedShape shape) {
		return true;
	}
	
	@Override
	public Style getStyle() {
		return blocked ? BLOCKED_STYLE : STYLE;
	}
}
