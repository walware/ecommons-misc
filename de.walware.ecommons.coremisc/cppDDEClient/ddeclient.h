/*=============================================================================#
 # Copyright (c) 2004-2014 TeXlipse-Project (texlipse.sf.net) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Tor Arne Vestbø - initial API and implementation
 #=============================================================================*/

#include <jni.h>
/* Header for class de_walware_ecommons_io_win_DDEClient */

#ifndef _Included_de_walware_ecommons_io_win_DDEClient
#define _Included_de_walware_ecommons_io_win_DDEClient
#ifdef __cplusplus
extern "C" {
#endif


JNIEXPORT jint JNICALL Java_de_walware_ecommons_io_win_DDEClient_ddeExecute(
		JNIEnv *, jclass, jstring, jstring, jstring);

#ifdef __cplusplus
}
#endif
#endif
