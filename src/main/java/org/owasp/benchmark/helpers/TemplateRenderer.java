package org.owasp.benchmark.helpers;

import org.apache.commons.text.StringSubstitutor;

/**
 * Renders user-supplied templates. Calls the vulnerable commons-text interpolator
 * (CVE-2022-42889) so the dependency risk is reachable from application code.
 */
public final class TemplateRenderer {

    private TemplateRenderer() {}

    public static String render(String template) {
        // The default interpolator enables script:, dns: and url: lookups in commons-text < 1.10.0
        return StringSubstitutor.createInterpolator().replace(template);
    }
}
