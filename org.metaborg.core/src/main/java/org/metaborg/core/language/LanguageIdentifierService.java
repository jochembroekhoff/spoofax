package org.metaborg.core.language;

import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.metaborg.core.MetaborgException;
import org.metaborg.core.MetaborgRuntimeException;
import org.metaborg.core.language.dialect.IDialectIdentifier;
import org.metaborg.core.language.dialect.IdentifiedDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class LanguageIdentifierService implements ILanguageIdentifierService {
    private static final Logger logger = LoggerFactory.getLogger(LanguageIdentifierService.class);

    private final ILanguageService languageService;
    private final IDialectIdentifier dialectIdentifier;


    @Inject public LanguageIdentifierService(ILanguageService languageService, IDialectIdentifier dialectIdentifier) {
        this.languageService = languageService;
        this.dialectIdentifier = dialectIdentifier;
    }


    @Override public boolean identify(FileObject resource, ILanguageImpl language) {
        final Iterable<IdentificationFacet> facets = language.facets(IdentificationFacet.class);
        if(Iterables.isEmpty(facets)) {
            logger.error("Cannot identify resources of {}, language does not have an identification facet", language);
            return false;
        }
        boolean identified = false;
        for(IdentificationFacet facet : facets) {
            identified = identified || facet.identify(resource);
        }
        return identified;
    }

    @Override public @Nullable ILanguageImpl identify(FileObject resource) {
        return identify(resource, languageService.getAllImpls());
    }

    @Override public @Nullable IdentifiedResource identifyToResource(FileObject resource) {
        return identifyToResource(resource, languageService.getAllImpls());
    }

    @Override public @Nullable ILanguageImpl identify(FileObject resource, Iterable<? extends ILanguageImpl> languages) {
        final IdentifiedResource identified = identifyToResource(resource, languages);
        if(identified == null) {
            return null;
        }
        return identified.dialectOrLanguage();
    }

    @Override public @Nullable IdentifiedResource identifyToResource(FileObject resource,
        Iterable<? extends ILanguageImpl> languages) {
        // Ignore directories.
        try {
            if(resource.getType() == FileType.FOLDER) {
                return null;
            }
        } catch(FileSystemException e1) {
            return null;
        }

        // Try to identify using the dialect identifier first.
        try {
            final IdentifiedDialect dialect = dialectIdentifier.identify(resource);
            if(dialect != null) {
                return new IdentifiedResource(resource, dialect);
            }
        } catch(MetaborgException e) {
            logger.error("Error identifying dialect", e);
            return null;
        } catch(MetaborgRuntimeException e) {
            // Ignore
        }

        // Identify using identification facet.
        final Set<LanguageIdentifier> identifiedLanguageIds = Sets.newLinkedHashSet();
        ILanguageImpl identifiedLanguage = null;
        for(ILanguageImpl language : languages) {
            if(identify(resource, language)) {
                identifiedLanguageIds.add(language.id());
                identifiedLanguage = language;
            }
        }

        if(identifiedLanguageIds.size() > 1) {
            throw new IllegalStateException("Resource " + resource + " identifies to multiple languages: "
                + Joiner.on(", ").join(identifiedLanguageIds));
        }

        if(identifiedLanguage == null) {
            return null;
        }

        return new IdentifiedResource(resource, null, identifiedLanguage);
    }
}
