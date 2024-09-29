package fr.tp.inf112.projects.robotsim.model;

import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;

public class Conveyor extends Component {
	
	private static final long serialVersionUID = 3686048824190456547L;

	public Conveyor(final Factory factory,
					final PositionedShape shape,
					final String name) {
		super(factory, shape, name);
	}

	@Override
	public String toString() {
		return super.toString() + "]";
	}

	@Override
	public boolean canBeOverlayed(final PositionedShape shape) {
		return true;
	}
}
