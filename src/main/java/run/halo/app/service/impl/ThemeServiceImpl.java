package run.halo.app.service.impl;

import static run.halo.app.model.support.HaloConst.DEFAULT_ERROR_PATH;
import static run.halo.app.model.support.HaloConst.DEFAULT_THEME_ID;
import static run.halo.app.utils.FileUtils.copyFolder;
import static run.halo.app.utils.FileUtils.deleteFolderQuietly;
import static run.halo.app.utils.VersionUtil.compareVersion;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import run.halo.app.cache.AbstractStringCacheStore;
import run.halo.app.config.properties.HaloProperties;
import run.halo.app.event.theme.ThemeActivatedEvent;
import run.halo.app.event.theme.ThemeUpdatedEvent;
import run.halo.app.exception.AlreadyExistsException;
import run.halo.app.exception.BadRequestException;
import run.halo.app.exception.ForbiddenException;
import run.halo.app.exception.NotFoundException;
import run.halo.app.exception.ServiceException;
import run.halo.app.exception.ThemeNotSupportException;
import run.halo.app.exception.ThemePropertyMissingException;
import run.halo.app.exception.ThemeUpdateException;
import run.halo.app.exception.UnsupportedMediaTypeException;
import run.halo.app.handler.theme.config.ThemeConfigResolver;
import run.halo.app.handler.theme.config.support.Group;
import run.halo.app.handler.theme.config.support.ThemeProperty;
import run.halo.app.model.properties.PrimaryProperties;
import run.halo.app.model.support.HaloConst;
import run.halo.app.model.support.ThemeFile;
import run.halo.app.repository.ThemeSettingRepository;
import run.halo.app.service.OptionService;
import run.halo.app.service.ThemeService;
import run.halo.app.theme.GitThemeFetcher;
import run.halo.app.theme.MultipartZipFileThemeFetcher;
import run.halo.app.theme.ThemeFetcherComposite;
import run.halo.app.theme.ThemeFileScanner;
import run.halo.app.theme.ThemePropertyScanner;
import run.halo.app.theme.ZipThemeFetcher;
import run.halo.app.utils.FileUtils;
import run.halo.app.utils.FilenameUtils;
import run.halo.app.utils.GitUtils;
import run.halo.app.utils.GithubUtils;
import run.halo.app.utils.HaloUtils;

/**
 * Theme service implementation.
 *
 * @author ryanwang
 * @date 2019-03-26
 */
@Slf4j
@Service
public class ThemeServiceImpl implements ThemeService {

    /**
     * Theme work directory.
     */
    private final Path themeWorkDir;

    private final OptionService optionService;

    private final AbstractStringCacheStore cacheStore;

    private final ThemeConfigResolver themeConfigResolver;

    private final RestTemplate restTemplate;

    private final ApplicationEventPublisher eventPublisher;

    private final ThemeSettingRepository themeSettingRepository;

    private final ThemeFetcherComposite fetcherComposite;

    /**
     * Activated theme id.
     */
    @Nullable
    private volatile String activatedThemeId;

    /**
     * Activated theme property.
     */
    private volatile ThemeProperty activatedTheme;

    public ThemeServiceImpl(HaloProperties haloProperties,
            OptionService optionService,
            AbstractStringCacheStore cacheStore,
            ThemeConfigResolver themeConfigResolver,
            RestTemplate restTemplate,
            ApplicationEventPublisher eventPublisher,
            ThemeSettingRepository themeSettingRepository) {
        this.optionService = optionService;
        this.cacheStore = cacheStore;
        this.themeConfigResolver = themeConfigResolver;
        this.restTemplate = restTemplate;

        this.themeWorkDir = Paths.get(haloProperties.getWorkDir(), THEME_FOLDER);
        this.eventPublisher = eventPublisher;
        this.themeSettingRepository = themeSettingRepository;

        this.fetcherComposite = new ThemeFetcherComposite();
        this.fetcherComposite.addFetcher(new ZipThemeFetcher());
        this.fetcherComposite.addFetcher(new GitThemeFetcher());
        this.fetcherComposite.addFetcher(new MultipartZipFileThemeFetcher());
    }

