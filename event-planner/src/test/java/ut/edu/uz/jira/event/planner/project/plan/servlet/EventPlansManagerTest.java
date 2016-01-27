package ut.edu.uz.jira.event.planner.project.plan.servlet;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.project.plan.webwork.EventPlansManager;
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

public class EventPlansManagerTest {
    private UserManager mockUserManager;
    private TemplateRenderer mockTemplateRenderer;
    private LoginUriProvider mockLoginUriProvider;
    private I18nResolver i18nResolver;
    private ActiveObjectsService service;

    @Before
    public void setUp() {
        mockUserManager = mock(UserManager.class);
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(mock(UserProfile.class));
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(true);

        i18nResolver = mock(I18nResolver.class);
        service = mock(ActiveObjectsService.class);

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
        TestingEventPlansManager fixture = new TestingEventPlansManager(mockTemplateRenderer, mockUserManager, mockLoginUriProvider, i18nResolver, service);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:2990/jira/plugins/servlet/test"));
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        fixture.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse, times(1)).sendRedirect(Mockito.anyString());
    }

    @Test
    public void should_redirect_to_login_page_when_User_Is_not_admin() throws IOException {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        TestingEventPlansManager fixture = new TestingEventPlansManager(mockTemplateRenderer, mockUserManager, mockLoginUriProvider, i18nResolver, service);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:2990/jira/plugins/servlet/test"));
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        fixture.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse, times(1)).sendRedirect(Mockito.anyString());
    }

    @Test
    public void if_user_not_admin_should_redirect_to_login_page_with_copying_source_address_with_query() throws IOException {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        TestingEventPlansManager fixture = new TestingEventPlansManager(mockTemplateRenderer, mockUserManager, mockLoginUriProvider, i18nResolver, service);
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
    public void if_admin_user_then_should_render_page() throws IOException {
        TestingEventPlansManager fixture = new TestingEventPlansManager(mockTemplateRenderer, mockUserManager, mockLoginUriProvider, i18nResolver, service);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:2990/jira/plugins/servlet/test"));
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        fixture.doGet(mockRequest, mockResponse);

        Mockito.verify(mockTemplateRenderer, times(1)).render(Mockito.anyString(), Mockito.any(Writer.class));
    }

    private class TestingEventPlansManager extends EventPlansManager {
        public TestingEventPlansManager(@Nonnull TemplateRenderer templateRenderer, @Nonnull UserManager userManager, @Nonnull LoginUriProvider loginUriProvider, I18nResolver resolver, ActiveObjectsService service) {
            super(templateRenderer, userManager, loginUriProvider, resolver, service);
        }

        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            super.doGet(request, response);
        }
    }
}
