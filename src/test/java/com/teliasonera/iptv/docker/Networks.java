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

import io.fabric8.docker.api.model.NetworkCreateResponse;
import io.fabric8.docker.api.model.NetworkResource;
import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;
import io.fabric8.docker.dsl.network.AllFiltersInterface;
import io.fabric8.docker.dsl.network.NetworkInterface;

import static org.assertj.core.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Networks {

    private Logger logger = LoggerFactory.getLogger(Networks.class);

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
        NetworkCreateResponse res = client.network().createNew().withName(NETWORK_NAME).withDriver("bridge").done();
        logger.info(res.toString());
        assertThat(res.getId()).isNotEmpty();
    }

    @Test
    public void inspect() {

        NetworkResource inspect = client.network().withName(NETWORK_NAME).inspect();
        logger.info(inspect.toString());
        logger.info("Inspecting network: {}",inspect.getName());
        assertThat(inspect.getName()).isEqualToIgnoringCase(NETWORK_NAME);
    }

    @Test
    public void listAll() {

        NetworkInterface network = client.network();
        AllFiltersInterface<List<NetworkResource>> list = network.list();
        List<NetworkResource> all = list.all();
        boolean alfaNetFound = false;
        for(NetworkResource nr : all) {
            if(nr.getName().equalsIgnoreCase(NETWORK_NAME)) {
                alfaNetFound = true;
            }
        }
        assertThat(alfaNetFound).isEqualTo(true);
        assertThat(client.network().list().all().size()).isGreaterThan(0);
    }

    @Test
    public void remove() {
        boolean deleted = client.network().withName(NETWORK_NAME).delete();
        logger.info("{} is deleted: {}",NETWORK_NAME,deleted);
        assertThat(deleted).isEqualTo(true);
    }

}
