package fr.tp.inf112.projects.robotsim.model;

import java.util.ArrayList;
import java.util.List;

import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;
import fr.tp.inf112.projects.robotsim.model.shapes.RectangularShape;

public class Room extends Component {
	
	private static final long serialVersionUID = 1449569724908316962L;

	public static enum WALL {LEFT, TOP, RIGHT, BOTTOM};
	
	private static final int WALL_THICKNESS = 5;
	
	private final PositionedShape leftWall;
	
	private final PositionedShape rightWall;
	
	private final PositionedShape topWall;
	
	private final PositionedShape bottomWall;
	
	private final List<Area> areas;

	private final List<Door> doors;

	public Room(final Factory factory,
				final RectangularShape shape,
				final String name) {
		super(factory, shape, name);
		
		leftWall = new RectangularShape(getxCoordinate(), getyCoordinate(), WALL_THICKNESS, getHeight() + WALL_THICKNESS);
		rightWall = new RectangularShape(getxCoordinate() + getWidth(), getyCoordinate(), WALL_THICKNESS, getHeight() + WALL_THICKNESS);
		topWall = new RectangularShape(getxCoordinate(), getyCoordinate(), getWidth(), WALL_THICKNESS);
		bottomWall = new RectangularShape(getxCoordinate(), getyCoordinate() + getHeight(), getWidth(), WALL_THICKNESS);
		
		areas = new ArrayList<>();
		doors = new ArrayList<>();
	}
	
	protected boolean addArea(final Area area) {
		return areas.add(area);
	}
	
	protected boolean addDoor(final Door door) {
		return doors.add(door);
	}
	
	public List<Area> getAreas() {
		return areas;
	}

	public List<Door> getDoors() {
		return doors;
	}

	@Override
	public boolean overlays(final PositionedShape shape) {
		return leftWall.overlays(shape) || rightWall.overlays(shape) || 
			   topWall.overlays(shape) || bottomWall.overlays(shape);
	}

	@Override
	public boolean canBeOverlayed(final PositionedShape shape) {
		final Door overlayedDoor = getOverlayedDoor(shape);
		
		if (overlayedDoor != null) {
			return overlayedDoor.canBeOverlayed(shape);
		}
		
		if (leftWall.overlays(shape) || rightWall.overlays(shape) || 
			topWall.overlays(shape) || bottomWall.overlays(shape)) {
			return false;
		}
		
		return true;
	}
	
	private Door getOverlayedDoor(final PositionedShape shape) {
		for (final Door door : getDoors()) {
			if (door.overlays(shape)) {
				return door;
			}
		}
		
		return null;
	}

	@Override
	public String toString() {
		return super.toString() + " areas=" + areas + "]";
	}
}
