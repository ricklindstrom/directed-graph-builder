package com.directedgraphbuilder;

import java.awt.Color;
import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Graph {

    private Formatter formatter;
    
    /** Maps a cluster id to a list of nodes */
    private Map<String, List<Node>> clusters = new HashMap<String, List<Node>>();

    private String rankdir = "LR";
    
    /** Maps node ids to Nodes */
    private Map<String, Node> nodes = new HashMap<String, Node>();
    
    /** Maps identifiers to a color */
    private Map<String, Color> colorLegend = new HashMap<String, Color>(); 
    
    
    public Graph() {
        //This should be injected, but it is not worth it to include a DI framework just for this.
        formatter = new GraphvizFormatter(this);
    }

    public Graph setLeftRight() {
        rankdir = "LR";
        return this;
    }
    
    public Graph setTopDown() {
        rankdir = "TD";
        return this;
    }
    
    public Node addNode(String id, String name) {
        Node node = new Node(escape(id), name);
        nodes.put(escape(id), node);
        return node;
    }

    public File save(String filename) {
        return formatter.save(filename);
    }
    
    public File save() {
        File f = new File(".");
        return save(f.getAbsolutePath() + "/" + generateGraphFilename());
    }

    private String generateGraphFilename() {
        String filename = Thread.currentThread().getName();
        filename = filename.replace('/', '-').replace(' ', '-').replace(';', '-');
        Format format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-S");
        return format.format(new java.util.Date()) + "-" + filename + ".dot";
    }

    public Graph buildClusterGraph() {
    	final Graph clusterGraph = new Graph();
    	
    	if (this.rankdir.equals("TD")) {
    		clusterGraph.setTopDown();
    	}
    	
    	for (Entry<String, List<Node>> clusterEntry : this.clusters.entrySet()) {
    		String sourceClusterId = clusterEntry.getKey();
    		Color sourceClusterColor = colorLegend.get(sourceClusterId);
    		clusterGraph.addNode(sourceClusterId).setColor(sourceClusterColor);

    		List<Node> clusterNodes = clusterEntry.getValue();
    		for (Graph.Node node : clusterNodes) {
    			if (node.isClustered()) {
	    			for (String target : node.getEdges()) {
	    				Node targetNode = this.nodes.get(target);
	    				if (targetNode.isClustered() && !sourceClusterId.equals(targetNode.clusterId)) {
	        				clusterGraph.addEdge(sourceClusterId, targetNode.clusterId).setColor(sourceClusterColor);	    					
	    				}
	    			}
    			}
    		}
    	}

    	return clusterGraph;
    }
    
    @Override
    public String toString() {
        return formatter.format();
    }

    public Node addEdge(String sourceId, String targetId) {
        String eSourceId = escape(sourceId);
        String eTargetId = escape(targetId);        
    	
        if (!nodes.containsKey(eSourceId)) {
            this.addNode(eSourceId);
        }

        if (!nodes.containsKey(eTargetId)) {
            this.addNode(eTargetId);
        }

        nodes.get(eSourceId).addEdge(eTargetId);
        return nodes.get(eTargetId);
    }
    
    private List<Node> getCluster(String clusterId) {
        List<Node> cluster = clusters.get(escape(clusterId)); 
        if (cluster == null) {
            cluster = new ArrayList<Node>();
            clusters.put(escape(clusterId), cluster);
        }
        return cluster;
    }

    private Color getColorForString(String value) {
        if (!colorLegend.containsKey(value)) {
            colorLegend.put(value, ColorUtil.hashColor(value));
        }
        return colorLegend.get(value);
    }
    

    public Node addNode(String id) {
        return addNode(id, id);
    }

    private String escape(String string) {
      String esc = string != null
              ? string.replace("\"", "\\\"").replace("'","\\'").replace("\n","\\n").replace("$","_").replace(".", "_").replace("<", "~").replace(">", "~") : "";
      if (esc.length() > 100) {
          return esc.substring(0, 100);
      }
      return esc;
    }
    
    class Node {
        private String id;
        private String simpleName;
        private String name;
        private Color color = null;
        private Set<String> edges = new HashSet<String>();
        private String clusterId = null;

        public Node(String id, String name) {
            this.id = escape(id);
            this.simpleName = escape(name);
            this.name = escape(name);
        }
        public boolean isClustered() {
            return (clusterId != null);
        }
        public Node addEdge(String targetId) {
            if (!nodes.containsKey(escape(targetId))) {
                addNode(escape(targetId));
            }
            edges.add(escape(targetId));
            return this;
        }
        public String getId() {return id;}
        public String getName() { return name; }
        public String getSimpleName() { return simpleName; }
        
        public Node setColor(Color color) {
            this.color = color;
            return this;
        }
        
        public Color getColor() { return color; }
        
        public Node setCluster(String clusterId) {
            if(isClustered()) {
                throw new IllegalStateException("Node has already been assigned to a Cluster");
            }
            setColor(getColorForString(clusterId));
            this.clusterId = clusterId;
            getCluster(clusterId).add(this);
            return this;
        }

        public Node addField(String field) {
        	this.name += " | " + escape(field);
        	return this;
        }

        public boolean containsField(String field) {
            return this.name.contains("| " + escape(field));
        }

        public Set<String> getEdges() { return edges; }

    }

    String getRankdir() { return rankdir; }

    Map<String, List<Node>> getClusters() { return this.clusters; }

    @Deprecated public Map<String, Node> getNodes() { return this.nodes; }

    public Node getNode(String id) { return this.nodes.get(escape(id)); }
    
    boolean contains(String id) { return this.nodes.containsKey(escape(id)); }

}

