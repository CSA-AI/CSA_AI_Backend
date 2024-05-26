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

        JsonNode classCodeNode = node.get("classCode");
        JsonNode classNameNode = node.get("className");
        JsonNode emailNode = node.get("email");

        if (classCodeNode == null || classCodeNode.isNull() || emailNode.isNull()) {
            throw new IOException("classCode field is missing or null");
        }

        String classCodeValue = classCodeNode.asText().trim();

        if (classCodeValue.isEmpty()) {
            throw new IOException("Invalid classCode value");
        }

        String classNameValue = classNameNode != null && !classNameNode.isNull() ? classNameNode.asText().trim() : "";

        String emailValue = emailNode != null && !emailNode.isNull() ? emailNode.asText().trim() : "";

        return new ClassCode(classCodeValue, classNameValue, emailValue, 100000.00, 100000.00);
    }
}