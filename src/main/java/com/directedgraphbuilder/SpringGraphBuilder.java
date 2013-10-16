package com.directedgraphbuilder;

import java.util.Collection;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;

/**
 * Builds a graph from a given spring Bean Factory
 */
public class SpringGraphBuilder {

	final private ConfigurableListableBeanFactory beanFactory;
    private boolean clustersOnly = false;
    private boolean obfuscate = false;

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
	public SpringGraphBuilder(ConfigurableListableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	/**
	 * Static factory method to build a graph from a given spring Bean Factory
	 * @param beanFactory Bean factory to examine
	 * @return Graph containing beans as nodes and references as edges.
	 */
    public static Graph buildGraph(ConfigurableListableBeanFactory beanFactory) {
        SpringGraphBuilder builder = new SpringGraphBuilder(beanFactory);
        Graph graph = builder.buildGraph();
        return graph;
    }

	/**
	 * Builds a graph from a given spring Bean Factory
	 * @param beanFactory Bean factory to examine
	 * @return Graph containing beans as nodes and references as edges.
	 */
    public Graph buildGraph() {
        Graph graph = new Graph();

        try {
	        for (final String beanId : beanFactory.getBeanDefinitionNames()) {	        		
	            final BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanId);
	            final MutablePropertyValues values = beanDefinition.getPropertyValues();
	            //beanDefinition.getConstructorArgumentValues();
	
	            String beanName = (this.isObfuscate() ? Obfuscater.rot13(beanId) : beanId);
	            Graph.Node node = graph.addNode(beanId, beanName).setCluster(clean(getClusterDescription(beanDefinition)));
	            
	            //Build the label for this bean
	            if (!beanDefinition.isAbstract()) {
	                try {
	                    Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
	                    // If the class name is different from the bean name, than include it in the node label
	                    if (!beanId.equalsIgnoreCase(beanClass.getSimpleName())) {
	        	            String fieldName = (this.isObfuscate() ? Obfuscater.rot13(beanClass.getSimpleName()) : beanClass.getSimpleName());
	                    	node.addField(fieldName);
	                    }
	                } catch (ClassNotFoundException e) {
	                    //Ignore
	                } catch (NullPointerException npe) {
	                	//Not Sure What this means yet
	                }
	            }
	
	            //Add more details to the node label
	            for (final PropertyValue v : values.getPropertyValues()) {
	                addFieldToNode(node, v);
	            }

	            //If the bean has a parent, create an edge to it
	            if (beanDefinition.getParentName() != null) {
	                node.addEdge(beanDefinition.getParentName());
	            }
	
	            //Create an edge to each of the referenced bean nodes 
	            for (final PropertyValue v : values.getPropertyValues()) {
	                addGraphEdge(graph, beanId, v);
	            }
	        }

	        //Constructor wiring
	        for (final String beanId : beanFactory.getBeanDefinitionNames()) {
	        	final BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanId);
	            @SuppressWarnings("unchecked")

	            Collection<ConstructorArgumentValues.ValueHolder> constructorArguments = 
	                     beanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().values();
	            for (final ConstructorArgumentValues.ValueHolder v : constructorArguments) {
	                addGraphEdge(graph, beanId, v);
	            }
	            Collection<ConstructorArgumentValues.ValueHolder> genericArgumentValues =
	            		beanDefinition.getConstructorArgumentValues().getGenericArgumentValues();
                for (final ConstructorArgumentValues.ValueHolder v : genericArgumentValues) {
                	addGraphEdge(graph, beanId, v);
                }
	        }

	        
        } catch (Exception ex) {
        	System.out.print("SpringGraphBuilder encountered an exception. " + ex.getLocalizedMessage());
        }
        
