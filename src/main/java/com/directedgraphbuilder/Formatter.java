package com.directedgraphbuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public abstract class Formatter {

    protected String q(Object value) { return "\"" + value + "\""; }

    public abstract void format(Appendable appendable) throws IOException;
        
    public abstract String format();

    public File save(String filename) {
        try {
            return saveInternal(filename);
        } catch(Exception ioe) {
            throw new RuntimeException(ioe);
        }
    }
    
    private File saveInternal(String filename) throws Exception {
        File file = new File(filename);
        
        System.out.println("Graph saving to: " + file.getCanonicalPath());
        
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            format(writer);
            return file;
        } finally {
            if (writer !=null) writer.close();
        }
    }
    
}
