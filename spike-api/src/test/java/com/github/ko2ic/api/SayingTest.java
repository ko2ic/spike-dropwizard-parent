package com.github.ko2ic.api;

import static org.fest.assertions.api.Assertions.*;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.FixtureHelpers;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ko2ic.api.Saying;

public class SayingTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private static final String FIXTURE = "fixtures/saying.json";

    @Test
    public void deserializesFromJSON() throws Exception {
        final Saying actual = new Saying(1, "Hello");
        final Saying expected = MAPPER.readValue(FixtureHelpers.fixture(FIXTURE), Saying.class);
        assertThat(actual).isEqualsToByComparingFields(expected);
    }

    @Test
    public void serializesToJSON() throws Exception {
        final Saying target = new Saying(1, "Hello");
        final String actual = MAPPER.writeValueAsString(target);
        assertThat(actual).isEqualTo(FixtureHelpers.fixture(FIXTURE));
    }

}
