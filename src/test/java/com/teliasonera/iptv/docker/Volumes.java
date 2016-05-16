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

import io.fabric8.docker.api.model.Volume;
import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;

import static org.assertj.core.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Volumes {

    private static final String VOLUME_NAME = "test-vol";
    private Logger logger = LoggerFactory.getLogger(Volumes.class);

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
    public void testA_create() {
        Volume volumeCreate = client.volume().createNew().withName(VOLUME_NAME).done();
        logger.info(volumeCreate.toString());
        assertThat(volumeCreate.getName()).isEqualToIgnoringCase(VOLUME_NAME);
    }

    @Test
    public void testB_list() {
        List<Volume> volumeList = client.volume().list().all();
        logger.info(volumeList.toString());
        boolean volumeFound = false;
        for(Volume v : volumeList) {
            if(v.getName().equalsIgnoreCase(VOLUME_NAME)) {
                volumeFound = true;
            }
        }
        assertThat(volumeList.size()).isGreaterThan(0);
        assertThat(volumeFound).isEqualTo(true);
    }

    @Test
    public void testC_inspect() {
        Volume volumeInspect = client.volume().withName(VOLUME_NAME).inspect();
        logger.info("Trying to inspect {}, got inspection of: {}",VOLUME_NAME,volumeInspect.getName());
        assertThat(volumeInspect.getName()).isEqualToIgnoringCase(VOLUME_NAME);
    }


    @Test
    public void testZ_remove() {
        boolean removed = client.volume().withName(VOLUME_NAME).delete();
        logger.info("Volume {} removed: {}",VOLUME_NAME,removed);
        assertThat(removed).isEqualTo(true);
    }


}
