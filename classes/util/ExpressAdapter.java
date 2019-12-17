package util;
import javax.servlet.*;
import javax.servlet.http.*;

public class ExpressAdapter extends HttpServlet {
    protected Express express;

    public ExpressAdapter() {
        super();
        this.express = new Express();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, java.io.IOException {
        this.express.forward(req, res);
    }
}
