/**
 * Copyright (C) 2015 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.web.designer.config;

import java.nio.file.Path;
import javax.inject.Named;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Named
@ConfigurationProperties(prefix = "designer.workspace")
public class WorkspaceProperties {

    private Path path;
    private String apiUrl;

    private Pages pages = new Pages();
    private Fragments fragments = new Fragments();
    private Widgets widgets= new Widgets();
    private WidgetsWc widgetsWc = new WidgetsWc();

    @Data
    public static class Pages {
        private Path dir;
    }

    @Data
    public static class Fragments {
        private Path dir;
    }

    @Data
    public static class Widgets {

        public static final String WIDGET_SUFFIX_WC = "Wc";

        private Path dir;
    }

    @Data
    public static class WidgetsWc {
        private Path dir;
    }

}