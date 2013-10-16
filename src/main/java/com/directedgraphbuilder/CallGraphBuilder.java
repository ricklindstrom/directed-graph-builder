package com.directedgraphbuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.awt.Color;

import com.directedgraphbuilder.Graph.Node;

public class CallGraphBuilder {
    
    final static String DEFAULT_EXCLUSIONS = 
        "(java|org.apache|org.quartz|sun.reflect|sun.misc.Unsafe|com.directedgraphbuilder.CallGraphBuilder).*";
    public static String EXCLUSIONS = DEFAULT_EXCLUSIONS;

    final private Graph graph = new Graph();
    final private ExecutorService executor = Executors.newSingleThreadExecutor();
    //private String threadAnnotation = "";

    public void add(final StackTraceElement[] stackTrace) {
    	executor.execute(new Runnable() {
			public void run() {
				addImpl(stackTrace, null);
			}
    	});
    }
    
    
    private static boolean enabled = false;
    
    public static void enable() {
        enabled = true;
    }
    
    public static void disable() {
        enabled = false;
    }
    
    public static boolean isEnabled() {
        return enabled || true;
    }
    
    private void addImpl(StackTraceElement[] stackTrace, Throwable error) {

        String previousId = "start";
        Graph.Node startNode = graph.addNode(previousId, "START\n" + Thread.currentThread().getName());
        startNode.setColor(Color.GRAY);

        try {
        
            for(int i = stackTrace.length - 1; i >= 0; i--) {
                StackTraceElement element = stackTrace[i];
                
                String id = element.getClassName();
                String name = getSimpleName(element.getClassName());
                String field = element.getMethodName();
                String packageId = getPackageName(element.getClassName()); 
    
                if(!element.getClassName().matches(EXCLUSIONS) && !element.getMethodName().equals("doFilter") ) {
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
            
            //If we a graphing an exception stacktrace, include an edge to an error node
            if (error != null && previousId != null) {
                String errorId = error.getClass().getSimpleName() + "." + error.getLocalizedMessage().hashCode();
                String errorName = error.getClass().getSimpleName() + "\n" + error.getLocalizedMessage();
                Node errorNode = graph.addNode(errorId, errorName);
                errorNode.setColor(Color.PINK);
                graph.addEdge(previousId, errorId);
            }
            
        } catch(Exception ex) {
            //Bizarre Concurrency NPE? - Unable to compute value for LoaderKeyAndBuilder
            System.out.println("CallGraphBuilder: " + ex.getLocalizedMessage());
        }
    }
    
    private static String getSimpleName(String className) {
        return className.substring(className.lastIndexOf(".") + 1);
    }
    
    private static String getPackageName(String className) {
        if (className.startsWith("$Proxy")) {
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

    //private static ThreadLocal<CallGraphBuilder> threadLocal = new InheritableThreadLocal<CallGraphBuilder>();
    private static ThreadLocal<CallGraphBuilder> threadLocal = new ThreadLocal<CallGraphBuilder>();

    public static void clear() {
        if (threadLocal.get() != null) {
            threadLocal.set(null);
        }
    }
    
    public static void start() {
        if (isEnabled() && isThreadTrackable() && threadLocal.get() == null) {
            CallGraphBuilder callGraphBuilder = new CallGraphBuilder();
            threadLocal.set(callGraphBuilder);
        }
    }

    public static void capture() {
        if (isEnabled() && isThreadTrackable()) {
            CallGraphBuilder callGraphBuilder = threadLocal.get();
            if(callGraphBuilder != null) {
                callGraphBuilder.addImpl(Thread.currentThread().getStackTrace(), null);
            } else {
                // If start has not been explicitly called, then don't capture the current thread
            }
        }
    }

    public static void capture(Throwable ex) {
        if (ex == null) {
            capture();
        } else if (isEnabled() && isThreadTrackable()) {
            CallGraphBuilder callGraphBuilder = threadLocal.get();
            if(callGraphBuilder != null) {
                callGraphBuilder.addImpl(ex.getStackTrace(), ex);
            } else {
                // If start has not been explicitly called, then don't capture the current thread
            }
        } 
    }

    public static Graph stop() {
        if (!isThreadTrackable()) {
            return null;
        }
        
//        if (!enabled) {
//            System.out.println("Ignoring stop() while disabled");
//            return null;
//        }
        
//        if (onlyLogImportant && !isThreadImportant()) {
//            
//        }
        
        CallGraphBuilder callGraphBuilder = threadLocal.get();
        threadLocal.set(null);
        if(callGraphBuilder == null) {
            System.out.println("INFO: stop() was called without start() for Thread " + Thread.currentThread().getName());
            return null;
        }

        return callGraphBuilder.stopImpl();
    }

    private Graph stopImpl() {
    	try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {}
    	return graph;
    }
    
    
    private static boolean isThreadTrackable() {

        String threadName = Thread.currentThread().getName().toLowerCase();
        
        if (//!threadName.contains("jsp") 
            !threadName.contains(".gif") 
         && !threadName.contains(".png") 
         //&& !threadName.contains(".js")
         && !threadName.contains(".css")
         //&& !threadName.contains("photo")
         && !threadName.contains("javascript") 
         && !threadName.contains("css")
         //&& !threadName.contains("soap")
         && !threadName.contains("jiffy")
         ) {
            return true;
        } else {
            return false;
        }
    }

    public static CallGraphBuilder get() {
    	return threadLocal.get();
    }
    
    public static void annotateThread(String name) {
        //threadAnnotation = name;
        if (isEnabled()) {
            CallGraphBuilder callGraphBuilder = threadLocal.get();
            Graph graph = callGraphBuilder.buildGraph();
            if (graph.getNodes().containsKey("start")) {
                graph.getNodes().get("start").addField(name);
            }
        }
    }
    
}
