package org.metaborg.core.config;

import javax.annotation.Nullable;

import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.language.LanguageIdentifier;

/**
 * Builder for {@link ILanguageComponentConfig} objects.
 */
public interface IProjectConfigBuilder {
    /**
     * Builds the configuration.
     * 
     * @return The built configuration.
     */
    IProjectConfig build(@Nullable FileObject rootFolder);

    /**
     * Resets the values of this builder.
     *
     * @return This builder.
     */
    IProjectConfigBuilder reset();

    /**
     * Copies the values from the specified configuration.
     *
     * @param config
     *            The configuration to copy values from.
     */
    IProjectConfigBuilder copyFrom(IProjectConfig config);


    /**
     * Sets the MetaBorg version.
     *
     * @param metaborgVersion
     *            The MetaBorg version.
     * @return This builder.
     */
    IProjectConfigBuilder withMetaborgVersion(String metaborgVersion);

    /**
     * Sets the compile-time dependencies.
     *
     * @param deps
     *            The compile-time dependency identifiers.
     * @return This builder.
     */
    IProjectConfigBuilder withCompileDeps(Iterable<LanguageIdentifier> deps);

    /**
     * Adds compile dependencies.
     *
     * @param deps
     *            The compile dependency identifiers.
     * @return This builder.
     */
    IProjectConfigBuilder addCompileDeps(Iterable<LanguageIdentifier> deps);

    /**
     * Sets the source dependencies.
     *
     * @param deps
     *            The source dependency identifiers.
     * @return This builder.
     */
    IProjectConfigBuilder withSourceDeps(Iterable<LanguageIdentifier> deps);

    /**
     * Adds source dependencies.
     *
     * @param deps
     *            The source dependency identifiers.
     * @return This builder.
     */
    IProjectConfigBuilder addSourceDeps(Iterable<LanguageIdentifier> deps);

    /**
     * Sets the java dependencies.
     *
     * @param deps
     *            The java dependency identifiers.
     * @return This builder.
     */
    IProjectConfigBuilder withJavaDeps(Iterable<LanguageIdentifier> deps);

    /**
     * Adds java dependencies.
     *
     * @param deps
     *            The java dependency identifiers.
     * @return This builder.
     */
    IProjectConfigBuilder addJavaDeps(Iterable<LanguageIdentifier> deps);

    /**
     * Sets the typesmart property.
     *
     * @param typesmart
     *            The typesmart property.
     * @return This builder.
     */
    IProjectConfigBuilder withTypesmart(boolean typesmart);

    /**
     * Sets whether incrementalization is enabled for the constraint solver.
     * 
     * @param incremental
     *            True to enable, false to disable.
     * @return This builder.
     */
    IProjectConfigBuilder withIncrementalConstraintSolver(boolean incremental);
}
