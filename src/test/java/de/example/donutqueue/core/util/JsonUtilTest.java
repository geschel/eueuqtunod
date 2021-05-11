package de.example.donutqueue.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JsonUtilTest {

    @Test
    void removeControlChars() {
        String text = "ABC abc 123 -= \n ?%&$§()[]{}|<>/\\'#+*\" in medical devices\u200b \u200a \u200c";
        String actual = JsonUtil.removeControlChars(text);
        assertEquals("ABC abc 123 -= \n ?%&$§()[]{}|<>/\\'#+*\" in medical devices  ", actual);
    }

    @Test
    void arrayToListTest() {
        String array = "[720, 721]";
        try {
            List<String> list = JsonUtil.jsonArrayToList(array);
            assertEquals(2, list.size());
            Collections.sort(list);
            assertEquals("720", list.get(0));
            assertEquals("721", list.get(1));

            assertEquals(0, JsonUtil.jsonArrayToList(null).size());
            assertEquals(0, JsonUtil.jsonArrayToList("").size());
            assertEquals(0, JsonUtil.jsonArrayToList("[]").size());
        } catch (JsonProcessingException e) {
            fail(e);
        }
    }

}
