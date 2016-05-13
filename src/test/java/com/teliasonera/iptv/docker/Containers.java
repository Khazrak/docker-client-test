package com.teliasonera.iptv.docker;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import io.fabric8.docker.api.model.Container;
import io.fabric8.docker.api.model.ContainerCreateRequestBuilder;
import io.fabric8.docker.api.model.ContainerCreateResponse;
import io.fabric8.docker.api.model.ContainerInspect;
import io.fabric8.docker.api.model.EditableContainerCreateRequest;
import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;

import static org.assertj.core.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Containers {

    private static final String CONTAINER_NAME = "my_container";
    private static final String IMAGE_NAME = "ubuntu:14.04";
    private Logger logger = LoggerFactory.getLogger(Containers.class);

    private DockerClient client;

    private static final String NETWORK_NAME = "alfa-net";

    @Before
    public void createClient() {
        Config config = new ConfigBuilder()
                .withDockerUrl("unix:///var/run/docker.sock")
                .build();

        client = new DefaultDockerClient(config);
    }

    @After
    public void closeClient() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void create() {
        ContainerCreateRequestBuilder builder = new ContainerCreateRequestBuilder();
        EditableContainerCreateRequest ls = builder.withImage(IMAGE_NAME).withName(CONTAINER_NAME).addToCmd("ls").build();
        ContainerCreateResponse containerCreateResponse = client.container().create(ls);
        logger.info(containerCreateResponse.toString());
    }

    @Test
    public void inspect() {
        ContainerInspect inspect = client.container().withName(CONTAINER_NAME).inspect();
        logger.info(inspect.toString());
        assertThat(inspect.getName()).isEqualToIgnoringCase("/"+CONTAINER_NAME);
    }

    @Test
    public void listAll() {
        List<Container> containers = client.container().list().all();
        logger.info("Container count: {}", containers.size());
        assertThat(containers.size()).isGreaterThan(0);
    }


    @Test
    public void remove() {
        boolean removed = client.container().withName(CONTAINER_NAME).remove();
        logger.info("Container {} removed: {}",CONTAINER_NAME,removed);
        assertThat(removed).isEqualTo(true);
    }

    
}