    @Override
    @NonNull
    public ThemeProperty getThemeOfNonNullBy(@NonNull String themeId) {
        return fetchThemePropertyBy(themeId)
                .orElseThrow(
                        () -> new NotFoundException(themeId + " 主题不存在或已删除！").setErrorData(themeId));
    }

    @Override
    @NonNull
    public Optional<ThemeProperty> fetchThemePropertyBy(String themeId) {
        if (StringUtils.isBlank(themeId)) {
            return Optional.empty();
        }

        // Get all themes
        List<ThemeProperty> themes = getThemes();

        // filter and find first
        return themes.stream()
                .filter(themeProperty -> StringUtils.equals(themeProperty.getId(), themeId))
                .findFirst();
    }

    @Override
    @NonNull
    public List<ThemeProperty> getThemes() {
        ThemeProperty[] themeProperties =
                cacheStore.getAny(THEMES_CACHE_KEY, ThemeProperty[].class).orElseGet(() -> {
                    List<ThemeProperty> properties =
                            ThemePropertyScanner.INSTANCE
                                    .scan(getBasePath(), getActivatedThemeId());
                    // Cache the themes
                    cacheStore.putAny(THEMES_CACHE_KEY, properties);
                    return properties.toArray(new ThemeProperty[0]);
                });
        return Arrays.asList(themeProperties);
    }

    @Override
    @NonNull
    public List<ThemeFile> listThemeFolderBy(@NonNull String themeId) {
        return fetchThemePropertyBy(themeId)
                .map(themeProperty -> ThemeFileScanner.INSTANCE.scan(themeProperty.getThemePath()))
                .orElse(Collections.emptyList());
    }

    @Override
    @NonNull
    public List<String> listCustomTemplates(@NonNull String themeId) {
        return listCustomTemplates(themeId, CUSTOM_SHEET_PREFIX);
    }

    @Override
    @NonNull
    public List<String> listCustomTemplates(@NonNull String themeId, @NonNull String prefix) {
        return fetchThemePropertyBy(themeId).map(themeProperty -> {
            // Get the theme path
            Path themePath = Paths.get(themeProperty.getThemePath());
            try (Stream<Path> pathStream = Files.list(themePath)) {
                return pathStream
                        .filter(path -> StringUtils
                                .startsWithIgnoreCase(path.getFileName().toString(), prefix))
                        .map(path -> {
                            // Remove prefix
                            String customTemplate =
                                    StringUtils.removeStartIgnoreCase(path.getFileName().toString(),
                                            prefix);
                            // Remove suffix
                            return StringUtils
                                    .removeEndIgnoreCase(customTemplate, HaloConst.SUFFIX_FTL);
                        })
                        .distinct()
                        .collect(Collectors.toList());
            } catch (Exception e) {
                throw new ServiceException("Failed to list files of path " + themePath, e);
            }
        }).orElse(Collections.emptyList());
    }

    @Override
    public boolean templateExists(String template) {
        if (StringUtils.isBlank(template)) {
            return false;
        }

        return fetchActivatedTheme().map(themeProperty -> {
            // Resolve template path
            Path templatePath = Paths.get(themeProperty.getThemePath(), template);
            // Check the directory
            checkDirectory(templatePath.toString());
            // Check existence
            return Files.exists(templatePath);
        }).orElse(false);
    }

    @Override
    public boolean themeExists(String themeId) {
        return fetchThemePropertyBy(themeId).isPresent();
    }

    @Override
    public Path getBasePath() {
        return themeWorkDir;
    }

