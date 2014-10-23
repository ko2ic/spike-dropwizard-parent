package com.github.ko2ic.resources;

import io.dropwizard.jersey.params.LongParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.skife.jdbi.v2.Transaction;
import org.skife.jdbi.v2.TransactionStatus;

import com.github.ko2ic.core.Person;
import com.github.ko2ic.db.PersonJdbiRepository;
import com.sun.jersey.api.NotFoundException;

@Path("/people/jndi")
@Produces(MediaType.APPLICATION_JSON)
public class PeopleJdbiResource {

    private final PersonJdbiRepository repository;

    public PeopleJdbiResource(PersonJdbiRepository peopleDAO) {
        this.repository = peopleDAO;
    }

    @GET
    public List<Person> listPeople() {
        return repository.findAll();
    }

    @GET
    @Path("/{personId}")
    public Person getPerson(@PathParam("personId") LongParam personId) {
        final Optional<Person> person = repository.findById(personId.get());
        if (!person.isPresent()) {
            throw new NotFoundException("{status:notfound}");
        }
        return person.get();
    }

    @POST
    public List<Person> createBatch() {
        repository.inTransaction(new Transaction<Void, PersonJdbiRepository>() {
            @Override
            public Void inTransaction(PersonJdbiRepository transactional, TransactionStatus status) throws Exception {
                List<Person> people = new ArrayList<Person>();
                people.add(new Person("ko2ic", "batch1"));
                people.add(new Person("ko2ic", "batch2"));
                repository.createMany(people.iterator());
                return null;
            }
        });
        return listPeople();
    }

    @POST
    @Path("/withTemp")
    public String createWithTemp() {
        // if this class should be transaction border, you must use "inTransaction" method.
        List<Person> people = new ArrayList<Person>();
        people.add(new Person("ko2ic", "batch3"));
        people.add(new Person("ko2ic", "batch4"));
        int count = repository.createWithTemp(people.iterator());

        return String.format("{\"count\": %d}", count);
    }

    @DELETE
    public String delete() {
        repository.deleteAll();
        return "{\"status\": \"success\"}";
    }
}
