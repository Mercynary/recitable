/**
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
