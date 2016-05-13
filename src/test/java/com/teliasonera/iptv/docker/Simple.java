package com.teliasonera.iptv.docker;


import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.List;

import io.fabric8.docker.api.model.NetworkResource;
import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;
import io.fabric8.docker.dsl.network.AllFiltersInterface;
import io.fabric8.docker.dsl.network.NetworkInterface;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Simple {

    private DockerClient client;

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
    public void listAllImages() {
        client.image().list().allImages().stream().filter(i -> !i.getRepoTags().get(0).equals("<none>:<none>"))
        .forEach(i -> System.out.println(i.getRepoTags()));
    }

    @Test
    public void listAllContainers() {
        client.container().list().all().stream().forEach(c -> System.out.println(c.toString()));
    }



}