    @Override
    public String getTemplateContent(@NonNull String absolutePath) {
        // Check the path
        checkDirectory(absolutePath);

        // Read file
        Path path = Paths.get(absolutePath);
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new ServiceException("读取模板内容失败 " + absolutePath, e);
        }
    }

    @Override
    @NonNull
    public String getTemplateContent(@NonNull String themeId, @NonNull String absolutePath) {
        checkDirectory(themeId, absolutePath);

        // Read file
        Path path = Paths.get(absolutePath);
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new ServiceException("读取模板内容失败 " + absolutePath, e);
        }
    }

    @Override
    public void saveTemplateContent(@NonNull String absolutePath, String content) {
        // Check the path
        checkDirectory(absolutePath);

        // Write file
        Path path = Paths.get(absolutePath);
        try {
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ServiceException("保存模板内容失败 " + absolutePath, e);
        }
    }

    @Override
    public void saveTemplateContent(@NonNull String themeId, @NonNull String absolutePath,
            String content) {
        // Check the path
        checkDirectory(themeId, absolutePath);

        // Write file
        Path path = Paths.get(absolutePath);
        try {
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ServiceException("保存模板内容失败 " + absolutePath, e);
        }
    }

    @Transactional
    @Override
    public void deleteTheme(@NonNull String themeId, @NonNull Boolean deleteSettings) {
        // Get the theme property
        ThemeProperty themeProperty = getThemeOfNonNullBy(themeId);

        if (themeId.equals(getActivatedThemeId())) {
            // Prevent to delete the activated theme
            throw new BadRequestException("不能删除正在使用的主题").setErrorData(themeId);
        }

        try {
            // Delete the folder
            FileUtils.deleteFolder(Paths.get(themeProperty.getThemePath()));
            if (deleteSettings) {
                // Delete theme settings
                themeSettingRepository.deleteByThemeId(themeId);
            }
            // Delete theme cache
            eventPublisher.publishEvent(new ThemeUpdatedEvent(this));
        } catch (Exception e) {
            throw new ServiceException("主题删除失败", e).setErrorData(themeId);
        }
    }

    @Override
    @NonNull
    public List<Group> fetchConfig(@NonNull String themeId) {
        Assert.hasText(themeId, "Theme id must not be blank");

        // Get theme property
        ThemeProperty themeProperty = getThemeOfNonNullBy(themeId);

        if (!themeProperty.isHasOptions()) {
            // If this theme dose not has an option, then return empty list
            return Collections.emptyList();
        }

        try {
            for (String optionsName : SETTINGS_NAMES) {
                // Resolve the options path
                Path optionsPath = Paths.get(themeProperty.getThemePath(), optionsName);

                log.debug("Finding options in: [{}]", optionsPath.toString());

                // Check existence
                if (!Files.exists(optionsPath)) {
                    continue;
                }

                // Read the yaml file
                String optionContent = Files.readString(optionsPath);

                // Resolve it
                return themeConfigResolver.resolve(optionContent);
            }

            return Collections.emptyList();
        } catch (IOException e) {
            throw new ServiceException("读取主题配置文件失败", e);
        }
    }

    @Override
    public String render(String pageName) {
        return fetchActivatedTheme()
                .map(themeProperty ->
                        String.format(RENDER_TEMPLATE, themeProperty.getFolderName(), pageName))
                .orElse(DEFAULT_ERROR_PATH);
    }

    @Override
    public String renderWithSuffix(String pageName) {
        // Get activated theme
        ThemeProperty activatedTheme = getActivatedTheme();
        // Build render url
        return String.format(RENDER_TEMPLATE_SUFFIX, activatedTheme.getFolderName(), pageName);
    }

    @Override
    @NonNull
    public String getActivatedThemeId() {
        if (activatedThemeId == null) {
            synchronized (this) {
                if (activatedThemeId == null) {
                    activatedThemeId = optionService.getByPropertyOrDefault(PrimaryProperties.THEME,
                            String.class,
                            DEFAULT_THEME_ID);
                }
            }
        }
        return activatedThemeId;
    }

    @Override
    @NonNull
    public ThemeProperty getActivatedTheme() {
        if (activatedTheme == null) {
            synchronized (this) {
                if (activatedTheme == null) {
                    // Get theme property
                    activatedTheme = getThemeOfNonNullBy(getActivatedThemeId());
                }
            }
        }
        return activatedTheme;
    }

    /**
     * Sets activated theme.
     *
     * @param activatedTheme activated theme
     */
    private void setActivatedTheme(@Nullable ThemeProperty activatedTheme) {
        this.activatedTheme = activatedTheme;
        this.activatedThemeId = Optional.ofNullable(activatedTheme)
                .map(ThemeProperty::getId)
                .orElse(null);
    }

    @Override
    @NonNull
    public Optional<ThemeProperty> fetchActivatedTheme() {
        return fetchThemePropertyBy(getActivatedThemeId());
    }

    @Override
    @NonNull
    public ThemeProperty activateTheme(@NonNull String themeId) {
        // Check existence of the theme
        ThemeProperty themeProperty = getThemeOfNonNullBy(themeId);

        // Save the theme to database
        optionService.saveProperty(PrimaryProperties.THEME, themeId);

        // Set activated theme
        setActivatedTheme(themeProperty);

        // Clear the cache
        eventPublisher.publishEvent(new ThemeUpdatedEvent(this));

        // Publish a theme activated event
        eventPublisher.publishEvent(new ThemeActivatedEvent(this));

        return themeProperty;
    }

    @Override
    @NonNull
    public ThemeProperty upload(@NonNull MultipartFile file) {
        Assert.notNull(file, "Multipart file must not be null");

        final var newThemeProperty = this.fetcherComposite.fetch(file);
        return attemptToAdd(newThemeProperty);
    }

    @NonNull
    private ThemeProperty attemptToAdd(ThemeProperty newProperty) {
        // 1. check existence
        final var exist = getThemes().stream()
                .anyMatch(themeProperty ->
                        themeProperty.getId().equalsIgnoreCase(newProperty.getId()));
        if (exist) {
            throw new AlreadyExistsException("当前安装的主题已存在");
        }

        // 2. check version compatibility
        // Not support current halo version.
        if (StringUtils.isNotEmpty(newProperty.getRequire())
                && !compareVersion(HaloConst.HALO_VERSION, newProperty.getRequire())) {
            throw new ThemeNotSupportException(
                    "当前主题仅支持 Halo " + newProperty.getRequire() + " 及以上的版本");
        }

        // 3. move the temp folder into templates/themes/{theme_id}
        Path sourceThemePath = Paths.get(newProperty.getThemePath());
        Path targetThemePath = themeWorkDir.resolve(newProperty.getId());
        // clear target theme folder firstly
        deleteFolderQuietly(targetThemePath);

        log.info("Copying new theme({}) from {} to {}",
                newProperty.getId(),
                sourceThemePath,
                targetThemePath);

        try {
            copyFolder(sourceThemePath, targetThemePath);
        } catch (IOException e) {
            // clear data
            deleteFolderQuietly(targetThemePath);
            throw new ServiceException("复制主题文件失败！", e);
        } finally {
            log.info("Clear temporary theme folder {}", sourceThemePath);
            deleteFolderQuietly(sourceThemePath);
        }


        // or else throw should never happen
        final var themeProperty =
                ThemePropertyScanner.INSTANCE.fetchThemeProperty(targetThemePath).orElseThrow();

        // Clear theme cache
        this.eventPublisher.publishEvent(new ThemeUpdatedEvent(this));

        return themeProperty;
    }

    @Override
    @NonNull
    @Deprecated
    public ThemeProperty add(@NonNull Path themeTmpPath) throws IOException {
        Assert.notNull(themeTmpPath, "Theme temporary path must not be null");
        Assert.isTrue(Files.isDirectory(themeTmpPath), "Theme temporary path must be a directory");

        if (log.isTraceEnabled()) {
            log.trace("Children path of [{}]:", themeTmpPath);

            try (Stream<Path> pathStream = Files.list(themeTmpPath)) {
                pathStream.forEach(path -> log.trace(path.toString()));
            }
        }

        // Check property config
        ThemeProperty tmpThemeProperty = getProperty(themeTmpPath);

        // Check theme existence
        boolean isExist = getThemes().stream()
                .anyMatch(themeProperty -> themeProperty.getId()
                        .equalsIgnoreCase(tmpThemeProperty.getId()));

        if (isExist) {
            throw new AlreadyExistsException("当前安装的主题已存在");
        }

        // Not support current halo version.
        if (StringUtils.isNotEmpty(tmpThemeProperty.getRequire())
                && !compareVersion(HaloConst.HALO_VERSION, tmpThemeProperty.getRequire())) {
            throw new ThemeNotSupportException(
                    "当前主题仅支持 Halo " + tmpThemeProperty.getRequire() + " 以上的版本");
        }

        // Copy the temporary path to current theme folder
        Path targetThemePath = themeWorkDir.resolve(tmpThemeProperty.getId());
        copyFolder(themeTmpPath, targetThemePath);

        // Get property again
        ThemeProperty property = getProperty(targetThemePath);

        // Clear theme cache
        this.eventPublisher.publishEvent(new ThemeUpdatedEvent(this));

        // Delete cache
        return property;
    }

    @Override
    public ThemeProperty fetch(@NonNull String uri) {
        Assert.hasText(uri, "Theme remote uri must not be blank");

        final var themeProperty = fetcherComposite.fetch(uri);
        return attemptToAdd(themeProperty);
    }

    @Override
    public ThemeProperty fetchBranch(String uri, String branchName) {
        Assert.hasText(uri, "Theme remote uri must not be blank");

        Path tmpPath = null;

        try {
            // Create temp path
            tmpPath = FileUtils.createTempDirectory();
            // Create temp path
            Path themeTmpPath = tmpPath.resolve(HaloUtils.randomUUIDWithoutDash());

            String repoUrl = StringUtils.appendIfMissingIgnoreCase(uri, ".git", ".git");
            GitUtils.cloneFromGit(repoUrl, themeTmpPath, branchName);

            return add(themeTmpPath);
        } catch (IOException | GitAPIException e) {
            throw new ServiceException("主题拉取失败 " + uri + "。" + e.getMessage(), e);
        } finally {
            deleteFolderQuietly(tmpPath);
        }
    }

    @Override
    public ThemeProperty fetchRelease(@NonNull String uri, @NonNull String tagName) {
        Assert.hasText(uri, "Theme remote uri must not be blank");
        Assert.hasText(tagName, "Theme remote tagName must not be blank");

        Path tmpPath = null;
        try {
            tmpPath = FileUtils.createTempDirectory();

            Path themeTmpPath = tmpPath.resolve(HaloUtils.randomUUIDWithoutDash());

            Map<String, Object> releaseInfo = GithubUtils.getRelease(uri, tagName);

            if (releaseInfo == null) {
                throw new ServiceException("主题拉取失败" + uri);
            }

            String zipUrl = (String) releaseInfo.get(ZIP_FILE_KEY);

            downloadZipAndUnzip(zipUrl, themeTmpPath);

            // find root theme folder
            Path themeRootPath = getThemeRootPath(themeTmpPath);
            log.debug("Got theme root path: [{}]", themeRootPath);
            return add(themeRootPath);
        } catch (IOException e) {
            throw new ServiceException("主题拉取失败 " + uri, e);
        } finally {
            deleteFolderQuietly(tmpPath);
        }
    }

    @Override
    public ThemeProperty fetchLatestRelease(@NonNull String uri) {
        Assert.hasText(uri, "Theme remote uri must not be blank");

        Map<String, Object> releaseInfo = GithubUtils.getLatestRelease(uri);
        if (releaseInfo == null) {
            throw new ServiceException("主题拉取失败" + uri);
        }
        String zipUrl = (String) releaseInfo.get(ZIP_FILE_KEY);

        final var themeProperty = this.fetcherComposite.fetch(zipUrl);
        return attemptToAdd(themeProperty);
    }

    @Override
    public List<ThemeProperty> fetchBranches(String uri) {
        Assert.hasText(uri, "Theme remote uri must not be blank");

        String repoUrl = StringUtils.appendIfMissingIgnoreCase(uri, ".git", ".git");
        List<String> branches = GitUtils.getAllBranchesFromRemote(repoUrl);

        List<ThemeProperty> themeProperties = new ArrayList<>();

        branches.forEach(branch -> {
            ThemeProperty themeProperty = new ThemeProperty();
            themeProperty.setBranch(branch);
            themeProperties.add(themeProperty);
        });

        return themeProperties;
    }

    @Override
    public List<ThemeProperty> fetchReleases(@NonNull String uri) {
        Assert.hasText(uri, "Theme remote uri must not be blank");

        List<String> releases = GithubUtils.getReleases(uri);

        List<ThemeProperty> themeProperties = new ArrayList<>();

        if (releases == null) {
            throw new ServiceException("主题拉取失败！可能原因：当前服务器无法链接到对方服务器或连接超时。");
        }

        releases.forEach(tagName -> {
            ThemeProperty themeProperty = new ThemeProperty();
            themeProperty.setBranch(tagName);
            themeProperties.add(themeProperty);
        });

        return themeProperties;
    }

    @Override
    public void reload() {
        eventPublisher.publishEvent(new ThemeUpdatedEvent(this));
    }

    @Override
    public ThemeProperty update(String themeId) {
        Assert.hasText(themeId, "Theme id must not be blank");

        ThemeProperty updatingTheme = getThemeOfNonNullBy(themeId);

        try {
            pullFromGit(updatingTheme);
        } catch (Exception e) {
            if (e instanceof ThemeNotSupportException) {
                throw (ThemeNotSupportException) e;
            }
            if (e instanceof GitAPIException) {
                throw new ThemeUpdateException("主题更新失败！" + e.getMessage(), e);
            }
            throw new ThemeUpdateException("主题更新失败！您与主题作者可能同时更改了同一个文件，您也可以尝试删除主题并重新拉取最新的主题", e)
                    .setErrorData(themeId);
        }

        eventPublisher.publishEvent(new ThemeUpdatedEvent(this));

        return getThemeOfNonNullBy(themeId);
    }

    @Override
    public ThemeProperty update(String themeId, MultipartFile file) {
        Assert.hasText(themeId, "Theme id must not be blank");
        Assert.notNull(themeId, "Theme file must not be blank");

        if (!StringUtils.endsWithIgnoreCase(file.getOriginalFilename(), ".zip")) {
            throw new UnsupportedMediaTypeException("不支持的文件类型: " + file.getContentType())
                    .setErrorData(file.getOriginalFilename());
        }

        ThemeProperty updatingTheme = getThemeOfNonNullBy(themeId);

        ZipInputStream zis = null;
        Path tempPath = null;

        try {
            // Create temp directory
            tempPath = FileUtils.createTempDirectory();

            String basename =
                    FilenameUtils.getBasename(Objects.requireNonNull(file.getOriginalFilename()));
            Path themeTempPath = tempPath.resolve(basename);

            // Check directory traversal
            FileUtils.checkDirectoryTraversal(tempPath, themeTempPath);

            // New zip input stream
            zis = new ZipInputStream(file.getInputStream());

            // Unzip to temp path
            FileUtils.unzip(zis, themeTempPath);

            Path preparePath = getThemeRootPath(themeTempPath);

            ThemeProperty prepareThemeProperty = getProperty(preparePath);

            if (!prepareThemeProperty.getId().equals(updatingTheme.getId())) {
                throw new ServiceException("上传的主题包不是该主题的更新包: " + file.getOriginalFilename());
            }

            // Not support current halo version.
            if (StringUtils.isNotEmpty(prepareThemeProperty.getRequire())
                    && !compareVersion(HaloConst.HALO_VERSION, prepareThemeProperty.getRequire())) {
                throw new ThemeNotSupportException("新版本主题仅支持 Halo "
                        + prepareThemeProperty.getRequire()
                        + " 及以上的版本，请更新 Halo 后重试。");
            }

            // Coping new theme files to old theme folder.
            copyFolder(preparePath, Paths.get(updatingTheme.getThemePath()));

            eventPublisher.publishEvent(new ThemeUpdatedEvent(this));

            // Gets theme property again.
            return getProperty(Paths.get(updatingTheme.getThemePath()));
        } catch (IOException e) {
            throw new ServiceException("更新主题失败: " + file.getOriginalFilename(), e);
        } finally {
            // Close zip input stream
            FileUtils.closeQuietly(zis);
            // Delete folder after testing
            deleteFolderQuietly(tempPath);
        }
    }

    private void pullFromGit(@NonNull ThemeProperty themeProperty) throws
            IOException, GitAPIException, URISyntaxException {
        // 0. git checkout master
        // 1. git add .
        // 2. git commit -m "commit by halo"
        // 3. git pull origin master --rebase
        Assert.notNull(themeProperty, "Theme property must not be null");

        // Get branch name
        final var branchName = themeProperty.getBranch();

        // 1. commit latest changes
        // 2. get last commit id
        // 3. fetch origin/$branch
        // 4. rebase origin/$branch into $branch
        //

        Git git = null;
        RevCommit lastCommit = null;
        try {
            git = GitUtils.openOrInit(Paths.get(themeProperty.getThemePath()));
            var repository = git.getRepository();
            git.checkout()
                    .setName(branchName)
                    .call();

            // Add all changes
            git.add().addFilepattern(".").call();
            // Commit the changes
            git.commit()
                    .setAllowEmpty(true)
                    .setSign(false)
                    .setMessage("Commit by halo automatically")
                    .call();
            RevWalk revWalk = new RevWalk(repository);
            Ref ref = repository.findRef(Constants.HEAD);
            Assert.notNull(ref, Constants.HEAD + " ref was not found!");
            lastCommit = revWalk.parseCommit(ref.getObjectId());
            // Force to set remote name
            git.remoteRemove().setRemoteName(THEME_PROVIDER_REMOTE_NAME).call();
            RemoteConfig remoteConfig = git.remoteAdd()
                    .setName(THEME_PROVIDER_REMOTE_NAME)
                    .setUri(new URIish(themeProperty.getRepo()))
                    .call();

            // Check out to specified branch
            if (!StringUtils.equalsIgnoreCase(branchName, git.getRepository().getBranch())) {
                boolean present = git.branchList()
                        .call()
                        .stream()
                        .map(Ref::getName)
                        .anyMatch(name -> StringUtils.equalsIgnoreCase(name, branchName));

                git.checkout()
                        .setCreateBranch(true)
                        .setForced(!present)
                        .setName(branchName)
                        .call();
            }

            // Pull with rebasing
            PullResult pullResult = git.pull()
                    .setRemote(remoteConfig.getName())
                    .setRemoteBranchName(branchName)
                    .setRebase(true)
                    .call();

            if (!pullResult.isSuccessful()) {
                log.error("Rebase result: [{}]", pullResult.getRebaseResult());
                log.error("Merge result: [{}]", pullResult.getMergeResult());
                throw new ThemeUpdateException("拉取失败！您与主题作者可能同时更改了同一个文件");
            }

            String latestTagName =
                    (String) GithubUtils.getLatestRelease(themeProperty.getRepo()).get(TAG_KEY);
            git.checkout().setName(latestTagName).call();

            // updated successfully.
            ThemeProperty updatedThemeProperty =
                    getProperty(Paths.get(themeProperty.getThemePath()));

            // Not support current halo version.
            if (StringUtils.isNotEmpty(updatedThemeProperty.getRequire())
                    && !compareVersion(HaloConst.HALO_VERSION, updatedThemeProperty.getRequire())) {
                throw new ThemeNotSupportException("新版本主题仅支持 Halo "
                        + updatedThemeProperty.getRequire()
                        + " 以上的版本");
            }
        } catch (Exception e) {
            if (git != null && lastCommit != null) {
                log.warn("Rollback theme files to [{}]", lastCommit.getFullMessage());
                git.reset()
                        .setMode(ResetCommand.ResetType.HARD)
                        .setRef(lastCommit.getName())
                        .call();
            }
            throw e;
        } finally {
            GitUtils.closeQuietly(git);
        }

    }

    /**
     * Downloads zip file and unzip it into specified path.
     *
     * @param zipUrl zip url must not be null
     * @param targetPath target path must not be null
     * @throws IOException throws when download zip or unzip error
     */
    private void downloadZipAndUnzip(@NonNull String zipUrl, @NonNull Path targetPath)
            throws IOException {
        Assert.hasText(zipUrl, "Zip url must not be blank");

        log.debug("Downloading [{}]", zipUrl);
        // Download it
        ResponseEntity<byte[]> downloadResponse = restTemplate.getForEntity(zipUrl, byte[].class);

        log.debug("Download response: [{}]", downloadResponse.getStatusCode());

        if (downloadResponse.getStatusCode().isError() || downloadResponse.getBody() == null) {
            throw new ServiceException("下载失败 "
                    + zipUrl
                    + ", 状态码: "
                    + downloadResponse.getStatusCode());
        }

        log.debug("Downloaded [{}]", zipUrl);
        // Unzip it
        FileUtils.unzip(downloadResponse.getBody(), targetPath);
    }

    /**
     * Check if directory is valid or not.
     *
     * @param absoluteName must not be blank
     * @throws ForbiddenException throws when the given absolute directory name is invalid
     */
    private void checkDirectory(@NonNull String absoluteName) {
        ThemeProperty activeThemeProperty = getThemeOfNonNullBy(getActivatedThemeId());
        FileUtils.checkDirectoryTraversal(activeThemeProperty.getThemePath(), absoluteName);
    }

    /**
     * Check if directory is valid or not.
     *
     * @param themeId themeId must not be blank
     * @param absoluteName throws when the given absolute directory name is invalid
     */
    private void checkDirectory(@NonNull String themeId, @NonNull String absoluteName) {
        ThemeProperty themeProperty = getThemeOfNonNullBy(themeId);
        FileUtils.checkDirectoryTraversal(themeProperty.getThemePath(), absoluteName);
    }

    /**
     * Gets theme property.
     *
     * @param themePath must not be null
     * @return theme property
     */
    @NonNull
    private ThemeProperty getProperty(@NonNull Path themePath) {
        return ThemePropertyScanner.INSTANCE.fetchThemeProperty(themePath)
                .orElseThrow(() -> new ThemePropertyMissingException(themePath + " 没有说明文件")
                        .setErrorData(themePath));
    }

    /**
     * Get theme root path.
     *
     * @param themePath theme folder path
     * @return real theme root path
     * @throws IOException IO exception
     */
    @NonNull
    private Path getThemeRootPath(@NonNull Path themePath) throws IOException {
        return FileUtils.findRootPath(themePath,
                path -> StringUtils.equalsAny(path.getFileName().toString(),
                        "theme.yaml",
                        "theme.yml"))
                .orElseThrow(() ->
                        new BadRequestException("无法准确定位到主题根目录，请确认主题目录中包含 theme.yml（theme.yaml）。"));
    }
}
