package com.github.ko2ic.resources;

import static org.fest.assertions.api.Assertions.*;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.testing.FixtureHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.exception.LockException;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.ko2ic.HelloWorldApplication;
import com.github.ko2ic.HelloWorldConfiguration;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class PeopleResourceIntegrationTest {

    private static final String FIXTURE_PATH = "fixtures/integrations/";

    private static Database database;

    private Liquibase migrations;

    @ClassRule
    public static final DropwizardAppRule<HelloWorldConfiguration> RULE = new DropwizardAppRule<>(HelloWorldApplication.class, "example.yml");

    @BeforeClass
    public static void beforeClass() throws SQLException, LiquibaseException {
        database = createDatabase();
    }

    @AfterClass
    public static void afterClass() throws DatabaseException, LockException {
        database.close();
        database = null;
    }

    @Before
    public void before() throws DatabaseException, SQLException, LiquibaseException {
        migrations = createLiquibase("migrations.xml");
        String context = null;
        migrations.update(context);
    }

    @After
    public void after() throws DatabaseException, LockException {
        migrations.dropAll();
    }

    @Test
    public void testGetPerson() throws JsonParseException, JsonMappingException, IOException, DatabaseException, SQLException, LiquibaseException {

        Liquibase data = createLiquibase("data.xml");
        data.update("testGetPerson");

        Client client = new Client();
        ClientResponse response = client.resource(String.format("http://localhost:%d/people/2", RULE.getLocalPort())).get(ClientResponse.class);

        assertThat(response.getStatus()).isEqualTo(200);

        String entity = response.getEntity(String.class);
        assertThat(entity).isEqualTo(FixtureHelpers.fixture(FIXTURE_PATH + "getPerson.json"));

    }

    private Liquibase createLiquibase(String migrations) throws SQLException, DatabaseException, LiquibaseException {
        Liquibase liquibase = new Liquibase(migrations, new ClassLoaderResourceAccessor(), database);
        return liquibase;
    }

    private static Database createDatabase() throws SQLException, DatabaseException {
        DataSourceFactory dataSourceFactory = RULE.getConfiguration().getDataSourceFactory();
        Properties info = new Properties();
        info.setProperty("user", dataSourceFactory.getUser());
        info.setProperty("password", dataSourceFactory.getPassword());
        org.h2.jdbc.JdbcConnection h2Conn = new org.h2.jdbc.JdbcConnection(dataSourceFactory.getUrl(), info);
        JdbcConnection conn = new JdbcConnection(h2Conn);
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(conn);
        return database;
    }
}
