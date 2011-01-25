package wandledi.java.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import wandledi.java.Interceptor;

/**Used to annotate actions to be intercepted by a given interceptor.
 * Example:
 * 
 * @InterceptWith(Login.class)
 * public void actionWhichRequiresLogin() {
 *      // ...
 * }
 *
 * @author Markus Kahl
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface InterceptWith {

    Class<? extends Interceptor> value();
    String[] except() default {};
}