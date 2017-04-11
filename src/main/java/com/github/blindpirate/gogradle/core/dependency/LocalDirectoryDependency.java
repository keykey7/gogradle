package com.github.blindpirate.gogradle.core.dependency;

import com.github.blindpirate.gogradle.GogradleGlobal;
import com.github.blindpirate.gogradle.core.dependency.install.LocalDirectoryDependencyInstaller;
import com.github.blindpirate.gogradle.core.dependency.parse.DirMapNotationParser;
import com.github.blindpirate.gogradle.core.dependency.parse.MapNotationParser;
import com.github.blindpirate.gogradle.core.dependency.resolve.DependencyResolver;
import com.github.blindpirate.gogradle.core.exceptions.DependencyResolutionException;
import com.github.blindpirate.gogradle.util.IOUtils;
import com.github.blindpirate.gogradle.util.StringUtils;
import com.github.blindpirate.gogradle.vcs.git.GolangRepository;
import com.google.common.collect.ImmutableMap;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.File;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import static com.github.blindpirate.gogradle.core.dependency.install.DependencyInstaller.CURRENT_VERSION_INDICATOR_FILE;
import static com.github.blindpirate.gogradle.util.IOUtils.isValidDirectory;

public class LocalDirectoryDependency extends AbstractNotationDependency implements ResolvedDependency {
    private static final File EMPTY_DIR = null;
    private static final long serialVersionUID = 1;
    private static final Logger LOGGER = Logging.getLogger(LocalDirectoryDependency.class);


    private File rootDir;

    private GolangDependencySet dependencies = GolangDependencySet.empty();

    public static LocalDirectoryDependency fromLocal(String name, File rootDir) {
        LocalDirectoryDependency ret = new LocalDirectoryDependency();
        ret.setName(name);
        ret.setDir(rootDir);
        return ret;
    }

    public File getRootDir() {
        return rootDir;
    }

    public void setDir(String dir) {
        if (!GolangRepository.EMPTY_DIR.equals(dir)) {
            setDir(new File(dir));
        }
    }

    private void setDir(File rootDir) {
        this.rootDir = rootDir;
        if (!isValidDirectory(rootDir)) {
            throw DependencyResolutionException.directoryIsInvalid(rootDir);
        }
    }

    @Override
    public ResolvedDependency doResolve(ResolveContext context) {
        if (rootDir != EMPTY_DIR) {
            this.dependencies = context.produceTransitiveDependencies(this, rootDir);
        }
        return this;
    }

    @Override
    public long getUpdateTime() {
        return rootDir.lastModified();
    }

    public void setDependencies(GolangDependencySet dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public GolangDependencySet getDependencies() {
        return dependencies;
    }

    @Override
    public Map<String, Object> toLockedNotation() {
        LOGGER.warn("You are locking a dependency existed only on your local filesystem, "
                + "which may cause issues on other one's computer.");
        return ImmutableMap.of(MapNotationParser.NAME_KEY, getName(),
                DirMapNotationParser.DIR_KEY, StringUtils.toUnixString(rootDir));
    }

    @Override
    public void installTo(File targetDirectory) {
        if (rootDir != EMPTY_DIR) {
            GogradleGlobal.getInstance(LocalDirectoryDependencyInstaller.class).install(this, targetDirectory);
            IOUtils.write(targetDirectory, CURRENT_VERSION_INDICATOR_FILE, formatVersion());
        }
    }

    @Override
    public String formatVersion() {
        return rootDir == EMPTY_DIR ? "" : StringUtils.toUnixString(rootDir);
    }

    @Override
    protected Class<? extends DependencyResolver> getResolverClass() {
        throw new UnsupportedOperationException();
    }

    // version of local directory is its timestamp
    @Override
    public String getVersion() {
        return Instant.ofEpochMilli(getUpdateTime()).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        LocalDirectoryDependency that = (LocalDirectoryDependency) o;
        return Objects.equals(rootDir, that.rootDir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rootDir, super.hashCode());
    }
}