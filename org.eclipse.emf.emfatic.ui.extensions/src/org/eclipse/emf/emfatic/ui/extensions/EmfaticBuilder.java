package org.eclipse.emf.emfatic.ui.extensions;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.emfatic.core.generator.ecore.EcoreGenerator;
import org.eclipse.ui.PlatformUI;

public class EmfaticBuilder extends IncrementalProjectBuilder {

	static final String ID = "org.eclipse.emf.emfatic.builder.EmfaticBuilder";
	
	@SuppressWarnings("unchecked")
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) {
		if (kind == IncrementalProjectBuilder.FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		
		refreshProject(monitor);
		
		return null;
	}
	
	private void fullBuild(final IProgressMonitor monitor) {
		try {			
			getProject().accept(new IResourceVisitor() {

				public boolean visit(IResource resource) throws CoreException {
					
					generateEcoreFor(resource, monitor);
					
					return true; // visit children too
				}
				
			});
						
		} catch (Exception e) {
			logException(e);
		}
	}

	private void incrementalBuild(IResourceDelta delta, final IProgressMonitor monitor) {
		try {			
			delta.accept(new IResourceDeltaVisitor() {
				
				public boolean visit(IResourceDelta delta) {					
					generateEcoreFor(delta.getResource(), monitor);
					
					return true; // visit children too
				}
			});
			
		} catch (CoreException e) {
			logException(e);
		}
	}
	
	private void generateEcoreFor(final IResource resource, final IProgressMonitor monitor) {
		if (resource instanceof IFile) {
			if ("emf".equals(resource.getFileExtension())) {
				new EcoreGenerator().generate((IFile)resource, true, monitor);
				logInfo("Generated Ecore for: " + resource.getFullPath());
			}
		}
	}
	
	private void refreshProject(IProgressMonitor monitor) {
		try {
			getProject().refreshLocal(IProject.DEPTH_INFINITE, monitor);
		} catch (CoreException e) {
			logException(e);
		}
	}
	
	private static void logInfo(String message) {
		if (PlatformUI.isWorkbenchRunning()) {
			EmfaticUiExtensionsPlugin.getDefault().info(message);
		} else {
			System.out.println(message);
		}
	}
	
	private static void logException(Exception e) {
		if (PlatformUI.isWorkbenchRunning()) {
			EmfaticUiExtensionsPlugin.getDefault().error("Error encountered whilst running the Emfatic UI Extensions builder.", e);
		} else {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

}
