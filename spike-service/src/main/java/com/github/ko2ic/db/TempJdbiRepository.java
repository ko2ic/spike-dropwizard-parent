package com.github.ko2ic.db;

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface TempJdbiRepository {

    @SqlQuery("select count(id) from temp")
    int count();

    @SqlUpdate("insert into temp (fullName) values ('fullName')")
    void create();

    @SqlUpdate("delete from temp")
    void deleteAll();
}
