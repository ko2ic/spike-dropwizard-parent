package com.github.ko2ic;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.java8.Java8Bundle;
import io.dropwizard.java8.auth.basic.BasicAuthProvider;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import org.skife.jdbi.v2.DBI;

import com.github.ko2ic.auth.ExampleAuthenticator;
import com.github.ko2ic.core.Person;
import com.github.ko2ic.core.Template;
import com.github.ko2ic.db.PersonJdbiRepository;
import com.github.ko2ic.db.PersonRepository;
import com.github.ko2ic.db.TempJdbiRepository;
import com.github.ko2ic.health.TemplateHealthCheck;
import com.github.ko2ic.resources.HelloWorldResource;
import com.github.ko2ic.resources.PeopleJdbiResource;
import com.github.ko2ic.resources.PeopleResource;
import com.github.ko2ic.resources.ProtectedResource;
import com.github.ko2ic.resources.TempJdbiResource;
import com.github.ko2ic.resources.ViewResource;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {
    public static void main(String[] args) throws Exception {
        new HelloWorldApplication().run(args);
    }

    private final HibernateBundle<HelloWorldConfiguration> hibernateBundle = new HibernateBundle<HelloWorldConfiguration>(Person.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(HelloWorldConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        // bootstrap.addCommand(new RenderCommand());
        bootstrap.addBundle(new Java8Bundle());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<HelloWorldConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(HelloWorldConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle());
    }

    @Override
    public void run(HelloWorldConfiguration configuration, Environment environment) throws ClassNotFoundException {
        final PersonRepository repository = new PersonRepository(hibernateBundle.getSessionFactory());
        final Template template = configuration.buildTemplate();

        environment.healthChecks().register("template", new TemplateHealthCheck(template));

        environment.jersey().register(new BasicAuthProvider<>(new ExampleAuthenticator(), "SUPER SECRET STUFF"));
        environment.jersey().register(new HelloWorldResource(template));
        environment.jersey().register(new ViewResource());
        environment.jersey().register(new ProtectedResource());
        environment.jersey().register(new PeopleResource(repository));

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "jdbi");

        PersonJdbiRepository personJdbiRepository = jdbi.onDemand(PersonJdbiRepository.class);
        environment.jersey().register(new PeopleJdbiResource(personJdbiRepository));

        TempJdbiRepository tempJdbiRepository = jdbi.onDemand(TempJdbiRepository.class);
        environment.jersey().register(new TempJdbiResource(tempJdbiRepository));
    }
}
