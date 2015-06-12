/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.text.core.util;

import org.junit.Assert;
import org.junit.Test;


public class HtmlUtilsEntityTest {
	
	
	@Test
	public void getEntityName_fromName() {
		Assert.assertEquals("amp", HtmlUtils.getEntityName("amp"));
	}
	
	@Test
	public void getEntityName_fromHtmlEntity() {
		Assert.assertEquals("amp", HtmlUtils.getEntityName("&amp;"));
		
		Assert.assertEquals("amp", HtmlUtils.getEntityName("&amp"));
	}
	
}
