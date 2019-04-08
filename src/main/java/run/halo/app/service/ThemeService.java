package run.halo.app.service;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import run.halo.app.model.support.Theme;
import run.halo.app.model.support.ThemeFile;
import run.halo.app.model.support.ThemeProperties;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * @author : RYAN0UP
 * @date : 2019/3/26
 */
public interface ThemeService {

    /**
     * Gets all themes
     *
     * @return list of themes
     */
    List<Theme> getThemes();

    /**
     * Lists theme folder by absolute path.
     *
     * @param absolutePath absolutePath
     * @return List<ThemeFile>
     */
    @Deprecated
    List<ThemeFile> listThemeFolder(@NonNull String absolutePath);

    /**
     * Lists theme folder by theme name.
     *
     * @param theme theme
     * @return List<ThemeFile>
     */
    List<ThemeFile> listThemeFolderBy(@NonNull String theme);

    /**
     * Gets custom template, such as page_xxx.ftl, and xxx will be template name
     *
     * @param theme theme name
     * @return List
     */
    List<String> getCustomTpl(@NonNull String theme);

    /**
     * Judging whether template exists under the specified theme
     *
     * @param template template
     * @return boolean
     */
    boolean isTemplateExist(@NonNull String template);

    /**
     * Judging whether theme exists under template path
     *
     * @param theme theme name
     * @return boolean
     */
    boolean isThemeExist(@NonNull String theme);

    /**
     * Gets theme base path.
     *
     * @return File
     */
    File getThemeBasePath();

    /**
     * Gets theme base path.
     *
     * @return theme base path
     */
    Path getBasePath();

    /**
     * Get theme Properties.
     *
     * @param path path
     * @return ThemeProperties
     */
    ThemeProperties getProperties(@NonNull File path);

    /**
     * Get template content by template absolute path.
     *
     * @param absolutePath absolute path
     * @return template content
     */
    @Deprecated
    String getTemplateContent(@NonNull String absolutePath);

    /**
     * Save template content by template absolute path.
     *
     * @param absolutePath absolute path
     * @param content      new content
     */
    @Deprecated
    void saveTemplateContent(@NonNull String absolutePath, @NonNull String content);

    /**
     * Delete a theme by key.
     *
     * @param key theme key
     */
    void deleteTheme(@NonNull String key);

    /**
     * Fetchs theme configuration.
     *
     * @param themeName theme name must not be blank
     * @return theme configuration or null if not found
     */
    @Nullable
    Object fetchConfig(@NonNull String themeName);

    /**
     * Render a theme page.
     *
     * @param pageName must not be blank
     * @return full path of the theme page
     */
    @NonNull
    String render(@NonNull String pageName);

}
