package com.directedgraphbuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Formatter {

    protected String q(String value) { return "\"" + value + "\""; }

    
    public abstract String format();

    public File save(String filename) {
        try {
            return saveInternal(filename);
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
    
    private File saveInternal(String filename) throws IOException {
        File file = new File(filename);
        
        System.out.println("Graph saving to: " + file.getCanonicalPath());

        FileWriter fw = null; 
        try {
            fw = new FileWriter(file);
            String contents = format();
            fw.append(contents);
            return file;
        } finally {
            if (fw != null) {
                fw.close();
            }
        }
    }
    
}
