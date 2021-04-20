package ru.job4j.todo.controller.filter;

import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IndexFilterTest {
    @Test
    public void whenNoRegisteredThenForwardToLogin() throws IOException, ServletException {
        String fakeContextPath = "myservice";
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(req.getRequestURI()).thenReturn(fakeContextPath + "/");
        Mockito.when(req.getContextPath()).thenReturn(fakeContextPath);
        new IndexFilter().doFilter(req, resp, filterChain);
        Mockito.verify(resp).sendRedirect(req.getContextPath() + "/index.do");
    }
}