package util;

import java.io.PrintWriter;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import util.FileUtils;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.StringUtils;
import org.json.*;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

public class Express {
    private static Express express;
    private Map<String, Map<String, ArrayList<ExpressHandler>>> map;
    private Map<String, ArrayList<ExpressHandler>> use_map;

    public Express() {
        super();
        this.map = new HashMap<String, Map<String, ArrayList<ExpressHandler>>>();
        this.use_map = new HashMap<String, ArrayList<ExpressHandler>>();
    }

    private static synchronized Express create() {
        if(express == null) {
            express  = new Express();
        }
        return express;
    }

    public void get(String pattern, ExpressHandler... handler) {
        this.set("GET", pattern, handler);
    }

    public void post(String pattern, ExpressHandler... handler) {
        this.set("POST", pattern, handler);
    }

    public void delete(String pattern, ExpressHandler... handler) {
        this.set("DELETE", pattern, handler);
    }

    public void put(String pattern, ExpressHandler... handler) {
        this.set("PUT", pattern, handler);
    }

    public void use(String pattern, ExpressHandler... handler) {
        ArrayList<ExpressHandler> handlerArray = use_map.get(pattern);
        if(handlerArray == null) {
            handlerArray = new ArrayList<ExpressHandler>();
            use_map.put(pattern, handlerArray);
        }
        for (ExpressHandler handle : handler) {
            if(handle == null) {
                continue;
            }
            handlerArray.add(handle);
        }
    }

    protected void set(String method, String pattern, ExpressHandler... handler) {
        Map<String, ArrayList<ExpressHandler>> pattern_map = map.get(method);
        if(pattern_map == null) {
            pattern_map = new HashMap<String, ArrayList<ExpressHandler>>();
            map.put(method, pattern_map);
        }
        ArrayList<ExpressHandler> handlerArray = pattern_map.get(pattern);
        if(handlerArray == null) {
            handlerArray = new ArrayList<ExpressHandler>();
            pattern_map.put(pattern, handlerArray);
        }
        for (ExpressHandler handle : handler) {
            if(handle == null) {
                continue;
            }
            handlerArray.add(handle);
        }
    }

    private boolean execute(ArrayList<ExpressHandler> handler, HttpServletRequest req, HttpServletResponse res)
        throws ServletException, java.io.IOException {
        if(handler == null) {
            return true;
        }
        for(ExpressHandler handle : handler) {
            if(!handle.handle(req, res)) {
                return false;
            }
        }
        return true;
    }

    private boolean handle_helper(String path, Map<String, ArrayList<ExpressHandler>> handleMap, HttpServletRequest req, HttpServletResponse res)
        throws ServletException, java.io.IOException {
        if(!use_helper(path, req, res)) {
            return false;
        }
        if(handleMap != null && !execute(handleMap.get(path), req, res)) {
            return false;
        }
        return true;
    }

    private boolean use_helper(String path, HttpServletRequest req, HttpServletResponse res)
        throws ServletException, java.io.IOException {
        return execute(use_map.get(path), req, res);
    }

    protected void forward(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, java.io.IOException {
        String method = req.getMethod();
        String[] patterns = req.getPathInfo().split("/");
        String path = "/";
        Map<String, ArrayList<ExpressHandler>> handleMap = this.map.get(method);
        for(int i = 0; i < patterns.length; ++i) {
            String[] current_path = new String[patterns.length - i];
            System.arraycopy(patterns, i, current_path, 0, current_path.length);
            ExpressRequestWrapper requestWrapper = new ExpressRequestWrapper(req, StringUtils.join(current_path, "/"));
            ExpressResponseWrapper responseWrapper = new ExpressResponseWrapper(req, res);
            if(!use_helper(path, requestWrapper, responseWrapper)) {
                return;
            }
            if(path.charAt(path.length() - 1) == '/') {
                path += patterns[i];
            } else {
                path += '/' + patterns[i];
            }
        }
        ExpressRequestWrapper requestWrapper = new ExpressRequestWrapper(req, path);
        ExpressResponseWrapper responseWrapper = new ExpressResponseWrapper(req, res);
        if(!handle_helper(path, handleMap, requestWrapper, responseWrapper)) {
            return;
        }
        res.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    public static interface ExpressHandler {
        public boolean handle(HttpServletRequest req, HttpServletResponse res) throws ServletException, java.io.IOException;
    }

    public static class FileHandler implements ExpressHandler {
        protected String filepath;
        protected String content = null;
        public FileHandler(String filepath) {
            this.filepath = filepath;
        }

        public boolean handle(HttpServletRequest req, HttpServletResponse res) throws ServletException, java.io.IOException {
            if(content == null) {
                content = new String(Files.readAllBytes(Paths.get(filepath)), StandardCharsets.UTF_8);
                // content = new File(filepath).getAbsolutePath(); //FileUtils.readFile(filepath);
            }

            res.setHeader("Content-Type", "text/html");

            PrintWriter out = res.getWriter();
            out.print(content);
            return false;
        }
    }

    public static class StringHandler implements ExpressHandler {
        String content;
        String content_type;
        public StringHandler(String content) {
            this(content, "text/plain");
        }

        public StringHandler(String content, String content_type) {
            this.content = content;
            this.content_type = content_type;
        }

        public boolean handle(HttpServletRequest req, HttpServletResponse res) throws ServletException, java.io.IOException {
            res.setHeader("Content-Type", this.content_type);

            PrintWriter out = res.getWriter();
            out.print(content);
            return false;
        }
    }

    public static class FolderHandler implements ExpressHandler {
        String path;
        public FolderHandler(String path) {
            this.path = path;
        }

        public boolean handle(HttpServletRequest req, HttpServletResponse res) throws ServletException, java.io.IOException {
            String content;
            try{
                String path = req.getServletPath();
                content = new String(Files.readAllBytes(Paths.get(this.path, path)), StandardCharsets.UTF_8);
            } catch(java.io.IOException error) {
                content = "file not exist";
            }
            PrintWriter out = res.getWriter();
            out.print(content);
            return false;
        }
    }
}
