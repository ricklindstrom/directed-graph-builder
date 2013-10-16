package com.directedgraphbuilder;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(locations = {
        "/spring-test1.xml",
        "/spring-test2.xml",
        "/spring-graphviz-postprocessor.xml"
        })
public class GraphvizPostProcessorTest extends AbstractJUnit4SpringContextTests {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void basicTest() {
        GraphvizPostProcessor p = (GraphvizPostProcessor) applicationContext.getBean("basicGraphvizPostProcessor");
        assertNotNull("basicGraphvizPostProcessor", p);
        assertNotNull("filename", p.getFilename());
        File file = new File(p.getFilename());
        assertTrue("file canRead", file.canRead());

    }
    
    @Test
    public void clusterOnlyGraphTest() {
        GraphvizPostProcessor p = (GraphvizPostProcessor) applicationContext.getBean("clusterOnlyGraphvizPostProcessor");
        assertNotNull("clusterOnlyGraphvizPostProcessor", p);
        assertNotNull("filename", p.getFilename());
        File file = new File(p.getFilename());
        assertTrue("file canRead", file.canRead());

    }

    @Test
    public void obfuscatedGraphTest() {
        GraphvizPostProcessor p = (GraphvizPostProcessor) applicationContext.getBean("obfuscatedGraphvizPostProcessor");
        assertNotNull("obfuscatedOnlyGraphvizPostProcessor", p);
        assertNotNull("filename", p.getFilename());
        File file = new File(p.getFilename());
        assertTrue("file canRead", file.canRead());
    }
}
