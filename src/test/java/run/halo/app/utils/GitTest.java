package run.halo.app.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.junit.jupiter.api.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Git test.
 *
 * @author johnniang
 * @date 2020.01.21
 */
@Slf4j
@ActiveProfiles("test")
class GitTest {

    Path tempPath;

    @BeforeEach
    void setUp() throws IOException {
        tempPath = Files.createTempDirectory("git-test");
    }

    @AfterEach
    void destroy() throws IOException {
        FileUtils.deleteFolder(tempPath);
    }

    @Test
    void openFailureTest() {
        Assertions.assertThrows(RepositoryNotFoundException.class, () -> Git.open(tempPath.toFile()));
    }

    @Test
    void initTest() throws GitAPIException {
        Git.init().setDirectory(tempPath.toFile()).call();
    }

    @Test
    void statusSuccessfulTest() throws GitAPIException {
        Git git = Git.init().setDirectory(tempPath.toFile()).call();
        Status status = git.status().call();
        log.debug("Status missing: [{}]", status.getMissing());
    }

    @Test
    void remoteAddTest() throws GitAPIException, URISyntaxException {
        Git git = Git.init().setDirectory(tempPath.toFile()).call();
        git.remoteRemove().setRemoteName("theme-provider").call();
        git.remoteAdd().setName("theme-provider").setUri(new URIish("https://github.com/halo-dev/halo-theme-pinghsu.git")).call();
        List<RemoteConfig> remoteConfigs = git.remoteList().call();
        remoteConfigs.forEach(remoteConfig -> log.debug("name: [{}], url: [{}]", remoteConfig.getName(), remoteConfig.getURIs()));
    }

    @Test
    @Disabled("Due to time-consumption cloning")
    void cloneTest() throws GitAPIException {
        cloneRepository();
    }

    @Test
    @Disabled("Due to time-consumption cloning and pulling")
    void pullTest() throws GitAPIException {
        Git git = cloneRepository();
        git.pull().call();
        git.clean().call();
        git.close();
    }

    @Test
    @Disabled("Due to time-consumption fetching")
    void getAllBranchesFromRemote() {
        List<String> branches = GitUtils.getAllBranchesFromRemote("https://github.com/halo-dev/halo-theme-hux.git");
        assertNotNull(branches);
    }

    @Test
    @Disabled("Due to time-consumption fetching")
    void getAllBranchesWithInvalidURL() {
        List<String> branches = GitUtils.getAllBranchesFromRemote("https://github.com/halo-dev/halo-theme.git");
        assertNotNull(branches);
        assertEquals(0, branches.size());
    }

    @Test
    void getAllBranchesTest() throws GitAPIException, IOException {
        Git git = Git.init().setDirectory(tempPath.toFile()).call();

        git.add().addFilepattern(".").call();
        git.commit().setAllowEmpty(true).setSign(false).setMessage("Empty commit").call();

        git.branchCreate().setName("main").call();
        git.branchCreate().setName("dev").call();
        Set<String> branches = git.branchList()
                .call()
                .stream()
                .map(ref -> {
                    String refName = ref.getName();
                    return refName.substring(refName.lastIndexOf('/') + 1);
                }).collect(Collectors.toSet());
        assertTrue(branches.containsAll(Arrays.asList("main", "dev")));
    }

    @Test
    void getBranchesFromRemote() throws GitAPIException {
        Map<String, Ref> refMap = Git.lsRemoteRepository()
                .setRemote("https://github.com/halo-dev/halo.git")
                .setHeads(true)
                .setTags(true)
                .callAsMap();
        refMap.forEach((name, ref) -> {
            log.debug("name: [{}], ref: [{}]", name, ref);
        });
    }

    @Test
    void mergeTwoLocalRepo() throws GitAPIException, IOException, URISyntaxException, InterruptedException {
        final var oldRepoPath = tempPath.resolve("old-repo");
        final var newRepoPath = tempPath.resolve("new-repo");

        // prepare one local repo
        final var oldGit = Git.init()
                .setDirectory(oldRepoPath.toFile())
                .call();
        final var testTextPath = oldRepoPath.resolve("test.txt");
        Files.writeString(testTextPath, "hello old git");
        oldGit.commit()
                .setSign(false)
                .setMessage("commit test.txt at old repo")
                .call();

        printAllLog(oldGit);

        // copy old repo path to new repo path
        FileUtils.copyFolder(oldRepoPath, newRepoPath);

        final var newGit = Git.init()
                .setDirectory(newRepoPath.toFile())
                .call();

        Files.writeString(newRepoPath.resolve("test.txt"), "hello old git\nhello new git");
        TimeUnit.SECONDS.sleep(1);
        newGit.commit()
                .setSign(false)
                .setMessage("commit test.txt at new repo")
                .call();

        printAllLog(newGit);

        // add new repo as old repo remote
        oldGit.remoteAdd().setName("newRepo")
                .setUri(new URIish(newRepoPath.toString()))
                .call();

        oldGit.fetch()
                .setRemote("newRepo")
                .call();
        final var rebaseResult = oldGit.rebase()
                .setUpstream("master")
                .setUpstreamName("newRepo")
                .call();

        log.debug("{} | {} | {}", rebaseResult.getCurrentCommit(), rebaseResult.getConflicts(), rebaseResult.getStatus());

        printAllLog(oldGit);
    }

    @Test
    @Disabled("Time consume")
    void findTags() throws GitAPIException {
        try (Git git = cloneRepository()) {
            git.branchList()
                    .setListMode(ListBranchCommand.ListMode.ALL)
                    .call()
                    .forEach(ref -> log.debug(ref.getName()));

            git.tagList()
                    .call()
                    .forEach(ref -> log.debug(ref.getName()));
        }
    }

    void printAllLog(Git git) throws IOException, GitAPIException {
        var commits = git.log().all().call();
        for (var commit : commits) {
            log.debug("{}: {} {}", git.toString(), commit, commit.getFullMessage());
        }
    }

    Git cloneRepository() throws GitAPIException {
        return Git.cloneRepository()
                .setURI("https://github.com/halo-dev/halo-theme-pinghsu.git")
                .setDirectory(tempPath.toFile())
                .call();
    }
}
