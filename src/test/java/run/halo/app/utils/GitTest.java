package run.halo.app.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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
        final var thread = new Thread(() -> {
            log.info("Clear temporary folder.");
            FileUtils.deleteFolderQuietly(tempPath);
        });
        Runtime.getRuntime().addShutdownHook(thread);
    }

    //    @AfterEach
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
        git.remoteAdd().setName("theme-provider")
                .setUri(new URIish("https://github.com/halo-dev/halo-theme-pinghsu.git")).call();
        List<RemoteConfig> remoteConfigs = git.remoteList().call();
        remoteConfigs.forEach(remoteConfig -> log
                .debug("name: [{}], url: [{}]", remoteConfig.getName(), remoteConfig.getURIs()));
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
        List<String> branches =
                GitUtils.getAllBranchesFromRemote("https://github.com/halo-dev/halo-theme-hux.git");
        assertNotNull(branches);
    }

    @Test
    @Disabled("Due to time-consumption fetching")
    void getAllBranchesWithInvalidURL() {
        List<String> branches =
                GitUtils.getAllBranchesFromRemote("https://github.com/halo-dev/halo-theme.git");
        assertNotNull(branches);
        assertEquals(0, branches.size());
    }

    @Test
    void getAllBranchesTest() throws GitAPIException {
        try (Git git = Git.init().setDirectory(tempPath.toFile()).call()) {
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
    void mergeTwoLocalRepo() throws GitAPIException, IOException, URISyntaxException {
        final var oldRepoPath = tempPath.resolve("old-repo");
        final var newRepoPath = tempPath.resolve("new-repo");

        // prepare one local repo
        try (final var oldGit = Git.init()
                .setDirectory(oldRepoPath.toFile())
                .call()) {

            final var testTextInOldRepoPath = oldRepoPath.resolve("test.txt");
            Files.writeString(testTextInOldRepoPath, "hello old git");
            oldGit.add().addFilepattern(".").call();
            oldGit.commit()
                    .setSign(false)
                    .setMessage("commit test.txt at old repo")
                    .call();

            printAllLog(oldGit);

            // copy old repo path to new repo path
            FileUtils.copyFolder(oldRepoPath, newRepoPath);

            try (final var newGit = Git.init()
                    .setDirectory(newRepoPath.toFile())
                    .call()) {

                Files.writeString(newRepoPath.resolve("test.txt"), "hello old git\nhello new git");
                newGit.add().addFilepattern(".").call();
                newGit.commit()
                        .setSign(false)
                        .setMessage("commit test.txt at new repo")
                        .call();

                final var refs = newGit.branchList()
                        .setListMode(ListBranchCommand.ListMode.ALL)
                        .call();
                refs.forEach(ref -> {
                    log.debug("Ref in new repo: {}", ref);
                });

                printAllLog(newGit);
            }

            // add new repo as old repo remote
            oldGit.remoteAdd().setName("newRepo")
                    .setUri(new URIish(newRepoPath.toString()))
                    .call();

            oldGit.fetch()
                    .setRemote("newRepo")
                    .call();

            final var refs = oldGit.branchList()
                    .setListMode(ListBranchCommand.ListMode.ALL)
                    .call();

            refs.forEach(ref -> log.debug("Ref in old repo: {}", ref));

            final var testTextInOldRepo = Files.readString(testTextInOldRepoPath);
            Assertions.assertEquals("hello old git", testTextInOldRepo);

            final var rebaseResult = oldGit.rebase()
                    .setUpstream("newRepo/master")
                    .setStrategy(MergeStrategy.THEIRS)
                    .call();

            log.debug("{} | {} | {}", rebaseResult.getCurrentCommit(), rebaseResult.getConflicts(),
                    rebaseResult.getStatus());
            Assertions.assertTrue(rebaseResult.getStatus().isSuccessful());

            final var testTextAfterRebase = Files.readString(testTextInOldRepoPath);
            Assertions.assertEquals("hello old git\nhello new git", testTextAfterRebase);

            printAllLog(oldGit);
        }
    }

    @Test
    @Disabled("Time consume")
    void findTags() throws GitAPIException, IOException {
        Git.lsRemoteRepository()
                .setRemote("https://gitee.com/xzhuz/halo-theme-xue.git")
                .setTags(true)
                .setHeads(false)
                .call()
                .forEach(ref -> {
                    log.info("ref: {}, object id: {}", ref.getName(), ref.getObjectId());
                });

        try (final var git = cloneRepository("https://gitee.com/xzhuz/halo-theme-xue.git")) {
            git.branchList()
                    .setListMode(ListBranchCommand.ListMode.ALL)
                    .call()
                    .forEach(ref -> log.debug("ref: {}, object id: {}", ref.getName(), ref.getObjectId()));

            ObjectId objectId = ObjectId.fromString("51bf554e58f38cff22bb93f8e6cd8f8b72aa2d64");
            RevWalk revWalk = new RevWalk(git.getRepository());
            revWalk.reset();
            revWalk.setTreeFilter(TreeFilter.ANY_DIFF);
            revWalk.sort(RevSort.TOPO, true);
            revWalk.sort(RevSort.COMMIT_TIME_DESC, true);
            RevCommit revCommit = revWalk.parseCommit(objectId);
            log.debug("Found commit: {} for object: {}", revCommit, objectId);
            log.debug("Commit details: {} {} {}",
                    revCommit.getName(),
                    revCommit.getFullMessage(),
                    new Timestamp(revCommit.getCommitTime() * 1000L));
            git.tagList()
                    .call()
                    .forEach(ref -> log.debug("ref: {}, object id: {}", ref.getName(), ref.getObjectId()));
        }
    }

    void printAllLog(Git git) throws IOException, GitAPIException {
        var commits = git.log().all().call();
        for (var commit : commits) {
            log.debug("{}: {} {}", git.toString(), commit, commit.getFullMessage());
        }
    }

    Git cloneRepository() throws GitAPIException {
        return cloneRepository("https://github.com/halo-dev/halo-theme-pinghsu.git");
    }

    Git cloneRepository(String url) throws GitAPIException {
        return Git.cloneRepository()
                .setURI(url)
                .setDirectory(tempPath.toFile())
                .call();
    }
}