        return graph;
    }

	/**
	 * @param node
	 * @param v
	 */
	private void addFieldToNode(Graph.Node node, final PropertyValue v) {
		if (v.getValue() instanceof TypedStringValue) {
		    if (!v.getName().toUpperCase().contains("PASSWORD")) {
		        node.addField(v.getName() + "=" +  ((TypedStringValue) v.getValue()).getValue());
		    } else {
		        node.addField(v.getName() + "=********");                        
		    }
		} else if (v.getValue() instanceof String | v.getValue() instanceof Boolean | v.getValue() instanceof Integer) {
		    node.addField(v.getName() + "=" +  v.getValue());
		} else if (v.getValue() instanceof BeanDefinitionHolder) {
		    node.addField(v.getName() + "=" +  ((BeanDefinitionHolder) v.getValue()).getBeanDefinition().getBeanClassName());
		} else if (!(v.getValue() instanceof RuntimeBeanReference)) {
		    node.addField(v.getName() + "=(" +  v.getValue().getClass().getSimpleName() + ")");
		} else {
		    //This is a RuntimeBeanReference which is handled as an edge
		}
	}

	/**
	 * @param graph Graph
	 * @param beanId String
	 * @param v PropertyValue
	 */
	private static void addGraphEdge(final Graph graph, final String beanId, final PropertyValue v) {
		if (v.getValue() instanceof RuntimeBeanReference) {
		    graph.addEdge(beanId, ((RuntimeBeanReference) v.getValue()).getBeanName());
		} else if (v.getValue() instanceof ManagedList) {
		    ManagedList list = (ManagedList) v.getValue();
		    for (Object o : list) {
		        if (o instanceof RuntimeBeanReference) {
		            graph.addEdge(beanId, ((RuntimeBeanReference) o).getBeanName());
		        }
		    }
		} else if (v.getValue() instanceof ManagedMap) {
		    ManagedMap map = (ManagedMap) v.getValue();
		    for (Object value : map.values()) {
		        if (value instanceof RuntimeBeanReference) {
		            graph.addEdge(beanId, ((RuntimeBeanReference) value).getBeanName());
		        }
		    }
		}
	}
    
	/**
	 * @param graph Graph
	 * @param beanId String
	 * @param v ConstructorArgumentValues.ValueHolder v
	 */
	private static void addGraphEdge(final Graph graph, final String beanId, final ConstructorArgumentValues.ValueHolder v) {
		if (v.getValue() instanceof BeanDefinition) {
			//In most cases this should be a field instead of an edge (?) 
//			graph.addEdge(beanId, ((BeanDefinition) v.getValue()).getBeanClassName());
		} else if (v.getValue() instanceof RuntimeBeanReference) {
		    graph.addEdge(beanId, ((RuntimeBeanReference) v.getValue()).getBeanName());
		} else if (v.getValue() instanceof ManagedList) {
		    ManagedList list = (ManagedList) v.getValue();
		    for (Object o : list) {
		        if (o instanceof RuntimeBeanReference) {
		            graph.addEdge(beanId, ((RuntimeBeanReference) o).getBeanName());
		        }
		    }
		} else if (v.getValue() instanceof ManagedMap) {
		    ManagedMap map = (ManagedMap) v.getValue();
		    for (Object value : map.values()) {
		        if (value instanceof RuntimeBeanReference) {
		            graph.addEdge(beanId, ((RuntimeBeanReference) value).getBeanName());
		        }
		    }
		} else {
			System.out.println("Unhandled constructor argument type");
		}
	}
    
    /**
     * 
     * @param bean bean with a resource description like [file:jboss/server/tmp3805services-SNAPSHOT-exp.war/WEB-INF/classes/services-context.xml] 
     * @return a description like services-context.xml
     */
    private static String getClusterDescription(BeanDefinition bean) {
        if (bean == null || bean.getResourceDescription() == null) {
            return "Internal";
        }
        String desc = bean.getResourceDescription();
        if (desc.contains("[")) {
            desc = desc.substring(desc.lastIndexOf("[") + 1);
        }
        if (desc.contains("/")) {
            desc = desc.substring(desc.lastIndexOf("/") + 1);
        }
        if (desc.endsWith("]")) {
            desc = desc.substring(0, desc.length() - 1);
        }
        return desc;
    }

    private static String clean(String value) {
        return value.replaceAll("[\\W]", "_");
    }

}
