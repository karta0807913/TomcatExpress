package util.middleware;

import javax.servlet.http.*;
import javax.servlet.*;
import java.lang.*;
import java.util.*;
import java.io.*;

import util.Express.ExpressHandler;

public class ParamChecker implements ExpressHandler {
    protected ArrayList<CheckFunction> funcs;
    protected String name;

    public ParamChecker(String name, CheckFunction... funcs) {
        this.funcs = new ArrayList<CheckFunction>();
        this.name = name;
        for(CheckFunction func : funcs) {
            this.funcs.add(func);
        }
    }

    protected boolean check_all(Object obj) throws ServletException, java.io.IOException {
        for(CheckFunction func : this.funcs) {
            if(!func.check(obj)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean handle(HttpServletRequest req, HttpServletResponse res) throws ServletException, java.io.IOException {
        Object obj = req.getParameter(this.name);
        if(!this.check_all(obj)) {
            PrintWriter out = res.getWriter();
            out.print("{\"status\": -1, \"msg\": \"parameter " + name + " error \"}");
            out.flush();
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    public static interface CheckFunction {
        public boolean check(Object param);
    }

    public static class IntegerChecker implements CheckFunction {
        @Override
        public boolean check(Object param) {
            try {
                Integer.parseInt(param.toString());
                return true;
            } catch(NumberFormatException e) {
            }
            return false;
        }
    }

    public static class ExistChecker implements CheckFunction {
        @Override
        public boolean check(Object param) {
            return param != null;
        }
    }
}
