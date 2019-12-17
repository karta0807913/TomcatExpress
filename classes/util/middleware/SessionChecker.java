package util.middleware;

import java.io.*;
import java.lang.*;
import javax.servlet.*;
import javax.servlet.http.*;

import util.Express.ExpressHandler;

public class SessionChecker extends ParamChecker {
    public SessionChecker(String name, CheckFunction... funcs) {
        super(name, funcs);
    }
    @Override
    public boolean handle(HttpServletRequest req, HttpServletResponse res) throws ServletException, java.io.IOException {
        Object obj = req.getSession().getAttribute(this.name);
        if(!this.check_all(obj)) {
            PrintWriter out = res.getWriter();
            out.print("{\"status\": -1, \"msg\": \"session error\"}");
            out.flush();
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }
}
