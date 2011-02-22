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

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class EmfaticNature implements IProjectNature {

	public static final String ID = "org.eclipse.emf.emfatic.nature.EmfaticNature";

	private IProject project;
	
	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}
		
	public void configure() throws CoreException {	
		if (!projectHasBuildSpecForBuilder()) {
			applyBuildSpecToProject(addCommandForOurBuilderToExistingBuildSpec());
		}
	}
	
	private Collection<ICommand> addCommandForOurBuilderToExistingBuildSpec() throws CoreException {
		final Collection<ICommand> newBuildSpec = new LinkedList<ICommand>();
		newBuildSpec.add(createOurBuildCommand());
		newBuildSpec.addAll(Arrays.asList(project.getDescription().getBuildSpec()));
		return newBuildSpec;
	}
	
	private ICommand createOurBuildCommand() throws CoreException {
		final ICommand buildCommand = project.getDescription().newCommand();
		buildCommand.setBuilderName(getBuilderID());
		return buildCommand;
	}
	
	private void applyBuildSpecToProject(Collection<ICommand> buildSpec) throws CoreException {
		final IProjectDescription desc = project.getDescription();
				
		desc.setBuildSpec(buildSpec.toArray(new ICommand[0]));
		
		project.setDescription(desc, null);
	}
	
	/*
	 * We do not remove the build command for their builder
	 * in this method, but have instead specified a nature for the 
	 * builder in the plugin.xml, which will cause the build command 
	 * to be removed automatically when the nature is removed.
	 */
	public void deconfigure() {
		// Do nothing
	}

	private boolean projectHasBuildSpecForBuilder() throws CoreException {
		if (project != null) {
			for (ICommand buildCommand : project.getDescription().getBuildSpec()) {
				if (getBuilderID().equals(buildCommand.getBuilderName())) {
					return true;
				}
			}
		}
		
		return false;
	}

	protected String getBuilderID() {
		return EmfaticBuilder.ID;
	}
}
