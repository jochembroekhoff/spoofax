package org.metaborg.core.config;

import javax.annotation.Nullable;

import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.language.ILanguageComponent;
import org.metaborg.util.file.FileAccess;

/**
 * Writes a configuration for the specified {@link ILanguageComponent}.
 */
public interface ILanguageComponentConfigWriter {
    /**
     * Writes the specified configuration for the specified language component.
     *
     * @param root
     *            The language root folder.
     * @param config
     *            The configuration to write.
     * @param access
     */
    void write(FileObject root, ILanguageComponentConfig config, @Nullable FileAccess access) throws ConfigException;
}