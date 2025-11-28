package ar.edu.itba.paw.webapp.auth.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SpaForwardFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        // ruta sin el context path (/webapp_war_exploded)
        String path = req.getRequestURI().substring(req.getContextPath().length());

        boolean isApi = path.startsWith("/api");
        boolean isStatic = path.contains(".") || path.startsWith("/_app");

        if (!isApi && !isStatic) {
            request.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
