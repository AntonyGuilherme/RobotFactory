package fr.tp.inf112.projects.robotsim.model;

import fr.tp.inf112.projects.canvas.model.Style;
import fr.tp.inf112.projects.canvas.model.impl.RGBColor;
import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;
import fr.tp.inf112.projects.robotsim.model.shapes.RectangularShape;

public class Door extends Component {

	private static final long serialVersionUID = 4038942468211075735L;

	private static final int THICKNESS = 1;
	
	private static int computexCoordinate(final Room room,
										  final Room.WALL wall,
										  final int offset) {
		switch (wall) {
			case BOTTOM: 
			case TOP: {
				return room.getxCoordinate() + offset;
			}
			case LEFT: {
				return room.getxCoordinate();
			}
			
			case RIGHT: {
				return room.getxCoordinate() + room.getWidth();
			}

			default: {
				throw new IllegalArgumentException("Unexpected value: " +  wall );
			}
		}
	}
	
	private static int computeyCoordinate(final Room room,
										  final Room.WALL wall,
										  final int offset) {
		switch (wall) {
			case LEFT: 
			case RIGHT: {
				return room.getyCoordinate() + offset;
			}
			case TOP: {
				return room.getyCoordinate();
			}
			
			case BOTTOM: {
				return room.getyCoordinate() + room.getHeight();
			}

			default: {
				throw new IllegalArgumentException("Unexpected value: " +  wall);
			}
		}
	}
	
	private static PositionedShape createShape(final Room room,
											   final Room.WALL wall,
											   final int offset,
											   final int doorWidth ) {
		final int xCoordinate = computexCoordinate(room, wall, offset);
		final int yCoordinate = computeyCoordinate(room, wall, offset);
		
		if (wall == Room.WALL.BOTTOM || wall == Room.WALL.TOP) {
			return new RectangularShape(xCoordinate, yCoordinate, doorWidth, THICKNESS);
		}
		
		return new RectangularShape(xCoordinate, yCoordinate, THICKNESS, doorWidth);
	}
	
	private boolean open;
	
	private final Room room;
	
	private static final Style OPEN_STYLE = new ComponentStyle(RGBColor.WHITE, null, 0, null);
	
	public Door(final Room room,
				final Room.WALL wall,
				final int offset,
				final int doorWidth,
				final boolean open,
				final String name) {
		super(room.getFactory(),
			  createShape(room, wall, offset, doorWidth),
			  name);
		
		this.room = room;
		this.room.addDoor(this);
		this.open = open;
	}
	
	@Override
	public Style getStyle() {
		return isOpen() ? OPEN_STYLE : ComponentStyle.DEFAULT_BLACK;
	}

	private boolean isOpen() {
		return open;
	}

	public boolean open() {
		if (isOpen()) {
			return false;
		}
		
		open = true;
		
		notifyObservers();
		
		return true;
	}

	public boolean close() {
		if (isOpen()) {
			open = false;
			
			notifyObservers();
			
			return true;
		}
		
		return false;
	}

	@Override
	public String toString() {
		return super.toString() + "]";
	}
	
	@Override
	public boolean canBeOverlayed(final PositionedShape shape) {
		return isOpen();
	}
//
//	private boolean isHorizontal() {
//		return getHeight() == THICKNESS;
//	}
//	
//	@Override
//	public Shape getShape() {
//		return isOpen() ? openShape : super.getShape();
//	}
}
