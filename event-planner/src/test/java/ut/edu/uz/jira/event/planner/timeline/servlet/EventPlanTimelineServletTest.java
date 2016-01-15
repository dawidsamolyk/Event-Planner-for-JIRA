package ut.edu.uz.jira.event.planner.timeline.servlet;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import edu.uz.jira.event.planner.timeline.servlet.EventPlanTimelineServlet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class EventPlanTimelineServletTest {
    private UserManager mockUserManager;
    private TemplateRenderer mockTemplateRenderer;
    private LoginUriProvider mockLoginUriProvider;

    @Before
    public void setUp() {
        mockUserManager = mock(UserManager.class);
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(mock(UserProfile.class));
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(true);

        mockTemplateRenderer = mock(TemplateRenderer.class);
        mockLoginUriProvider = mock(LoginUriProvider.class);
        Mockito.when(mockLoginUriProvider.getLoginUri(Mockito.any(URI.class))).thenAnswer(new Answer<URI>() {
            @Override
            public URI answer(InvocationOnMock invocation) throws Throwable {
                return (URI) invocation.getArguments()[0];
            }
        });
    }

    @Test
    public void should_redirect_to_login_page_when_User_Is_Null() throws IOException {
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(null);
        TestingEventPlanTimelineServlet fixture = new TestingEventPlanTimelineServlet(mockTemplateRenderer, mockUserManager, mockLoginUriProvider);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:2990/jira/plugins/servlet/test"));
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        fixture.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse, times(1)).sendRedirect(Mockito.anyString());
    }

    @Test
    public void if_user_not_admin_should_redirect_to_login_page_with_copying_source_address_with_query() throws IOException {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        TestingEventPlanTimelineServlet fixture = new TestingEventPlanTimelineServlet(mockTemplateRenderer, mockUserManager, mockLoginUriProvider);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:2990/jira/plugins/servlet/test"));
        Mockito.when(mockRequest.getQueryString()).thenReturn("a=1&b=2");
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String redirectLocation = (String) invocation.getArguments()[0];
                assertTrue(redirectLocation.contains("a=1&b=2"));
                return null;
            }
        }).when(mockResponse).sendRedirect(Mockito.anyString());

        fixture.doGet(mockRequest, mockResponse);
    }

    @Test
    public void if_jira_user_then_should_render_page() throws IOException {
        TestingEventPlanTimelineServlet fixture = new TestingEventPlanTimelineServlet(mockTemplateRenderer, mockUserManager, mockLoginUriProvider);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:2990/jira/plugins/servlet/test"));
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        fixture.doGet(mockRequest, mockResponse);

        Mockito.verify(mockTemplateRenderer, times(1)).render(Mockito.anyString(), Mockito.any(Writer.class));
    }

    private class TestingEventPlanTimelineServlet extends EventPlanTimelineServlet {
        public TestingEventPlanTimelineServlet(@Nonnull TemplateRenderer templateRenderer, @Nonnull UserManager userManager, @Nonnull LoginUriProvider loginUriProvider) {
            super(templateRenderer, userManager, loginUriProvider);
        }

        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            super.doGet(request, response);
        }
    }
}
