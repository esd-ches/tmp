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
package org.eclipse.virgo.ide.runtime.internal.ui.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.PDEPluginImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.virgo.ide.bundlerepository.domain.ArtefactSet;
import org.eclipse.virgo.ide.bundlerepository.domain.IArtefact;
import org.eclipse.virgo.ide.runtime.internal.ui.ArtefactLabelProvider;
import org.eclipse.virgo.ide.runtime.internal.ui.ServerUiImages;
import org.eclipse.virgo.ide.runtime.internal.ui.ServerUiPlugin;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.editor.IServerEditorPartFactory;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorCore;

/**
 * 
 * 
 * @author Miles Parker
 * 
 */
public class ServerEditorContentLabelProvider implements ITreeContentProvider, ILabelProvider {

	IServer server;

	IServerEditorPartFactory[] pageFactories;
	
	IServerEditorPartFactory repositoryPageFactory;

	RepositoryContentProvider repositoryContentProvider;
	ArtefactLabelProvider repositoryLabelProvider;

	ServerEditorContentLabelProvider(IServer server) {
		this.server = server;
		IServerType serverType = server.getServerType();
		IServerWorkingCopy serverWorking = server.createWorkingCopy();
		Iterator iterator = ServerEditorCore.getServerEditorPageFactories().iterator();
		List<IServerEditorPartFactory> pageTexts = new ArrayList<IServerEditorPartFactory>();
		while (iterator.hasNext()) {
			IServerEditorPartFactory factory = (IServerEditorPartFactory) iterator.next();
			if (factory.supportsType(serverType.getId()) && factory.shouldCreatePage(serverWorking)) {
				pageTexts.add(factory);
			}
			if (factory.getId().equals(ServerUiPlugin.REPOSITORY_PAGE_ID)) {
				repositoryPageFactory = factory;
			}
		}
		repositoryContentProvider = new RepositoryContentProvider();
		repositoryLabelProvider = new ArtefactLabelProvider(server.getRuntime());
		pageFactories = pageTexts.toArray(new IServerEditorPartFactory[] {});
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public boolean hasChildren(Object element) {
		if (element == Messages.RepositoryBrowserEditorPage_InstalledBundlesAndLibraries
			|| getChildren(element).length > 0) {
			return true;
		}
		return false;
	}

	public Object getParent(Object element) {
		if (element == Messages.RepositoryBrowserEditorPage_InstalledBundlesAndLibraries) {
			return repositoryPageFactory;
		}
		if (element instanceof IRuntime) {
			return Messages.RepositoryBrowserEditorPage_InstalledBundlesAndLibraries;
		}
		if (element instanceof IServerEditorPartFactory) {
			return server;
		}
		Object parent = repositoryContentProvider.getParent(element);
		if (parent != null) {
			return parent;
		}
		return null;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement == server) {
			return pageFactories;
		}
		if (inputElement instanceof IServerEditorPartFactory) {
			IServerEditorPartFactory factory = (IServerEditorPartFactory) inputElement;
			if (factory == repositoryPageFactory) {
				return new Object[] { Messages.RepositoryBrowserEditorPage_InstalledBundlesAndLibraries };
			}
		}
		if (inputElement == Messages.RepositoryBrowserEditorPage_InstalledBundlesAndLibraries) {
			return repositoryContentProvider.getElements(server.getRuntime());
		}
		return new Object[] {};
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ArtefactSet) {
			return repositoryContentProvider.getChildren(parentElement);
		}
		return getElements(parentElement);
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public String getText(Object element) {
		if (element instanceof IServerEditorPartFactory) {
			return ((IServerEditorPartFactory) element).getName();
		}
		if (element instanceof IArtefact || element instanceof ArtefactSet) {
			return repositoryLabelProvider.getText(element);
		}
		return element.toString();
	}

	public Image getImage(Object element) {
		if (element instanceof IServerEditorPartFactory) {
			return PDEPlugin.getDefault().getLabelProvider().get(PDEPluginImages.DESC_PAGE_OBJ);
		}
		if (element instanceof IArtefact || element instanceof ArtefactSet) {
			return repositoryLabelProvider.getImage(element);
		}
		if (element == Messages.RepositoryBrowserEditorPage_InstalledBundlesAndLibraries) {
			return ServerUiImages.getImage(ServerUiImages.IMG_OBJ_LIB);
		}
		return null;
	}

	int getPageNumber(Object object) {
		if (object instanceof IServerEditorPartFactory) {
			for (int i = 0; i < pageFactories.length; i++) {
				if (pageFactories[i] == object) {
					return i;
				}
			}
		}
		Object parent = getParent(object);
		if (parent != null) {
			return getPageNumber(parent);
		}
		return -1;
	}
}