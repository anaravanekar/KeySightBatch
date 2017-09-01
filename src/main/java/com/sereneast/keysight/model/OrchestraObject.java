package com.sereneast.keysight.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(value = { "childObjects","id" })
public class OrchestraObject {
    private Map<String,OrchestraContent> content;

    List<OrchestraObject> childObjects;

    private Integer id;

    public Map<String, OrchestraContent> getContent() {
        return content;
    }

    public void setContent(Map<String, OrchestraContent> content) {
        this.content = content;
    }

    public List<OrchestraObject> getChildObjects() {
        return childObjects;
    }

    public void setChildObjects(List<OrchestraObject> childObjects) {
        this.childObjects = childObjects;
    }

    public static void main(String[] args) throws IOException {
        OrchestraObject testObject = new OrchestraObject();
        Map<String,OrchestraContent> fields = new HashMap<>();
        fields.put("gender",new OrchestraContent("M"));
        fields.put("firstName",new OrchestraContent("Ashish"));
        testObject.setContent(fields);
        StringWriter stringTestObject = new StringWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(stringTestObject, testObject);
        System.out.println("JSON is\n"+stringTestObject);
    }
}
