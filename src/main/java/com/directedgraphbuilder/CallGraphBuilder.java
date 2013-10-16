package com.directedgraphbuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CallGraphBuilder {
    
    final static String DEFAULT_EXCLUSIONS = 
        "(java|org.apache|org.quartz|sun.reflect|sun.misc.Unsafe|com.directedgraphbuilder.CallGraphBuilder).*";
    static String EXCLUSIONS = DEFAULT_EXCLUSIONS;

    private Graph graph = new Graph();
	private ExecutorService executor = Executors.newSingleThreadExecutor();

    public void add(final StackTraceElement[] stackTrace) {
    	executor.execute(new Runnable() {
			public void run() {
				addImpl(stackTrace);
			}
    	});
    }
    
    private void addImpl(StackTraceElement[] stackTrace) {

        String previousId = null;
        for(int i = stackTrace.length - 1; i >= 0; i--) {
            StackTraceElement element = stackTrace[i];
            
            String id = element.getClassName();
            String name = getSimpleName(element.getClassName());
            String field = element.getMethodName();
            String packageId = getPackageName(element.getClassName()); 

            if(!element.getClassName().matches(EXCLUSIONS)) {
                if (!graph.contains(id)) {
                    Graph.Node node = graph.addNode(id, name);
                    node.setCluster(packageId);
                }
                
                if(!graph.getNodes().get(id).containsField(field)) {
                    graph.getNodes().get(id).addField(field);
                }
                
                if (previousId != null) {
                    graph.addEdge(previousId, id);
                }
    
                previousId = id;
            }
        }        
    }
    
    private static String getSimpleName(String className) {
        return className.substring(className.lastIndexOf(".") + 1);
    }
    
    private static String getPackageName(String className) {
        if ("$Proxy0".equals(className)) {
            return "proxy";
        } else {
            try {
                return className.substring(0, className.lastIndexOf("."));
            } catch (Exception e) {
                throw new RuntimeException("Could not get package name from " + className);
            }
        }
    }

    public Graph buildGraph() {
        return graph;
    }

    private static ThreadLocal<CallGraphBuilder> threadLocal = new ThreadLocal<CallGraphBuilder>();
    
    public static void start() {
        CallGraphBuilder callGraphBuilder = new CallGraphBuilder(); 
        threadLocal.set(callGraphBuilder);        
    }

    public static void capture() {
        CallGraphBuilder callGraphBuilder = threadLocal.get();
        if(callGraphBuilder == null) {
            throw new IllegalStateException("start() must be called before capture()");
        }
        callGraphBuilder.add(Thread.currentThread().getStackTrace());
    }

    public static Graph stop() {
        CallGraphBuilder callGraphBuilder = threadLocal.get();
        if(callGraphBuilder == null) {
            throw new IllegalStateException("start() must be called before stop()");
        }
        threadLocal.set(null);
        return callGraphBuilder.stopImpl();
    }

    private Graph stopImpl() {
    	try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException ignore) { }
    	
    	return graph;
    }


}
