package fr.tp.inf112.projects.robotsim.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

import fr.tp.inf112.projects.robotsim.app.SimulationClient;
import fr.tp.inf112.projects.robotsim.model.Area;
import fr.tp.inf112.projects.robotsim.model.Battery;
import fr.tp.inf112.projects.robotsim.model.ChargingStation;
import fr.tp.inf112.projects.robotsim.model.Component;
import fr.tp.inf112.projects.robotsim.model.Conveyor;
import fr.tp.inf112.projects.robotsim.model.Door;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.FactorySerialyzer;
import fr.tp.inf112.projects.robotsim.model.Machine;
import fr.tp.inf112.projects.robotsim.model.Robot;
import fr.tp.inf112.projects.robotsim.model.Room;
import fr.tp.inf112.projects.robotsim.model.path.CustomDijkstraFactoryPathFinder;
import fr.tp.inf112.projects.robotsim.model.path.FactoryPathFinder;
import fr.tp.inf112.projects.robotsim.model.path.JGraphTDijkstraFactoryPathFinder;
import fr.tp.inf112.projects.robotsim.model.shapes.BasicPolygonShape;
import fr.tp.inf112.projects.robotsim.model.shapes.CircularShape;
import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;
import fr.tp.inf112.projects.robotsim.model.shapes.RectangularShape;
import fr.tp.inf112.projects.canvas.model.impl.BasicVertex;

public class SerializationTests {
	private Logger LOGGER = Logger.getAnonymousLogger();

	//@Test
	public void factoryShouldBeSerialized() throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		
		PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
				.allowIfSubType(PositionedShape.class.getPackageName())
				.allowIfSubType(Component.class.getPackageName())
				.allowIfSubType(ArrayList.class.getName())
				.allowIfSubType(LinkedHashSet.class.getName())
				.allowIfSubType(BasicVertex.class.getName())
				.build();
		mapper.activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL);
		
		final Factory factory = createFactory();
		
		
		final String factoryAsJsonString = mapper.writeValueAsString(factory);
		LOGGER.info(factoryAsJsonString);
		
		FactorySerialyzer s = new FactorySerialyzer();
		
		s.createFactoryFrom(factoryAsJsonString);
		
		
		final Factory roundTrip = mapper.readValue(factoryAsJsonString, Factory.class);
		
		
		//LOGGER.info(roundTrip.toString());
		
		// Assuring that no information is being lost in the serialization
		assertEquals(mapper.writeValueAsString(factory), mapper.writeValueAsString(roundTrip));
	}
	
	//@Test
	public void getFactory() throws JsonMappingException, JsonProcessingException {
		SimulationClient client = new SimulationClient("vasco.factory");
		
		assertNotNull(client.getFactory());
	}
	
	@Test
	public void shouldSerializedOpenPropertyInDoors() {
		FactorySerialyzer serialyzer = new FactorySerialyzer();
		Factory factory = serialyzer.createFactoryMock();
		String json = serialyzer.toJSON(factory);
		
		
		Factory serialyzedFactory = serialyzer.createFactoryFrom(json);
		
		//System.out.println(json);
		assertTrue(json.contains("open"));
	}
	
	@Test
	public void shouldSerializedTargetComponetsInRobots() {
		FactorySerialyzer serialyzer = new FactorySerialyzer();
		Factory factory = serialyzer.createFactoryMock();
		String json = serialyzer.toJSON(factory);
		
		
		Factory serialyzedFactory = serialyzer.createFactoryFrom(json);
		
		System.out.println(json);
		assertTrue(json.contains("targetComponents"));
	}
	
	@Test
	public void shouldSerializedBatteryInRobots() {
		FactorySerialyzer serialyzer = new FactorySerialyzer();
		Factory factory = serialyzer.createFactoryMock();
		String json = serialyzer.toJSON(factory);
		
		
		Factory serialyzedFactory = serialyzer.createFactoryFrom(json);
		
		System.out.println(json);
		assertTrue(json.contains("battery"));
		assertTrue(json.contains("speed"));
		assertTrue(json.contains("blocked"));
		assertTrue(json.contains("charging"));
		assertTrue(json.contains("leftWall"));
	}
	
	
	private final Factory createFactory() {
		
		final Factory factory = new Factory(200, 200, "Simple Test Puck Factory");
		final Room room1 = new Room(factory, new RectangularShape(20, 20, 75, 75), "Production Room 1");
		new Door(room1, Room.WALL.BOTTOM, 10, 20, true, "Entrance");
		final Area area1 = new Area(room1, new RectangularShape(35, 35, 50, 50), "Production Area 1");
		final Machine machine1 = new Machine(area1, new RectangularShape(50, 50, 15, 15), "Machine 1");

		final Room room2 = new Room(factory, new RectangularShape( 120, 22, 75, 75 ), "Production Room 2");
		new Door(room2, Room.WALL.LEFT, 10, 20, true, "Entrance");
		final Area area2 = new Area(room2, new RectangularShape( 135, 35, 50, 50 ), "Production Area 1");
		final Machine machine2 = new Machine(area2, new RectangularShape( 150, 50, 15, 15 ), "Machine 1");
		
		final int baselineSize = 3;
		final int xCoordinate = 10;
		final int yCoordinate = 165;
		final int width =  10;
		final int height = 30;
		final BasicPolygonShape conveyorShape = new BasicPolygonShape();
		conveyorShape.addVertex(new BasicVertex(xCoordinate, yCoordinate));
		conveyorShape.addVertex(new BasicVertex(xCoordinate + width, yCoordinate));
		conveyorShape.addVertex(new BasicVertex(xCoordinate + width, yCoordinate + height - baselineSize));
		conveyorShape.addVertex(new BasicVertex(xCoordinate + width + baselineSize, yCoordinate + height - baselineSize));
		conveyorShape.addVertex(new BasicVertex(xCoordinate + width + baselineSize, yCoordinate + height));
		conveyorShape.addVertex(new BasicVertex(xCoordinate - baselineSize, yCoordinate + height));
		conveyorShape.addVertex(new BasicVertex(xCoordinate - baselineSize, yCoordinate + height - baselineSize));
		conveyorShape.addVertex(new BasicVertex(xCoordinate, yCoordinate + height - baselineSize));

		final Room chargingRoom = new Room(factory, new RectangularShape(125, 125, 50, 50), "Charging Room");
		new Door(chargingRoom, Room.WALL.RIGHT, 10, 20, true, "Entrance");
		final ChargingStation chargingStation = new ChargingStation(factory, new RectangularShape(150, 145, 15, 15), "Charging Station");

		final FactoryPathFinder jgraphPahtFinder = new JGraphTDijkstraFactoryPathFinder(factory, 5);
		final Robot robot1 = new Robot(factory, jgraphPahtFinder, new CircularShape(5, 5, 2), new Battery(10), "Robot 1");
		robot1.addTargetComponent(machine1);
		robot1.addTargetComponent(machine2);
		robot1.addTargetComponent(new Conveyor(factory, conveyorShape, "Conveyor 1"));
		robot1.addTargetComponent(chargingStation);

		final FactoryPathFinder customPathFinder = new CustomDijkstraFactoryPathFinder(factory, 5);
		final Robot robot2 = new Robot(factory, customPathFinder, new CircularShape(45, 5, 2), new Battery(10), "Robot 2");
		robot2.addTargetComponent(chargingStation);
		robot2.addTargetComponent(machine1);
		robot2.addTargetComponent(machine2);
		robot2.addTargetComponent(new Conveyor(factory, conveyorShape, "Conveyor 1"));
		
		
		return factory;
		
	}

}
