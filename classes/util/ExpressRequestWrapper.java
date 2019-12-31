package util;

import java.io.PrintWriter;

import javax.servlet.http.*;

public class ExpressRequestWrapper extends HttpServletRequestWrapper {
    String servletPath;
    String contextPath;
    ExpressRequestWrapper(HttpServletRequest request) {
        this(request, request.getServletPath(), request.getContextPath());
    }

    ExpressRequestWrapper(HttpServletRequest request, String servletPath) {
        this(request, servletPath, request.getContextPath());
    }

    ExpressRequestWrapper(HttpServletRequest request, String servletPath, String contextPath) {
        super(request);
        this.servletPath = servletPath;
        this.contextPath = contextPath;
    }

    public java.lang.String getServletPath() {
        return servletPath;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }
}
