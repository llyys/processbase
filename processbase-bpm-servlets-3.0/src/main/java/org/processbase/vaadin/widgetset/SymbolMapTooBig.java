
package org.processbase.vaadin.widgetset;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.linker.Shardable;

@LinkerOrder(Order.POST)
@Shardable
public class SymbolMapTooBig extends AbstractLinker {
	@Override
	public String getDescription() {
		return "Remove the symbolMap file from the artifacts.";
	}

	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context,
			ArtifactSet artifacts, boolean onePermutation)
			throws UnableToCompleteException {
		if (onePermutation)
			return artifacts;
		artifacts = new ArtifactSet(artifacts);
		for (EmittedArtifact artifact : artifacts.find(EmittedArtifact.class))
			if (artifact.getPartialPath().endsWith(".symbolMap"))
				artifacts.remove(artifact);
		return artifacts;
	}
}
