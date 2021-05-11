package de.example.donutqueue.core.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This helper utility contains different easy to use json mappers.
 */
public class JsonUtil {

    private JsonUtil() {
    }

    /**
     * this mapper disables auto-boxing between String and Number/Float
     */
    public static ObjectMapper strictMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new SimpleModule().addDeserializer(String.class, new ForceStringDeserializer()));
        mapper.registerModule(new SimpleModule().addDeserializer(Integer.class, new ForceIntDeserializer()));
        mapper.registerModule(new SimpleModule().addDeserializer(Long.class, new ForceLongDeserializer()));
        mapper.registerModule(new SimpleModule().addDeserializer(Double.class, new ForceFloatDeserializer()));
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        return mapper;
    }

    /**
     * Removes all control characters except new lines and whitespaces.
     *
     * @param text string to be edited
     * @return
     */
    public static String removeControlChars(String text) {
        String result = null;

        if (text != null) {
            final String whiteSpace = " ";
            final String newLine = "\n";
            final String placeHolderWp = "${WP}";
            final String placeHolderNl = "${NL}";

            // safe new lines and whitespaces
            result = text.replace(whiteSpace, placeHolderWp)
                    .replace(newLine, placeHolderNl);
            // remove control characters
            result = CharMatcher.invisible().removeFrom(result);
            // restore new lines and whitespaces
            result = result.replace(placeHolderWp, whiteSpace)
                    .replace(placeHolderNl, newLine);
        }

        return result;
    }

    /**
     * Converts given JSON-array to list of strings.
     *
     * @param array JSON-array like '[710, 720]'
     * @return list of strings
     */
    public static List<String> jsonArrayToList(String array) throws JsonProcessingException {
        if (StringUtils.isBlank(array)) {
            return Collections.emptyList();
        }

        return (List<String>) JsonUtil.strictMapper().readValue(array, List.class)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    /**
     * Utility methode for dumping arbitrary objects without.
     * @param dumpMe - arbitrary object
     * @return String
     */
    public static String dump(Object dumpMe) {
        if(null == dumpMe) {
            return null;
        }
        try {
            return JsonUtil.strictMapper().writeValueAsString(dumpMe);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * custom deserializer for turning off auto-boxing of json fields
     */
    static class ForceFloatDeserializer extends JsonDeserializer<Double> {

        @Override
        public Double deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            if (!jsonParser.getCurrentToken().equals(JsonToken.VALUE_NUMBER_INT) && !jsonParser.getCurrentToken().equals(JsonToken.VALUE_NUMBER_FLOAT)) {
                throw deserializationContext.wrongTokenException(jsonParser, Double.class, JsonToken.VALUE_NUMBER_FLOAT, "Attempted to parse float but found something else");
            }
            return jsonParser.getValueAsDouble();
        }
    }

    /**
     * custom deserializer for turning off auto-boxing of json fields
     */
    static class ForceIntDeserializer extends JsonDeserializer<Integer> {

        @Override
        public Integer deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            if (!(jsonParser.getCurrentToken().equals(JsonToken.VALUE_NUMBER_INT))) {
                throw deserializationContext.wrongTokenException(jsonParser, Integer.class, JsonToken.VALUE_NUMBER_INT, "Attempted to parse Integer but found something else");
            }
            return jsonParser.getValueAsInt();
        }
    }

    /**
     * custom deserializer for turning off auto-boxing of json fields
     */
    static class ForceLongDeserializer extends JsonDeserializer<Long> {

        @Override
        public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            if (!(jsonParser.getCurrentToken().equals(JsonToken.VALUE_NUMBER_INT))) {
                throw deserializationContext.wrongTokenException(jsonParser, Long.class, JsonToken.VALUE_NUMBER_INT, "Attempted to parse long but found something else");
            }
            return jsonParser.getValueAsLong();
        }
    }

    /**
     * custom deserializer for turning off auto-boxing of json fields
     */
    static class ForceStringDeserializer extends JsonDeserializer<String> {

        @Override
        public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            if (!jsonParser.getCurrentToken().equals(JsonToken.VALUE_STRING)) {
                throw deserializationContext.wrongTokenException(jsonParser, String.class, JsonToken.VALUE_STRING, "Attempted to parse something else than string but this is forbidden for this field");
            }
            return jsonParser.getValueAsString();
        }
    }
}