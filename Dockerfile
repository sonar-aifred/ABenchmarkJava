# Runtime image for the OWASP Benchmark WAR (built by the CI workflow into target/benchmark.war).
FROM tomcat:9.0-jdk17-temurin

# Benchmark is served over HTTPS on 8443, matching the cargo configuration in pom.xml.
COPY .keystore /usr/local/tomcat/conf/.keystore
RUN sed -i 's#</Service>#    <Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol" SSLEnabled="true">\n        <SSLHostConfig>\n            <Certificate certificateKeystoreFile="conf/.keystore" certificateKeystorePassword="changeit"/>\n        </SSLHostConfig>\n    </Connector>\n  </Service>#' /usr/local/tomcat/conf/server.xml \
    && rm -rf /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/docs /usr/local/tomcat/webapps/examples

COPY target/benchmark.war /usr/local/tomcat/webapps/benchmark.war

EXPOSE 8443
# App is available at https://<host>:8443/benchmark/
