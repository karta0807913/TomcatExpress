package util;

import java.nio.file.Paths;
import java.nio.file.Path;

import javax.servlet.http.*;

public class ExpressResponseWrapper extends HttpServletResponseWrapper {
    HttpServletRequest request;
    ExpressResponseWrapper(HttpServletRequest request, HttpServletResponse response) {
        super(response);
        this.request = request;
    }

    @Override
    public void sendRedirect(String location)
    throws java.io.IOException, java.lang.IllegalStateException{
        Path path = Paths.get(this.request.getRequestURI());
        path = Paths.get(path.getParent().toString(), location);
        super.sendRedirect(path.toString());
    }
}
