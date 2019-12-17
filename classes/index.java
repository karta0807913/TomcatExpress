import util.Express;
import util.ExpressAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.Cookie;

import route.*;
import util.middleware.ParamChecker;
import util.middleware.SessionChecker;
import java.io.*;

import javax.mail.MessagingException;

import util.Express.ExpressHandler;

public class index extends ExpressAdapter {
    public index() {
        super();
        Express express = this.express;
    }
}
