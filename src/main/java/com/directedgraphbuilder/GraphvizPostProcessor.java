package com.directedgraphbuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * 
 */
public class GraphvizPostProcessor implements BeanFactoryPostProcessor {
    
    private static final Log log = LogFactory.getLog(GraphvizPostProcessor.class);

    private String filename = "spring-context.dot"; 
    private boolean clustersOnly = false;
    private boolean obfuscate = false;
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getFilename() {
        return filename;
    }

	public void setClustersOnly(boolean clustersOnly) {
		this.clustersOnly = clustersOnly;
	}
	public boolean isClustersOnly() {
		return clustersOnly;
	}    
	public void setObfuscate(boolean obfuscate) {
		this.obfuscate = obfuscate;
	}
	public boolean isObfuscate() {
		return obfuscate;
	}
    
    /**
     * {@inheritDoc}
     * 
     * @param beanFactory - the spring beanFactory
     * @throws BeansException - {@link BeansException}
     */
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        
        //TODO: Allow some kind of include/exclude support for selecting the different context files. 

    	SpringGraphBuilder builder = new SpringGraphBuilder(beanFactory);
    	builder.setObfuscate(isObfuscate());
    	builder.setClustersOnly(isClustersOnly());
        Graph g = builder.buildGraph();
        
        if (isClustersOnly()) {
        	Graph clusterOnlyGraph = g.buildClusterGraph();
        	clusterOnlyGraph.save(filename);
        } else {
        	g.save(filename);	
        }
        
    	
    }

}
