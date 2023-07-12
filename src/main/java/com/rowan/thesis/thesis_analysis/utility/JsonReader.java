package com.rowan.thesis.thesis_analysis.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rowan.thesis.thesis_analysis.model.input.Span;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class JsonReader {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    @Autowired
    public JsonReader(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    public List<List<Span>> readJsonFile() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:data.json");
        byte[] bytes = Files.readAllBytes(Paths.get(resource.getURI()));
        String jsonContent = new String(bytes);

        TypeReference<List<List<Span>>> typeReference = new TypeReference<List<List<Span>>>() {};
        return objectMapper.readValue(jsonContent, typeReference);
    }
}

