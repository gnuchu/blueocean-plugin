package io.jenkins.blueocean.rest.impl.pipeline.credential;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsStore;
import hudson.Extension;
import hudson.model.User;
import io.jenkins.blueocean.rest.OrganizationRoute;
import io.jenkins.blueocean.rest.factory.OrganizationResolver;
import io.jenkins.blueocean.rest.hal.Link;
import io.jenkins.blueocean.rest.model.BlueOrganization;
import io.jenkins.blueocean.rest.model.Container;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.export.ExportedBean;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Credential API container
 *
 * TODO: can very well be moved in it's own plugin along with {@link CredentialApi}
 *
 * @author Vivek Pandey
 */
@Extension
@ExportedBean
public class CredentialContainer extends Container<CredentialApi> implements OrganizationRoute {
    private final Link self;

    public CredentialContainer() {
        BlueOrganization organization= OrganizationResolver.getInstance().getContainingOrg(Jenkins.getInstance());
        this.self = (organization != null) ? organization.getLink().rel("credentials")
                : null;
    }

    public CredentialContainer(@Nonnull Link parent) {
        this.self = parent.rel("credentials");
    }

    @Override
    public String getUrlName() {
        return "credentials";
    }


    @Override
    public Link getLink() {
        return self;
    }

    @Override
    public CredentialApi get(String name) {
        for(CredentialApi api: this){
            if(api.getStore().equals(name)){
                return api;
            }
        }
        return null;
    }

    @Override
    public Iterator<CredentialApi> iterator() {
        List<CredentialApi> apis = new ArrayList<>();
        User user = User.current();
        if(user != null){
            for(CredentialsStore store: CredentialsProvider.lookupStores(user)){
                if(store.getStoreAction() != null) {
                    apis.add(new CredentialApi(store.getStoreAction(), this));
                }
            }
        }else{
            for(CredentialsStore store: CredentialsProvider.lookupStores(Jenkins.getInstance())){
                if(store.getStoreAction() != null) {
                    apis.add(new CredentialApi(store.getStoreAction(), this));
                }
            }
        }
        return apis.iterator();
    }

}