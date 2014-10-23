package com.github.ko2ic.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.github.ko2ic.core.Person;

public class PersonJdbiMapper implements ResultSetMapper<Person> {

    @Override
    public Person map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Person person = new Person(r.getInt("id"), r.getString("fullName"), r.getString("jobTitle"));
        return person;
    }
}
