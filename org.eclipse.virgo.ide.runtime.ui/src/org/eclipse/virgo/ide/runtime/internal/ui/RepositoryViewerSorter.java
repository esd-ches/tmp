/*******************************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a divison of VMware, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SpringSource, a division of VMware, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.virgo.ide.runtime.internal.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.virgo.ide.bundlerepository.domain.Artefact;
import org.eclipse.virgo.ide.bundlerepository.domain.ArtefactSet;
import org.eclipse.virgo.ide.bundlerepository.domain.IArtefactTyped;
import org.eclipse.virgo.ide.bundlerepository.domain.ILocalEntity;
import org.eclipse.virgo.ide.bundlerepository.domain.LocalArtefactSet;

/**
 * @author Christian Dupuis
 */
public class RepositoryViewerSorter extends ViewerSorter {

	@Override
	public int category(Object element) {
		int category = 0;
		if (element instanceof IArtefactTyped) {
			IArtefactTyped typed = (IArtefactTyped) element;
			category = typed.getArtefactType().getOrdering();
		}
		if (element instanceof ArtefactSet) {
			category += 10;
		}
		if (category > 0) {
			return category;
		}
		return super.category(element);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof LocalArtefactSet && e2 instanceof LocalArtefactSet) {
			return ((ILocalEntity) e1).getFile().compareTo(((ILocalEntity) e2).getFile());
		} else if (e1 instanceof Artefact && e2 instanceof Artefact) {
			String st1 = ((Artefact) e1).getSymbolicName() + ";" + ((Artefact) e1).getVersion();
			String st2 = ((Artefact) e2).getSymbolicName() + ";" + ((Artefact) e2).getVersion();
			return super.compare(viewer, st1, st2);
		}
		return super.compare(viewer, e1, e2);
	}

}
