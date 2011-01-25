package wandledi.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;

/**Controller for OpenID authentication.
 *
 * Simply add it to the Switchboard via #addController in your Bootstrap.
 * The important actions are #authenticate and #verify.
 *
 * #authenticate redirects the user to the given OpenID service (parameter 'service'),
 * which in return redirects back to #verify.
 * #verify displays the user's identifier as well as his email adress in case
 * the service was kind enough to provide it.
 *
 * @author markus
 */
public class OpenIDController extends Controller {

    private static ConsumerManager manager;

    static {
        try {
            manager = new ConsumerManager();
        } catch (ConsumerException ex) {
            Logger.getLogger(OpenIDController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void index() {

        PrintWriter out = getWriter();
        out.println("<form action='authenticate' method='post'>");
        out.println("  OpenID Service: ");
        out.println("  <input type='text' name='service'/>");
        out.println("  <input type='submit' value='Anmelden'/>");
        out.println("</form>");
    }

    public void status() {

        getWriter().println("Status: " + (manager != null ? "OK" : "ERROR"));
    }

    /**Takes two HTTP parameters: 'service' and 'returnUri'.
     *
     * service - holds the URL to the open id service
     * returnUri - the URI to return to after authentication
     *
     * The URI will be called with potentially two parameters: 'identifier' and 'email'
     *
     * identifier - the successfully authanticated user's OpenID identifier
     * email - the user's email address, if provided
     *
     * If 'identifier' is null, the authentication failed.
     *
     * @throws IOException
     * @throws ServletException
     */
    public void authenticate() {

        String service = param("service");
        session.put("returnUri", param("returnUri"));
        if (!doAuthenticate(service)) {
            getWriter().println("Authentication failed: " + model.get("error"));
        }
    }

    public void verify() {

        PrintWriter out = getWriter();
        Identifier id = doVerify();
        String returnUri = session.get(String.class, "returnUri");
        String email = model.get(String.class, "email");
        if (id != null) {
            if (returnUri != null) {
                List<Parameter> params = new LinkedList<Parameter>();
                params.add(new Parameter("identifier", id.getIdentifier()));
                if (email != null) {
                    params.add(new Parameter("email", email));
                }
                session.put("identifier", id.getIdentifier());
                session.put("email", email);
                redirectToUri(returnUri/*, params.toArray(new Parameter[params.size()])*/);
            } else {
                out.println("Identifier: " + id.getIdentifier());
                out.println("<br/>E-Mail: " + email);
            }
        } else {
            if (returnUri != null) {
                redirectToUri(returnUri, new Parameter("error", (String)model.get("error")));
            } else {
                out.println("Verification failed: " + model.get("error"));
            }
        }
    }

    private String getRoot() {

        return (String) request.getAttribute("root");
    }

    private String getReturnURL() {

        return getRoot() + DefaultRoute.getURI("openID", "verify").substring(1);
    }

    /**Performs the actual authentication.
     *
     * @param openIdService URL to the OpenID service provider.
     * @return
     * @throws IOException
     * @throws ServletException
     */
    private boolean doAuthenticate(String openIdService) {

        try {
            List discoveries = manager.discover(openIdService);
            DiscoveryInformation discovered = manager.associate(discoveries);
            session.put("openid-disc", discovered);
            AuthRequest authReq = manager.authenticate(discovered, getReturnURL());
            FetchRequest fetch = FetchRequest.createFetchRequest();
            fetch.addAttribute(
                    "email", // attribute alias
                    "http://schema.openid.net/contact/email", // type URI
                    true); // required
            authReq.addExtension(fetch);
            if (!discovered.isVersion2()) {
                redirectToUrl(authReq.getDestinationUrl(true));
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("<form id='openid' action='" + authReq.getDestinationUrl(false) + "'>");
                Map params = authReq.getParameterMap();
                for (Object key: params.keySet()) {
                    sb.append("  <input type='hidden' name='" + key.toString() + "' value='" +
                            params.get(key).toString() + "'/>");
                }
                sb.append("  <input type='submit' value='Anmelden'/>");
                sb.append("</form>");
                if (formJsp == null) {
                    getWriter().println(sb.toString());
                } else {
                    model.put("form", sb.toString());
                    renderFile(formJsp);
                }
            }
            return true;
        } catch (OpenIDException e) {
            model.put("error", e.getMessage());
        }
        return false;
    }


    /**Sets the file to be rendered for the OpenID v2 login.
     * Into this page the required plain form has to be embedded.
     * It is accessible within the model under the name 'form'.
     *
     * @param file The jsp to be rendered. The path is relative to the default view directory.
     */
    public static void setFormJsp(String file) {
        formJsp = file;
    }
    private static String formJsp = null;

    private Identifier doVerify() {

        try {
            ParameterList response = new ParameterList(request.getParameterMap());
            StringBuffer receivingURL = request.getRequestURL();
            String queryString = request.getQueryString();
            if (queryString != null && queryString.length() > 0) {
                receivingURL.append("?").append(request.getQueryString());
            }
            VerificationResult verification = manager.verify(
                    receivingURL.toString(),
                    response, session.get(DiscoveryInformation.class, "openid-disc"));
            Identifier verified = verification.getVerifiedId();
            if (verified != null) {
                AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();
                if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
                    FetchResponse fetchResp =
                            (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);

                    List emails = fetchResp.getAttributeValues("email");
                    model.put("email", emails.get(0));
                }
                return verified;
            } else {
                model.put("error", "Verification failed.");
            }
        } catch (OpenIDException e) {
            model.put("error", e.getMessage());
        }
        return null;
    }
}
