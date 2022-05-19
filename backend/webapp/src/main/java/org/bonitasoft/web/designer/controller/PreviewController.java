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
package org.bonitasoft.web.designer.controller;

import org.bonitasoft.web.designer.controller.preview.Previewer;
import org.bonitasoft.web.designer.controller.utils.HttpFile;
import org.bonitasoft.web.designer.repository.FragmentRepository;
import org.bonitasoft.web.designer.repository.PageRepository;
import org.bonitasoft.web.designer.workspace.WorkspacePathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Controller
public class PreviewController {

    protected static final Logger logger = LoggerFactory.getLogger(PreviewController.class);
    public static final String JS_FOLDER = "js";

    private PageRepository pageRepository;
    private Previewer previewer;
    private Path widgetRepositoryPath;
    private Path fragmentRepositoryPath;
    private Path pageRepositoryPath;
    private WorkspacePathResolver pathResolver;
    private FragmentRepository fragmentRepository;

    @Inject
    public PreviewController(PageRepository pageRepository,
                             FragmentRepository fragmentRepository,
                             Previewer previewer,
                             @Named("widgetPath") Path widgetRepositoryPath,
                             @Named("fragmentsPath") Path fragmentRepositoryPath,
                             @Named("pagesPath") Path pageRepositoryPath,
                             WorkspacePathResolver pathResolver) {
        this.pageRepository = pageRepository;
        this.fragmentRepository = fragmentRepository;
        this.previewer = previewer;
        this.widgetRepositoryPath = widgetRepositoryPath;
        this.fragmentRepositoryPath = fragmentRepositoryPath;
        this.pageRepositoryPath = pageRepositoryPath;
        this.pathResolver = pathResolver;
    }

    /**
     * Send redirect to the Rest API
     */
    @RequestMapping({"/preview/{previewableType}/{appName}/API/**","/preview/{previewableType}/API/**"})
    public void proxyAPICall(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        try {
            String queryString = isEmpty(request.getQueryString()) ? "" : "?" + request.getQueryString();
            response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
            response.addHeader("Location", request.getContextPath() + "/API/" + RequestMappingUtils.extractPathWithinPattern(request) + queryString);

            response.flushBuffer();
        } catch (IOException e) {
            String message = "Error while redirecting API call";
            logger.error(message, e);
            throw new ServletException(message, e);
        }
    }



    @RequestMapping(value = {"/preview/page/{appName}/{id}", "/preview/page/{id}"}, produces = "text/html; charset=UTF-8")
    public ResponseEntity<String> previewPage(@PathVariable(value = "id") String id, HttpServletRequest httpServletRequest) {
        return previewer.render(id, pageRepository, httpServletRequest);
    }

    @RequestMapping({"/preview/page/{appName}/{id}/assets/**", "/preview/page/{id}/assets/**"})
    public void servePageAsset(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String pageId) throws IOException {
        String matchingPath = RequestMappingUtils.extractPathWithinPattern(request);
        Path filePath = pageRepositoryPath.resolve(pageId).resolve("assets").resolve(matchingPath);
        HttpFile.writeFileInResponse(request, response, filePath);
    }

    @RequestMapping({"/preview/{previewableType}/{appName}/{id}/widgets/**","/preview/{previewableType}/{id}/widgets/**"})
    public void serveWidgetFiles(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String matchingPath = RequestMappingUtils.extractPathWithinPattern(request);
        HttpFile.writeFileInResponse(request, response, widgetRepositoryPath.resolve(matchingPath));
    }

    @RequestMapping({"/preview/page/{appName}/{id}/js/**","/preview/page/{id}/js/**"})
    public void servePageJs(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String pageId) throws IOException {
        String matchingPath = RequestMappingUtils.extractPathWithinPattern(request);
        Path filePath = pathResolver.getTmpPagesRepositoryPath().resolve(pageId).resolve(JS_FOLDER).resolve(matchingPath);
        HttpFile.writeFileInResponse(request, response, filePath);
    }

    @RequestMapping(value = {"/preview/fragment/{appName}/{id}","/preview/fragment/{id}"}, produces = "text/html; charset=UTF-8")
    public ResponseEntity<String> previewFragment(@PathVariable(value = "id") String id, HttpServletRequest
            httpServletRequest) {
        return previewer.render(id, fragmentRepository, httpServletRequest);
    }

    @RequestMapping({"/preview/fragment/{appName}/{id}/widgets*","/preview/fragment/{id}/widgets*"})
    public void serveFragmentWidgets(HttpServletRequest request, HttpServletResponse response, @PathVariable("id")
            String id) throws IOException {
        String matchingPath = RequestMappingUtils.extractPathWithinPattern(request);
        Path filePath = pathResolver.getTmpFragmentsRepositoryPath().resolve(id).resolve(matchingPath);
        HttpFile.writeFileInResponse(request, response, filePath);
    }

    @RequestMapping({"/preview/page/{appName}/{id}/fragments/**","/preview/page/{id}/fragments/**"})
    public void serveFragmentFiles(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String matchingPath = RequestMappingUtils.extractPathWithinPattern(request);
        HttpFile.writeFileInResponse(request, response, fragmentRepositoryPath.resolve(matchingPath));
    }

    /**
     * Send redirect to the Application theme resources if an application is selected
     * Else returns a default theme.css files.
     */
    @RequestMapping({"/preview/{previewableType}/theme/**", "/preview/{previewableType}/{appName}/theme/**"})
    public void serveThemeResources(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String matchingPath = RequestMappingUtils.extractPathWithinPattern(request);
        response.setHeader("Content-Type", "text/css");
        response.setHeader("Content-Disposition", "inline; filename=\"theme.css\"");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setStatus(HttpStatus.OK.value());
        try (PrintWriter writer = response.getWriter()) {
            writer.println("/**");
            writer.println("* Living application theme");
            writer.print("*/");
        } catch (IOException e) {
            // fail silently
        }
    }

}
