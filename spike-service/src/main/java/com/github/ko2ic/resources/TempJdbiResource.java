package com.github.ko2ic.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.ko2ic.db.TempJdbiRepository;

@Path("/temp/jndi")
@Produces(MediaType.APPLICATION_JSON)
public class TempJdbiResource {

    private final TempJdbiRepository repository;

    public TempJdbiResource(TempJdbiRepository tempRepository) {
        this.repository = tempRepository;
    }

    @GET
    public String listPeople() {
        return String.format("{\"count\": %d}", repository.count());
    }

    @DELETE
    public String delete() {
        repository.deleteAll();
        return "{\"status\": \"success\"}";
    }
}
