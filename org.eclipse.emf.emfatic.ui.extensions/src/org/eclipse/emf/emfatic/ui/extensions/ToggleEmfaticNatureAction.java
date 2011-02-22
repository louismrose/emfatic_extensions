/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Louis Rose - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfatic.ui.extensions;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ToggleEmfaticNatureAction implements IObjectActionDelegate {

// FIXME - does not check item on startup. Would using the command platform framework fix this?
	
	protected ISelection selection;
	
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {}
	
	protected Object getFirstElementOf(ISelection selection) {
		if (selection instanceof IStructuredSelection)
			return ((IStructuredSelection)selection).getFirstElement();
		
		else
			return null;	
	}
	
	protected Object getFirstElementInSelection() {
		return getFirstElementOf(selection);	
	}
	
	protected static void refreshProjectContaining(IFile file) throws CoreException {
		file.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
		
		try {
			action.setChecked(containsNature(selection));
		
		} catch (CoreException e) {
			EmfaticUiExtensionsPlugin.getDefault().error("Error encountered will trying to check or uncheck a ToggleNatureAction", e);
		}
	}
	
	public void run(IAction action) {
		try {
			if (getFirstElementInSelection() instanceof IProject) {
				final IProject project = (IProject)getFirstElementInSelection();
				
				applyNaturesToProject(project, toggleNature(project));
			}
			
		} catch (CoreException e) {
			EmfaticUiExtensionsPlugin.getDefault().error("Error encountered will trying toggle a nature", e);
		}
	}

	private Collection<String> toggleNature(final IProject project) throws CoreException {
		final Collection<String> newNatureIds;
		
		final String[] currentNatureIds = project.getDescription().getNatureIds();
		
		if (containsOurNature(currentNatureIds)) {
			newNatureIds = removeOurNature(currentNatureIds);
			
		} else {
			newNatureIds = addOurNature(currentNatureIds);
		}
		
		return newNatureIds;
	}
	
	private void applyNaturesToProject(IProject project, Collection<String> natureIds) throws CoreException {
		final IProjectDescription desc = project.getDescription();
				
		desc.setNatureIds(natureIds.toArray(new String[0]));
		
		project.setDescription(desc, null);
	}

	private Collection<String> addOurNature(final String[] natureIds) {
		final Collection<String> newNatureIds = new LinkedList<String>();
		newNatureIds.add(getNatureId());
		newNatureIds.addAll(Arrays.asList(natureIds));
		return newNatureIds;
	}

	private Collection<String> removeOurNature(final String[] natureIds) {
		final Collection<String> newNatureIds = new LinkedList<String>(Arrays.asList(natureIds));
		newNatureIds.remove(getNatureId());
		return newNatureIds;
	}

	private boolean containsNature(ISelection selection) throws CoreException {
		if (getFirstElementOf(selection) instanceof IProject) {
			final IProject project = (IProject)getFirstElementOf(selection);
			final String[] natureIds = project.getDescription().getNatureIds();
			
			return containsOurNature(natureIds);
		}
		
		return false;
	}
	
	private boolean containsOurNature(String[] natureIds) {
		for (String natureId : natureIds) {
			if (getNatureId().equals(natureId))
				return true;
		}
		
		return false;
	}

	protected String getNatureId() {
		return EmfaticNature.ID;
	}
}
