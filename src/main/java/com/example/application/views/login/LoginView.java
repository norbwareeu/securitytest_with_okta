package com.example.application.views.login;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @see "https://vaadin.com/docs/latest/security/enabling-security"
 * @see "https://github.com/okta/samples-java-spring/tree/master/custom-login"
 * @see "https://github.com/okta/okta-signin-widget/releases/"
 */
@Route(value = "custom-login")
@AnonymousAllowed
@PageTitle("Login")
@JavaScript("https://global.oktacdn.com/okta-signin-widget/5.16.1/js/okta-sign-in.min.js")
@StyleSheet("https://global.oktacdn.com/okta-signin-widget/5.16.1/css/okta-sign-in.min.css")
public class LoginView extends VerticalLayout implements AfterNavigationObserver, BeforeEnterObserver {

    private static final Logger log = LoggerFactory.getLogger(LoginView.class);
    @Value("${okta.oauth2.issuer}")
    private String oktaOauth2Issuer;
    @Value("${okta.oauth2.redirect-uri}")
    private String oktaOauth2RedirectUri;
    private Div signinWidget;

    public LoginView() {

        addClassName("login");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        signinWidget = new Div();
        signinWidget.setId("sign-in-widget");
        add(signinWidget);
    }

    public void afterNavigation(AfterNavigationEvent event) {

        //VaadinRequest request = VaadinRequest.getCurrent();
        /*
        example:
        http://localhost:8081/custom-login?
        response_type=code&
        client_id=0oa4drr..........5d7&
        scope=openid%20email%20profile&
        state=YAjMS__hFTA2u957XGpcK--..........O_dX-PTalI%3D&
        redirect_uri=http://localhost:8081/authorization-code/callback&
        nonce=C1ns3RCLaZ5mezfM8.........._J8nbEYmoDJt7xcc
         */
/*
        String reqParamResponseType = request.getParameter("response_type");
        String reqParamClientId = request.getParameter("client_id");
        String reqParamScope = request.getParameter("scope");
        String reqParamState = request.getParameter("state");
        String reqParamRedirectUri = request.getParameter("redirect_uri");
        String reqParamNonce = request.getParameter("nonce");
*/
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        log.debug("queryParameters=" + queryParameters.toString());
        String reqParamResponseType = queryParameters.getSingleParameter("response_type").orElse("");
        String reqParamClientId = queryParameters.getSingleParameter("client_id").orElse("");
        String reqParamScope = queryParameters.getSingleParameter("scope").orElse("");
        String reqParamState = queryParameters.getSingleParameter("state").orElse("");
        String reqParamRedirectUri = queryParameters.getSingleParameter("redirect_uri").orElse("");
        String reqParamNonce = queryParameters.getSingleParameter("nonce").orElse("");

        log.debug("reqParamResponseType=" + reqParamResponseType);
        log.debug("reqParamClientId=" + reqParamClientId);
        log.debug("reqParamScope=" + reqParamScope);
        log.debug("reqParamState=" + reqParamState);
        log.debug("reqParamRedirectUri=" + reqParamRedirectUri);
        log.debug("reqParamNonce=" + reqParamNonce);
        log.debug("event.getLocation().getPath()=" + event.getLocation().getPath());
        //log.debug(UI.getCurrent().getPage().getHistory());
        log.debug("event.getRouteParameters()=" + event.getRouteParameters().toString());
        log.debug("event.getLocation().getQueryParameters()=" + event.getLocation().getQueryParameters());

        // if we don't have the state parameter redirect
        if (reqParamState == null || reqParamState.isEmpty()) {
            // handel this in beforeEnter()
            log.error("afterNavigation: no reqParamState");
            /*
            //return new ModelAndView("redirect:" + oktaOAuth2Properties.getRedirectUri());
            //UI.getCurrent().navigate("http://localhost:8081/authorization-code/callback");
            log.debug("LoginView redirect to "+oktaOauth2RedirectUri);
            //UI.getCurrent().navigate(oktaOauth2RedirectUri);
            UI.getCurrent().navigateToClient(oktaOauth2RedirectUri);
             */
/*
            Anchor redirectLink = new Anchor(oktaOauth2RedirectUri, "(redirect)");
            //redirectLink.getElement().setAttribute("router-ignore", true);
            redirectLink.setRouterIgnore(true);
            //UI.getCurrent().navigate(oktaOauth2RedirectUri);
            //UI.getCurrent().getPage().setLocation(oktaOauth2RedirectUri);
            Button redirButton = new Button("redir");
            redirectLink.add(redirButton);
            add(redirectLink);
            redirButton.click();
            log.debug("redirButton click() called.");
 */
            log.debug("LoginView redirect to " + oktaOauth2RedirectUri);
            UI.getCurrent().navigate(oktaOauth2RedirectUri);
            //UI.getCurrent().getPage().setLocation(oktaOauth2RedirectUri);
            return;
        }

        String issuer = oktaOauth2Issuer; //oktaOAuth2Properties.getIssuer();
        // the widget needs the base url, just grab the root of the issuer
        String orgUrl = "";
        try {
            orgUrl = new URL(new URL(issuer), "/").toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        Page page = UI.getCurrent().getPage();

        // custom style for the app:
        String customCss =
                " body {" +
                        "    overflow: auto;" +
                        "} " +
                        "#okta-sign-in {" +
                        "    border-width: 0px;" +
                        "} " +
                        "#okta-sign-in .auth-header { " +
                        "    padding: 0px; " +
                        "    border-bottom: 0px; " +
                        "} " +
                        "#okta-sign-in.auth-container {" +
                        "    background-color: transparent;" +
                        "} " +
                        "#okta-sign-in .auth-org-logo {" +
                        "    max-width: 100%; " +
                        "    max-height: 100%; " +
                        "} " +
                        "#okta-sign-in.auth-container.main-container { " +
                        "    background-color: transparent; " +
                        "} ";

        String customCss2 =
                "#okta-sign-in .auth-content {" +
                        "    background-color: rgb(255, 255, 255, 0.8);" +
                        "    border: 2px;" +
                        "} ";

        log.debug("reqParamRedirectUri=" + reqParamRedirectUri);

        customCss += customCss2;

        String addCustomStyleJsScript = "document.head.appendChild(document.createElement('style')).innerHTML = '" + customCss + "';";
        page.executeJs(addCustomStyleJsScript);

        String jsScript =
                "var config = {}; " +

                        "config.baseUrl = '" + orgUrl + "'," +   // // /*[[${oktaBaseUrl}]]*/ 'https://{yourOktaDomain}';
                        "config.clientId = '" + reqParamClientId + "'," +  // /*[[${oktaClientId}]]*/ '{clientId}';
                        "config.redirectUri = '" + reqParamRedirectUri + "'," +   // /*[[${redirectUri}]]*/ '{redirectUri}';
                        "config.authParams = { " +
                        "        issuer: '" + issuer + "'," +    // /*[[${issuerUri}]]*/ '{issuerUri}',
                        "        responseType: 'code'," +  //'code',
                        "        pkce: false, " +
                        "        state: '" + reqParamState + "'," +     // /*[[${state}]]*/ '{state}' || false,
                        "        nonce: '" + reqParamNonce + "'," +     // /*[[${nonce}]]*/ '{nonce}',
                        "        scopes: [\"openid\",\"email\",\"profile\"]," +    // /*[[${scopes}]]*/ '[scopes]',
                        "        display: 'page' " +
                        "}" +

                        // @see https://developer.okta.com/docs/guides/custom-widget/main/#initial-sign-in-page
                        // @see https://github.com/okta/okta-signin-widget/blob/master/docs/classic.md#events
                        ",config.brandName = 'Mybrand'" +
                        ",config.logoText = 'Mybrand'" +

                        ";" +

                        " var signIn = new OktaSignIn(config);" +
                        " signIn.renderEl(" +
                        "    { el: '#sign-in-widget' }," +
                        "    function (res) { } " +
                        ");";

        page.executeJs(jsScript);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        log.debug("beforeEnter");

        log.debug("getLocation.getPath=" + beforeEnterEvent.getLocation().getPath());
        log.debug("getLocation.getQueryParameters=" + beforeEnterEvent.getLocation().getQueryParameters());

        // inform the user about an authentication error
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            //login.setError(true);
            return;
        }

        QueryParameters queryParameters = beforeEnterEvent.getLocation().getQueryParameters();
        log.debug("queryParameters=" + queryParameters.toString());
        String reqParamResponseType = queryParameters.getSingleParameter("response_type").orElse("");
        String reqParamClientId = queryParameters.getSingleParameter("client_id").orElse("");
        String reqParamScope = queryParameters.getSingleParameter("scope").orElse("");
        String reqParamState = queryParameters.getSingleParameter("state").orElse("");
        String reqParamRedirectUri = queryParameters.getSingleParameter("redirect_uri").orElse("");
        String reqParamNonce = queryParameters.getSingleParameter("nonce").orElse("");

        log.debug("reqParamResponseType=" + reqParamResponseType);
        log.debug("reqParamClientId=" + reqParamClientId);
        log.debug("reqParamScope=" + reqParamScope);
        log.debug("reqParamState=" + reqParamState);
        log.debug("reqParamRedirectUri=" + reqParamRedirectUri);
        log.debug("reqParamNonce=" + reqParamNonce);
        log.debug("event.getLocation().getPath()=" + beforeEnterEvent.getLocation().getPath());
        //log.debug(UI.getCurrent().getPage().getHistory());
        log.debug("event.getRouteParameters()=" + beforeEnterEvent.getRouteParameters().toString());
        log.debug("event.getLocation().getQueryParameters()=" + beforeEnterEvent.getLocation().getQueryParameters());

        // if we don't have the state parameter redirect
        if (reqParamState == null || reqParamState.isEmpty()) {
            //return new ModelAndView("redirect:" + oktaOAuth2Properties.getRedirectUri());
            //UI.getCurrent().navigate("http://localhost:8081/authorization-code/callback");
            //UI.getCurrent().navigate(oktaOauth2RedirectUri);
            //UI.getCurrent().navigateToClient(oktaOauth2RedirectUri);

            //log.debug("LoginView redirect to "+oktaOauth2RedirectUri);
            //beforeEnterEvent.forwardToUrl(oktaOauth2RedirectUri);

            //UI.getCurrent().getPage().setLocation(oktaOauth2RedirectUri);
        }

    }
}
