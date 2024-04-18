package com.nighthawk.spring_portfolio.mvc.person;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;


import java.io.IOException;

public class ClassCodeDeserializer extends StdDeserializer<ClassCode> {

    public ClassCodeDeserializer() {
        this(null);
    }

    public ClassCodeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ClassCode deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String ClassCode = node.get("ClassCode").asText();
        // Assuming ClassCode has a constructor that takes the classCode as a parameter
        return new ClassCode(ClassCode);
    }
}
