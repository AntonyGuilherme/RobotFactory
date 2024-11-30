package fr.tp.inf112.projects.robotsim.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

import fr.tp.inf112.projects.canvas.model.impl.BasicVertex;
import fr.tp.inf112.projects.robotsim.model.shapes.PositionedShape;

public class FactorySerialyzer {
	
	ObjectMapper mapper = new ObjectMapper();
	
	PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
			.allowIfSubType(PositionedShape.class.getPackageName())
			.allowIfSubType(Component.class.getPackageName())
			.allowIfSubType(ArrayList.class.getName())
			.allowIfSubType(LinkedHashSet.class.getName())
			.allowIfSubType(BasicVertex.class.getName())
			.build();
	
	
	public Factory createFactoryFrom(String json) {
		try {
			return mapper.readValue(json, Factory.class);
		} catch (Exception e) {
			return null;
		}
	}
	
	public String toJSON(Factory factory) {
		mapper.activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL);
		
		try {
			return mapper.writeValueAsString(factory);
		} catch (JsonProcessingException e) {
			return null;
		}
	}
}
