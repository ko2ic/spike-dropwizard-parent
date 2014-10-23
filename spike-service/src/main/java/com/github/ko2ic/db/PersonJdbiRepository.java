package com.github.ko2ic.db;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import com.github.ko2ic.core.Person;

public abstract class PersonJdbiRepository implements Transactional<PersonJdbiRepository> {

    @SqlQuery("select id, fullName, jobTitle from people where id = :id")
    @Mapper(PersonJdbiMapper.class)
    public abstract Optional<Person> findById(@Bind("id") Long id);

    @SqlQuery("select id, fullName, jobTitle from people")
    @Mapper(PersonJdbiMapper.class)
    public abstract List<Person> findAll();

    @SqlBatch("insert into people (fullName, jobTitle) values (:fullName, :jobTitle)")
    public abstract void createMany(@BindBean Iterator<Person> iterator);

    @SqlUpdate("delete from people")
    public abstract void deleteAll();

    @CreateSqlObject
    public abstract TempJdbiRepository createTempJdbiRepository();

    @Transaction
    public int createWithTemp(Iterator<Person> iterator) {
        createMany(iterator);
        TempJdbiRepository repository = createTempJdbiRepository();
        repository.create();
        // throw new IllegalArgumentException();
        return repository.count();
    }
}
