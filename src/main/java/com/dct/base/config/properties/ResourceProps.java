package com.dct.base.config.properties;

import com.dct.base.constants.BasePropertiesConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * When the application starts, Spring will automatically create an instance of this class
 * and load the values from configuration files like application.properties or application.yml <p>
 *
 * {@link ConfigurationProperties} helps Spring map config properties to fields,
 * instead of using @{@link Value} for each property individually <p>
 *
 * {@link BasePropertiesConstants#RESOURCE_CONFIG} decides the prefix for the configurations that will be mapped <p>
 *
 * See <a href="">application-dev.yml</a> for detail
 *
 * @author thoaidc
 */
@ConfigurationProperties(prefix = BasePropertiesConstants.RESOURCE_CONFIG)
public class ResourceProps {

    private StaticResource staticResource;
    private UploadResource uploadResource;

    public StaticResource getStaticResource() {
        return staticResource;
    }

    public void setStaticResource(StaticResource staticResource) {
        this.staticResource = staticResource;
    }

    public UploadResource getUploadResource() {
        return uploadResource;
    }

    public void setUploadResource(UploadResource uploadResource) {
        this.uploadResource = uploadResource;
    }

    public static class StaticResource {

        private List<String> patterns;
        private List<String> locations;

        public List<String> getPatterns() {
            return patterns;
        }

        public void setPatterns(List<String> patterns) {
            this.patterns = patterns;
        }

        public List<String> getLocations() {
            return locations;
        }

        public void setLocations(List<String> locations) {
            this.locations = locations;
        }
    }

    public static class UploadResource {
        private List<String> acceptFormats;
        private List<String> patterns;
        private List<String> locations;

        public List<String> getAcceptFormats() {
            return acceptFormats;
        }

        public void setAcceptFormats(List<String> acceptFormats) {
            this.acceptFormats = acceptFormats;
        }

        public List<String> getPatterns() {
            return patterns;
        }

        public void setPatterns(List<String> patterns) {
            this.patterns = patterns;
        }

        public List<String> getLocations() {
            return locations;
        }

        public void setLocations(List<String> locations) {
            this.locations = locations;
        }
    }
}
