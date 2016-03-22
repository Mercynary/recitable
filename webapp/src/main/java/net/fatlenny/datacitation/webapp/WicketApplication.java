package net.fatlenny.datacitation.webapp;

import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

import net.fatlenny.datacitation.webapp.guice.BindingModule;
import net.fatlenny.datacitation.webapp.pages.DatasetCreationPage;
import net.fatlenny.datacitation.webapp.pages.HomePage;
import net.fatlenny.datacitation.webapp.pages.QueryPage;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start
 * class.
 * 
 * @see net.fatlenny.datacitation.webapp.Start#main(String[])
 */
public class WicketApplication extends WebApplication {
    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();

        getComponentInstantiationListeners().add(new GuiceComponentInjector(this, new BindingModule()));

        mountPage("/home", HomePage.class);
        mountPage("/query", QueryPage.class);
        mountPage("/datasetcreation", DatasetCreationPage.class);
    }
}
