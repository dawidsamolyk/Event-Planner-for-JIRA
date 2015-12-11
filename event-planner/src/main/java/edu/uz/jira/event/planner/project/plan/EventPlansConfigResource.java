package edu.uz.jira.event.planner.project.plan;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Path("/")
public class EventPlansConfigResource {
    private final UserManager userManager;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final TransactionTemplate transactionTemplate;

    public EventPlansConfigResource(@Nonnull final UserManager userManager,
                                    @Nonnull final PluginSettingsFactory pluginSettings,
                                    @Nonnull final TransactionTemplate transactionTemplate) {
        this.userManager = userManager;
        this.pluginSettingsFactory = pluginSettings;
        this.transactionTemplate = transactionTemplate;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(final Configuration config, @Context HttpServletRequest request) {
        if (isAdminUser(userManager.getRemoteUser(request))) return Response.status(Status.UNAUTHORIZED).build();

        transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction() {
                PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
                pluginSettings.put(Configuration.class.getName() + ".name", config.getName());
                pluginSettings.put(Configuration.class.getName() + ".time", Integer.toString(config.getTime()));
                return null;
            }
        });
        return Response.noContent().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest request) {
        if (isAdminUser(userManager.getRemoteUser(request))) return Response.status(Status.UNAUTHORIZED).build();

        return Response.ok(transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction() {
                PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
                Configuration config = new Configuration();
                config.setName((String) settings.get(Configuration.class.getName() + ".name"));

                String time = (String) settings.get(Configuration.class.getName() + ".time");
                if (time != null) {
                    config.setTime(Integer.parseInt(time));
                }
                return config;
            }
        })).build();
    }

    private boolean isAdminUser(UserProfile user) {
        if (user == null || !userManager.isSystemAdmin(user.getUserKey())) {
            return true;
        }
        return false;
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Configuration {
        @XmlElement
        private String name;
        @XmlElement
        private int time;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }
    }
}
