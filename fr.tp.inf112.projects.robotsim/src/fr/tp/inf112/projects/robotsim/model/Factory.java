package fr.tp.inf112.projects.robotsim.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import fr.tp.inf112.projects.canvas.controller.Observable;
import fr.tp.inf112.projects.canvas.controller.Observer;
import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.Figure;
import fr.tp.inf112.projects.canvas.model.Style;
import fr.tp.inf112.projects.robotsim.infrasturcture.FactoryModelChangedNotifier;
import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;
import fr.tp.inf112.projects.robotsim.model.shapes.RectangularShape;

public class Factory extends Component implements Canvas, Observable {
	private static final long serialVersionUID = 5156526483612458192L;	
	private static final ComponentStyle DEFAULT = new ComponentStyle(5.0f);
	
	@JsonManagedReference
    private List<Component> components;
	
    @JsonIgnore
    private transient List<Observer> observers;
    @JsonIgnore
	private transient boolean simulationStarted;
    
    private transient FactoryModelChangedNotifier notifier;
    
    @JsonIgnore
    private transient Logger logger = Logger.getLogger("Factory");
	
	public Factory(final int width,
				   final int height,
				   final String name) {
		super(null, new RectangularShape(0, 0, width, height), name);
		
		components = new ArrayList<>();
		observers = null;
		simulationStarted = false;
		
	}
	
	public Factory() {
	}
	
	@JsonIgnore
	public List<Observer> getObservers() {
		if (observers == null) {
			observers = new ArrayList<>();
		}
		
		return observers;
	}

	@Override
	public boolean addObserver(Observer observer) {
		return getObservers().add(observer);
	}

	@Override
	public boolean removeObserver(Observer observer) {
		return getObservers().remove(observer);
	}
	
	public void notifyObservers() {
		for (final Observer observer : getObservers()) {
			observer.modelChanged();
		}
		
		if (notifier != null)
			notifier.notifyObservers();
	}
	
	public boolean addComponent(final Component component) {
		if (components.add(component)) {
			notifyObservers();
			
			return true;
		}
		
		return false;
	}

	public boolean removeComponent(final Component component) {
		if (components.remove(component)) {
			notifyObservers();
			
			return true;
		}
		
		return false;
	}
	
	public List<Component> getComponents() {
		return components;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@JsonIgnore
	public Collection<Figure> getFigures() {
		return (Collection) components;
	}
	

	@Override
	public String toString() {
		return super.toString() + " components=" + components + "]";
	}
	
	public boolean isSimulationStarted() {
		return simulationStarted;
	}

	public void startSimulation() {
		if (!isSimulationStarted()) {
			this.simulationStarted = true;
			notifyObservers();

			while (isSimulationStarted()) {
				behave();
				
				
				try {
					Thread.sleep(100);
				}
				catch (final InterruptedException ex) {
					logger.warning("Simulation was abruptely interrupted");
				}
			}
		}
	}
	

	public void stopSimulation() {
		if (isSimulationStarted()) {
			this.simulationStarted = false;
			
			notifyObservers();
		}
	}

	@Override
	public boolean behave() {
		boolean behaved = true;
		
		for (final Component component : getComponents()) {
			behaved = component.behave() || behaved;
		}
		
		return behaved;
	}
	
	@Override
	@JsonIgnore
	public Style getStyle() {
		return DEFAULT;
	}
	
	@JsonIgnore
	public boolean hasObstacleAt(final PositionedShape shape) {
		for (final Component component : getComponents()) {
			if (component.overlays(shape) && !component.canBeOverlayed(shape)) {
				return true;
			}
		}
		
		return false;
	}
	
	@JsonIgnore
	public boolean hasMobileComponentAt(final PositionedShape shape,
										final Component movingComponent) {
		for (final Component component : getComponents()) {
			if (component != movingComponent && component.isMobile() && component.overlays(shape)) {
				return true;
			}
		}
		
		return false;
	}

	public void setNotifier(FactoryModelChangedNotifier notifier) {
		this.notifier = notifier;
	}
}
