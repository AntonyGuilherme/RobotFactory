package fr.tp.inf112.projects.robotsim.model;

import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;
import fr.tp.inf112.projects.robotsim.model.shapes.RectangularShape;

public class ChargingStation extends Component {
	
	private static final long serialVersionUID = -154228412357092561L;
	
	private boolean charging;

	public ChargingStation(final Room room,
						   final RectangularShape shape,
						   final String name) {
		this(room.getFactory(), shape, name);
	}

	public ChargingStation(final Factory factory,
						   final RectangularShape shape,
						   final String name) {
		super(factory, shape, name);
		
		charging = false;
	}

	@Override
	public String toString() {
		return super.toString() + "]";
	}

	protected boolean isCharging() {
		return charging;
	}

	protected void setCharging(boolean charging) {
		this.charging = charging;
	}

	@Override
	public boolean canBeOverlayed(final PositionedShape shape) {
		return true;
	}
}
