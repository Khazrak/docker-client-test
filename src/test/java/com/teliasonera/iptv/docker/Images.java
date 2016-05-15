package com.teliasonera.iptv.docker;

import io.fabric8.docker.api.model.Image;
import io.fabric8.docker.api.model.ImageDelete;
import io.fabric8.docker.api.model.ImageInspect;
import io.fabric8.docker.api.model.SearchResult;
import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;
import io.fabric8.docker.dsl.OutputHandle;
import io.fabric8.docker.dsl.image.ForceAndPruneNoInterface;
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

import static org.assertj.core.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Images {

    private static final String IMAGE_NAME = "alpine:3.2";
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
    public void testA_list() {
        List<Image> images = client.image().list().allImages();
        for (Image image : images) {
            logger.info(image.getRepoTags().get(0));
        }
    }

    @Test
    public void testB_inspect() {
        ImageInspect inspect = client.image().withName(IMAGE_NAME).inspect();
        logger.info(inspect.getRepoTags().toString());
        assertThat(inspect.getSize()).isGreaterThan(0);
    }

    @Test
    public void testC_search() {
        List<SearchResult> mysql = client.image().search("mysql");
        SearchResult official = null;
        boolean found = false;
        for(SearchResult s : mysql) {
            if(s.getIsOfficial()) {
                found = true;
                official = s;
                break;
            }
        }
        logger.info("Found official Mysql image: {}, {}", found,official);
        assertThat(found).isEqualTo(true);
    }

    @Test
    public void testD_delete() {
        List<ImageDelete> imageDeletes = client.image().withName(IMAGE_NAME).delete().andPrune();
        logger.info(imageDeletes.toString());
        assertThat(imageDeletes.size()).isGreaterThan(1);
    }

    @Test
    public void testE_pull() {
        OutputHandle outputHandle = client.image().withName(IMAGE_NAME).pull().redirectingOutput().fromRegistry();
        logger.info("pulling image {}", IMAGE_NAME);
    }
}
