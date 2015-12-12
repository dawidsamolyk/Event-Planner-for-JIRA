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
    public Response put(final Configuration config, @Context final HttpServletRequest request) {
        if (isAdminUser(userManager.getRemoteUser(request))) return Response.status(Status.UNAUTHORIZED).build();

        transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction() {
                PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
                pluginSettings.put(Configuration.class.getName() + ".name", config.getName());
                pluginSettings.put(Configuration.class.getName() + ".description", config.getDescription());
                pluginSettings.put(Configuration.class.getName() + ".time", config.getTime());
                pluginSettings.put(Configuration.class.getName() + ".domain", config.getDomain());
                return null;
            }
        });
        return Response.noContent().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context final HttpServletRequest request) {
        if (isAdminUser(userManager.getRemoteUser(request))) return Response.status(Status.UNAUTHORIZED).build();

        return Response.ok(transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction() {
                PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
                Configuration config = new Configuration();

                config.setName((String) settings.get(Configuration.class.getName() + ".name"));
                config.setDescription((String) settings.get(Configuration.class.getName() + ".description"));
                config.setTime((String) settings.get(Configuration.class.getName() + ".time"));
                config.setDomain((String) settings.get(Configuration.class.getName() + ".domain"));

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
        private String description;
        @XmlElement
        private String time;
        @XmlElement
        private String domain;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }
    }
}
