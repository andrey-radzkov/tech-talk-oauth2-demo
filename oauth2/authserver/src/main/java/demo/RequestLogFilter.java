package demo;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;


class RequestLogFilter extends GenericFilterBean {
    private static Logger LOG = Logger.getLogger(RequestLogFilter.class);



    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) req;
        String queryString = !StringUtils.isEmpty(servletRequest.getQueryString()) ? "?" + servletRequest.getQueryString() : "";
        String postString = "";
        if ("POST".equalsIgnoreCase(servletRequest.getMethod())) {
            postString = servletRequest.getParameterMap().entrySet().stream().map(entry -> entry.getKey() + ": "
                    + String.join(",", entry.getValue())).collect(Collectors.joining(", "));
        }
        String headerAuth = Optional.ofNullable(servletRequest.getHeader("Authorization")).orElse("");
        if (headerAuth.contains("Basic")) {
            headerAuth = headerAuth.replace("Basic", "");
            headerAuth = org.apache.commons.codec.binary.StringUtils.newStringUtf8(Base64.decodeBase64(headerAuth));
        }
        headerAuth = !"".equals(headerAuth) ? " " + headerAuth : "";
        LOG.info(servletRequest.getMethod() + " " + servletRequest.getRequestURI() + queryString + headerAuth + " " + postString);

        chain.doFilter(req, res); // always continue
    }
}
