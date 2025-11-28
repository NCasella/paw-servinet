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

        // No tocar recursos estáticos: tienen punto o son de _app
        boolean isStatic =
                path.startsWith("/app/_app")
                        || path.contains(".");   // .js, .css, .png, etc.

        if (!isStatic) {
            // forward a index.html de la SPA
            request.getRequestDispatcher("/app/index.html").forward(request, response);
            return;
        }

        // para todo lo demás, seguir normalmente
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
