/*=============================================================================#
 # Copyright (c) 2000-2015 IBM Corporation and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     IBM Corporation - initial API and implementation
 #     Patrick Chuong (Texas Instruments) - Bug 292411
 #     Stephan Wahlbrink - sync, IDisposable
 #=============================================================================*/

package de.walware.ecommons.ui.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.walware.ecommons.IDisposable;


/**
 * A registry that maps <code>ImageDescriptors</code> to <code>Image</code>.
 */
public class ImageDescriptorRegistry implements IDisposable {
	
	
	private final Map<ImageDescriptor, Image> fRegistry = new HashMap<ImageDescriptor, Image>();
	
	private final Display fDisplay;
	
	private boolean fDisposed;
	
	
	/**
	 * Creates a new image descriptor registry for the current or default display, respectively.
	 */
	public ImageDescriptorRegistry() {
		this(UIAccess.getDisplay());
	}
	
	/**
	 * Creates a new image descriptor registry for the given display. All images
	 * managed by this registry will be disposed when the display gets disposed.
	 * 
	 * @param display the display the images managed by this registry are allocated for 
	 */
	public ImageDescriptorRegistry(final Display display) {
		if (display == null) {
			throw new NullPointerException("display");
		}
		fDisplay = display;
		hookDisplay();
	}
	
	
	/**
	 * Returns the image associated with the given image descriptor.
	 * 
	 * @param descriptor the image descriptor for which the registry manages an image
	 * @return the image associated with the image descriptor or <code>null</code>
	 *  if the image descriptor can't create the requested image.
	 */
	public Image get(ImageDescriptor descriptor) {
		if (descriptor == null) {
			descriptor = ImageDescriptor.getMissingImageDescriptor();
		}
		synchronized (fRegistry) {
			Image result = fRegistry.get(descriptor);
			if (result == null && !fDisposed) {
				result = descriptor.createImage();
				if (result != null) {
					fRegistry.put(descriptor, result);
				}
			}
			return result;
		}
	}
	
	private void hookDisplay() {
		fDisplay.asyncExec(new Runnable() {
			@Override
			public void run() {
				fDisplay.disposeExec(new Runnable() {
					@Override
					public void run() {
						dispose();
					}
				});
			}
		});
	}
	
	/**
	 * Disposes all images managed by this registry.
	 */	
	@Override
	public void dispose() {
		synchronized (fRegistry) {
			fDisposed = true;
			for (final Image image : fRegistry.values()) {
				image.dispose();
			}
			fRegistry.clear();
		}
	}
	
}
