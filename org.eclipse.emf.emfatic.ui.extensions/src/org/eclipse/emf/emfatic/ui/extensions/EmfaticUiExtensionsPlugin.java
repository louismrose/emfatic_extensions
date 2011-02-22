package org.eclipse.emf.emfatic.ui.extensions;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class EmfaticUiExtensionsPlugin extends Plugin {

	//The shared instance.
	private static EmfaticUiExtensionsPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public EmfaticUiExtensionsPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static EmfaticUiExtensionsPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Returns an image transformationStrategyExtension for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image transformationStrategyExtension
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.emf.emfatic.ui.extensions", path);
	}
	
	/**
	 * This method returns an image from the path
	 * @param path
	 * @return
	 */
	public Image createImage(String path) {
		try {
			URL BASE_URL = EmfaticUiExtensionsPlugin.getDefault().getBundle().getEntry("/");
			URL url = new URL(BASE_URL, path);
			return ImageDescriptor.createFromURL(url).createImage();
		}
		catch(MalformedURLException e) {}
		return null;
	}

	public void error(String message, Exception e) {
		getLog().log(new Status(IStatus.ERROR, "org.eclipse.emf.emfatic.ui.extensions", message));
	}

	public void info(String message) {
		getLog().log(new Status(IStatus.INFO, "org.eclipse.emf.emfatic.ui.extensions", message));
	}

}
