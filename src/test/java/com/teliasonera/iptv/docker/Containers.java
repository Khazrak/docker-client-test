package com.teliasonera.iptv.docker;

import io.fabric8.docker.api.model.*;
import io.fabric8.docker.dsl.OutputHandle;
import io.fabric8.docker.dsl.container.SinceContainerOutputErrorTimestampsTailingLinesFollowDisplayInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;

import static org.assertj.core.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Containers {

    private static final String CONTAINER_NAME = "my_container";
    private static final String IMAGE_NAME = "mongo:3.2.4";
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
    public void testA_create() {
        ContainerCreateRequestBuilder builder = new ContainerCreateRequestBuilder();
        EditableContainerCreateRequest ls = builder.withImage(IMAGE_NAME).withName(CONTAINER_NAME).build();
        ContainerCreateResponse containerCreateResponse = client.container().create(ls);
        logger.info(containerCreateResponse.toString());
    }

    @Test
    public void testB_inspect() {
        ContainerInspect inspect = client.container().withName(CONTAINER_NAME).inspect();
        logger.info(inspect.toString());
        ContainerState state = inspect.getState();
        logger.info("State: Running: {}, Stopped: {},",state.getRunning(),state.getStatus());
        assertThat(inspect.getName()).isEqualToIgnoringCase("/"+CONTAINER_NAME);
    }

    @Test
    public void testC_listAll() {
        List<Container> containers = client.container().list().all();
        logger.info("Container count: {}", containers.size());
        assertThat(containers.size()).isGreaterThan(0);
    }

    @Test
    public void testD_start() {
        boolean started = client.container().withName(CONTAINER_NAME).start();
        logger.info("Container started: {}",started);
        assertThat(started).isEqualTo(true);
    }

    @Test
    public void testE_top() {
        ContainerProcessList top = client.container().withName(CONTAINER_NAME).top();
        logger.info("Processes: {}",top.toString());
        assertThat(top).isNotNull();
    }

    @Test
    public void testF_stats() {
        Stats stats = client.container().withName(CONTAINER_NAME).stats();
        String stat = stats.toString();
        assertThat(stats).isNotNull();
        logger.info(stat);
    }

    @Test
    public void testG_kill() {
        boolean killed = client.container().withName(CONTAINER_NAME).kill();
        logger.info("Container {} killed: {}", CONTAINER_NAME, killed);
        assertThat(killed).isEqualTo(true);
    }

    @Test
    public void testH_restart() {
        boolean restarted = client.container().withName(CONTAINER_NAME).restart();
        logger.info("Container {} restarted: {}",CONTAINER_NAME,restarted);
        assertThat(restarted).isEqualTo(true);
    }


    @Test
    public void testG_logs() {
        OutputHandle outputHandle = client.container().withName(CONTAINER_NAME).logs().display();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(outputHandle.getOutput()))) {
            String line = "";
            while(line != null) {
                line = reader.readLine();
                logger.info(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testX_stop() {
        boolean stopped = client.container().withName(CONTAINER_NAME).stop();
        logger.info("Container {} stopped: {}",CONTAINER_NAME, stopped);
        assertThat(stopped).isEqualTo(true);
    }

    @Test
    public void testZ_remove() {
        boolean removed = client.container().withName(CONTAINER_NAME).remove();
        logger.info("Container {} removed: {}",CONTAINER_NAME,removed);
        assertThat(removed).isEqualTo(true);
    }

    
}
