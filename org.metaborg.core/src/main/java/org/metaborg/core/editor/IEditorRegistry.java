package org.metaborg.core.editor;

import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.core.project.IProject;

public interface IEditorRegistry {
    /**
     * @return All open editors.
     */
    Iterable<IEditor> openEditors();

    /**
     * Open an editor for given resource.
     *
     * @param resource
     *            Resource to open editor for.
     * @param project
     *            The project that contains the resource.
     */
    void open(FileObject resource, IProject project);
}
