package org.metaborg.spoofax.core.syntax;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.config.JSGLRVersion;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.core.messages.IMessage;
import org.metaborg.core.messages.MessageFactory;
import org.metaborg.core.messages.MessageSeverity;
import org.metaborg.core.messages.MessageUtils;
import org.metaborg.core.source.ISourceRegion;
import org.metaborg.core.source.SourceRegion;
import org.metaborg.parsetable.IParseTable;
import org.metaborg.sdf2table.parsetable.ParseTable;
import org.metaborg.spoofax.core.unit.ParseContrib;
import org.metaborg.util.time.Timer;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.shared.BadTokenException;
import org.spoofax.jsglr2.JSGLR2;
import org.spoofax.jsglr2.JSGLR2Result;
import org.spoofax.jsglr2.JSGLR2Success;
import org.spoofax.jsglr2.JSGLR2Variant;
import org.spoofax.jsglr2.messages.Message;

import com.google.common.collect.SetMultimap;

public class JSGLR2I extends JSGLRI<IParseTable> {

    private final JSGLR2<IStrategoTerm> parser;

    public JSGLR2I(IParserConfig config, ITermFactory termFactory, ILanguageImpl language, ILanguageImpl dialect,
        JSGLRVersion parserType) throws IOException {
        super(config, termFactory, language, dialect);

        this.parseTable = getParseTable(config.getParseTableProvider());
        this.parser = jsglrVersionToJSGLR2Preset(parserType).getJSGLR2(parseTable);
    }

    // TODO the two enums JSGLRVersion and JSGLR2Variants should be linked together,
    // but JSGLR2 cannot depend on org.metaborg.core and the other way around.
    // org.metaborg.spoofax.core depends on both, that's why the linking now happens here.
    // To fix this properly, JSGLRVersion should move to org.metaborg.spoofax.core,
    // because org.metaborg.core should be tool-agnositic and should not need to know the specifics of JSGLR(2).
    // The configuration of metaborg.yaml can then directly use the enum from JSGLR2.
    private JSGLR2Variant.Preset jsglrVersionToJSGLR2Preset(JSGLRVersion jsglrVersion) {
        switch(jsglrVersion) {
            case dataDependent:
                return JSGLR2Variant.Preset.dataDependent;
            case incremental:
                return JSGLR2Variant.Preset.incremental;
            case layoutSensitive:
                return JSGLR2Variant.Preset.layoutSensitive;
            case recovery:
                return JSGLR2Variant.Preset.recovery;
            case recoveryIncremental:
                return JSGLR2Variant.Preset.recoveryIncremental;
            case v2:
            default:
                return JSGLR2Variant.Preset.standard;
        }
    }

    @Override public ParseContrib parse(@Nullable JSGLRParserConfiguration parserConfig, @Nullable FileObject resource,
        String input) {
        if(parserConfig == null) {
            parserConfig = new JSGLRParserConfiguration();
        }

        final String fileName = resource != null ? resource.getName().getURI() : "";
        String startSymbol = getOrDefaultStartSymbol(parserConfig);

        final Timer timer = new Timer(true);

        final JSGLR2Result<IStrategoTerm> result = parser.parseResult(input, fileName, startSymbol);
        IStrategoTerm ast = result.isSuccess() ? ((JSGLR2Success<IStrategoTerm>) result).ast : null;
        final Collection<IMessage> messages = mapMessages(resource, result.messages);

        // add non-assoc warnings to messages
        messages.addAll(addDisambiguationWarnings(ast, resource));

        final long duration = timer.stop();

        final boolean hasAst = ast != null;
        final boolean hasErrors = MessageUtils.containsSeverity(messages, MessageSeverity.ERROR);

        if(hasAst && resource != null)
            SourceAttachment.putSource(ast, resource);

        return new ParseContrib(hasAst, hasAst && !hasErrors, ast, messages, duration);
    }

    private Collection<IMessage> mapMessages(FileObject resource, Collection<Message> messages) {
        return messages.stream().map(message -> {
            ISourceRegion region = new SourceRegion(message.region.startOffset, message.region.startRow,
                message.region.startColumn, message.region.endOffset, message.region.endRow, message.region.endColumn);
            MessageSeverity severity;

            switch(message.severity) {
                case WARNING:
                    severity = MessageSeverity.WARNING;
                    break;
                case ERROR:
                default:
                    severity = MessageSeverity.ERROR;
                    break;
            }

            return MessageFactory.newParseMessage(resource, region, message.message, severity, null);
        }).collect(Collectors.toList());
    }

    @Override public Set<BadTokenException> getCollectedErrors() {
        return Collections.emptySet();
    }

    @Override public SetMultimap<String, String> getNonAssocPriorities() {
        return ((ParseTable) parseTable).normalizedGrammar().getNonAssocPriorities();
    }

    @Override public SetMultimap<String, String> getNonNestedPriorities() {
        return ((ParseTable) parseTable).normalizedGrammar().getNonNestedPriorities();
    }

}
