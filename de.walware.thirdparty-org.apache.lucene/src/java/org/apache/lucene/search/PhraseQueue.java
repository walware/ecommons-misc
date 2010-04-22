package org.apache.lucene.search;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.util.PriorityQueue;

final class PhraseQueue extends PriorityQueue<PhrasePositions> {
  PhraseQueue(int size) {
    initialize(size);
  }

  @Override
  protected final boolean lessThan(PhrasePositions pp1, PhrasePositions pp2) {
    if (pp1.doc == pp2.doc) 
      if (pp1.position == pp2.position)
        // same doc and pp.position, so decide by actual term positions. 
        // rely on: pp.position == tp.position - offset. 
        return pp1.offset < pp2.offset;
      else
        return pp1.position < pp2.position;
    else
      return pp1.doc < pp2.doc;
  }
}
