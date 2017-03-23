package com.github.blindpirate.gogradle.vcs.mercurial;

import com.github.blindpirate.gogradle.GolangRepositoryHandler;
import com.github.blindpirate.gogradle.core.cache.GlobalCacheManager;
import com.github.blindpirate.gogradle.core.dependency.produce.DependencyVisitor;
import com.github.blindpirate.gogradle.util.ProcessUtils;
import com.github.blindpirate.gogradle.vcs.GitMercurialAccessor;
import com.github.blindpirate.gogradle.vcs.GitMercurialDependencyManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MercurialDependencyManager extends GitMercurialDependencyManager {
    public static final String DEFAULT_BRANCH = "default";

    private final HgClientAccessor accessor;

    @Inject
    public MercurialDependencyManager(HgClientAccessor accessor,
                                      DependencyVisitor visitor,
                                      GlobalCacheManager cacheManager,
                                      GolangRepositoryHandler repositoryHandler,
                                      ProcessUtils processUtils) {
        super(cacheManager, visitor, repositoryHandler);
        this.accessor = accessor;
    }

    @Override
    protected String getDefaultBranchName() {
        return DEFAULT_BRANCH;
    }

    @Override
    protected GitMercurialAccessor getAccessor() {
        return accessor;
    }
//
//    private MercurialAccessor determineAccessor(Hg4JMercurialAccessor hg4JAccessor,
//                                                HgClientMercurialAccessor hgClientAccessor) {
//        try {
//            Process process = processUtils.run("hg", "version");
//            Assert.isTrue(processUtils.getStdout(process).contains("Mercurial"),
//                    "Can't find hg in $PATH, do you have mercurial client installed?");
//            return hgClientAccessor;
//        } catch (Exception e) {
//            LOGGER.info("exception in hg version: {}, use hg4j", e);
//            return hg4JAccessor;
//        }
//    }
//
//    @Override
//    protected void doReset(ResolvedDependency dependency, Path globalCachePath) {
//        GitMercurialResolvedDependency resolvedDependency = (GitMercurialResolvedDependency) dependency;
//        HgRepository repository = accessor.getRepository(globalCachePath.toFile());
//        accessor.resetToSpecificNodeId(repository, resolvedDependency.getVersion());
//    }
//
//    @Override
//    protected ResolvedDependency createResolvedDependency(NotationDependency dependency,
//                                                          File directory,
//                                                          HgRepository hgRepository,
//                                                          HgChangeset hgChangeset) {
//        GitMercurialNotationDependency notationDependency = GitMercurialNotationDependency.class.cast(dependency);
//        GitMercurialResolvedDependency ret = GitMercurialResolvedDependency.mercurialBuilder()
//                .withNotationDependency(notationDependency)
//                .withName(dependency.getPackage().getRootPath())
//                .withCommitId(hgChangeset.getId())
//                .withRepoUrl(accessor.getRemoteUrl(hgRepository))
//                .withTag(notationDependency.getTag())
//                .withCommitTime(hgChangeset.getCommitTime())
//                .build();
//        GolangDependencySet dependencies = dependency.getStrategy().produce(ret, directory, visitor, BUILD);
//        ret.setDependencies(dependencies);
//
//        setVendorUpdateTimeIfNecessary(hgRepository, dependencies);
//        return ret;
//    }
//
//    private void setVendorUpdateTimeIfNecessary(HgRepository repository, GolangDependencySet dependencies) {
//        dependencies.flatten().stream()
//                .filter(dependency -> dependency instanceof VendorResolvedDependency)
//                .map(dependency -> (VendorResolvedDependency) dependency)
//                .forEach(dependency -> {
//                    String relativePath = StringUtils.toUnixString(dependency.getRelativePathToHost());
//                    dependency.setUpdateTime(accessor.getLastCommitTimeOfPath(repository, relativePath));
//                });
//    }
//
//    @Override
//    protected void resetToSpecificVersion(HgRepository hgRepository, HgChangeset hgChangeset) {
//        accessor.resetToSpecificNodeId(hgRepository, hgChangeset.getId());
//    }
//
//    @Override
//    protected HgChangeset determineVersion(HgRepository repository, NotationDependency dependency) {
//        GitMercurialNotationDependency notationDependency = (GitMercurialNotationDependency) dependency;
//        if (notationDependency.getTag() != null) {
//            String tag = notationDependency.getTag();
//            Optional<HgChangeset> changeset = accessor.findChangesetByTag(repository, tag);
//            if (changeset.isPresent()) {
//                return changeset.get();
//            }
//        }
//        if (isConcreteCommit(notationDependency.getCommit())) {
//            String nodeId = notationDependency.getCommit();
//            Optional<HgChangeset> changeset = accessor.findChangesetById(repository, nodeId);
//            if (changeset.isPresent()) {
//                return changeset.get();
//            }
//        }
//
//        return accessor.headOfBranch(repository, DEFAULT_BRANCH);
//    }
//
//    private boolean isConcreteCommit(String nodeId) {
//        return nodeId != null && !GitMercurialNotationDependency.NEWEST_COMMIT.equals(nodeId);
//    }
//
//    @Override
//    protected HgRepository updateRepository(NotationDependency dependency, HgRepository hgRepository, File directory) {
//        accessor.pull(hgRepository);
//        return hgRepository;
//    }
//
//    @Override
//    protected HgRepository initRepository(NotationDependency dependency, File directory) {
//        List<String> urls = GitMercurialNotationDependency.class.cast(dependency).getUrls();
//        Assert.isNotEmpty(urls, "Urls cannot be empty!");
//        for (int i = 0; i < urls.size(); ++i) {
//            IOUtils.clearDirectory(directory);
//            String url = urls.get(i);
//            try {
//                return accessor.cloneWithUrl(directory, url);
//            } catch (Throwable e) {
//                LOGGER.quiet("Cloning with url {} failed, the cause is {}", url, e.getMessage());
//                if (i == urls.size() - 1) {
//                    throw DependencyResolutionException.cannotCloneRepository(dependency, e);
//                }
//            }
//        }
//        throw new IllegalStateException("Urls is empty:" + dependency);
//    }
//
//    @Override
//    protected Optional<HgRepository> repositoryMatch(File directory, NotationDependency dependency) {
//        HgRepository repository = accessor.getRepository(directory);
//        String url = accessor.getRemoteUrl(directory);
//        if (GitMercurialNotationDependency.class.cast(dependency).getUrls().contains(url)) {
//            return Optional.of(repository);
//        } else {
//            return Optional.empty();
//        }
//    }
}
