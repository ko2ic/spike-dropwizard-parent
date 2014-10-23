package com.github.ko2ic.resources;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import io.dropwizard.testing.junit.ResourceTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.github.ko2ic.core.Person;
import com.github.ko2ic.db.PersonRepository;

public class PeopleResourceTest {

    private static final PersonRepository dao = mock(PersonRepository.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder().addResource(new PeopleResource(dao)).build();

    private final Person person = new Person(1, "ko2ic", "job");

    @Before
    public void setup() {
        when(dao.findById(Long.parseLong("1"))).thenReturn(Optional.ofNullable(person));
    }

    @Test
    public void testGetPerson() {
        assertThat(resources.client().resource("/people/1").get(Person.class)).isEqualsToByComparingFields(person);
        verify(dao).findById(Long.parseLong("1"));
    }

}
