package org.metaborg.core.config;

import java.io.Serializable;

import org.metaborg.core.language.LanguageName;

/**
 * Language-specific source.
 */
public class LangSource implements ISourceConfig, Serializable {
    private static final long serialVersionUID = 4230206217435589272L;

    /**
     * Language for which sources are in this directory.
     */
    public final LanguageName language;

    /**
     * Source directory, relative to the project root.
     */
    public final String directory;

    public LangSource(LanguageName language, String directory) {
        this.language = language;
        this.directory = directory;
    }

    @Override public void accept(ISourceVisitor visitor) {
        visitor.visit(this);
    }


    @Override public String toString() {
        return "source " + directory + " for " + language;
    }

}
