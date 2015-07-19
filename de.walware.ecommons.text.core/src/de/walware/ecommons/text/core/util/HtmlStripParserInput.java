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

import de.walware.ecommons.text.core.input.FilterParserInput;
import de.walware.ecommons.text.core.input.StringParserInput;
import de.walware.ecommons.text.core.input.TextParserInput;
import de.walware.ecommons.text.core.util.HtmlUtils.Entity;


/**
 * Text parser input stripping out HTML markup providing decoded content text.
 */
public class HtmlStripParserInput extends FilterParserInput {
	
	private static class Match {
		
		private final char[] chars;
		private final int n;
		
		public Match(final char[] chars, final int n) {
			this.chars= chars;
			this.n= n;
		}
		
	}
	
	
	public HtmlStripParserInput(final TextParserInput source, final int defaultBufferSize) {
		super(source, defaultBufferSize);
	}
	
	public HtmlStripParserInput(final TextParserInput source) {
		this(source, DEFAULT_BUFFER_SIZE);
	}
	
	public HtmlStripParserInput(final String source) {
		this(new StringParserInput(source).init(),
				Math.min(source.length(), DEFAULT_BUFFER_SIZE) );
	}
	
	
	@Override
	protected int read(final TextParserInput in, final char[] buffer,
			final int[] beginIndexes, final int[] endIndexes,
			final int beginIdx, final int requiredEnd, final int recommendEnd) {
		int idx= beginIdx;
		ITER_C0: while (idx < recommendEnd) {
			final int c0= in.get(0);
			C0: switch (c0) {
			case EOF:
				break ITER_C0;
			case '<':
				if (consumeTag(in)) {
					continue ITER_C0;
				}
				break C0;
			case '&':
			{	final Match match= readEntity(in);
				if (match != null) {
					if (idx + match.chars.length <= buffer.length) {
						final int beginIndex= in.getIndex();
						final int endIndex= beginIndex + in.getLengthInSource(match.n);
						for (int i= 0; i < match.chars.length; i++, idx++) {
							buffer[idx]= match.chars[i];
							beginIndexes[idx]= beginIndex;
							endIndexes[idx]= endIndex;
						}
						in.consume(match.n);
						continue ITER_C0;
					}
					else {
						break ITER_C0;
					}
				}
				break C0;
			}
			default:
				break C0;
			}
			
			buffer[idx]= (char) c0;
			beginIndexes[idx]= in.getIndex();
			endIndexes[idx]= in.getIndex() + in.getLengthInSource(1);
			idx++;
			in.consume(1);
			continue;
		}
		beginIndexes[idx]= in.getIndex();
		return idx;
	}
	
	
	private boolean consumeTag(final TextParserInput in) {
		// after: <
		int n;
		switch (in.get(1)) {
//		EOF:
//			return false; -> default
		
		case '?': // pi
			n= 2;
			ITER_CN: while (true) {
				switch (in.get(n++)) {
				case EOF:
					in.consume(n - 1);
					return true;
				case '"':
					n= consumeQuoteD(in, n);
					continue ITER_CN;
				case '\'':
					n= consumeQuoteS(in, n);
					continue ITER_CN;
				case '?':
					if (in.get(n) == '>') {
						in.consume(n + 1);
						return true;
					}
					//$FALL-THROUGH$
				default:
					if (n >= 0x400) {
						in.consume(n);
						n= 0;
					}
					continue ITER_CN;
				}
			}
		
		case '!':
			if (in.matches(2, '-', '-')) { // comment
				n= 4;
				ITER_CN: while (true) {
					switch (in.get(n++)) {
					case EOF:
						in.consume(n - 1);
						return true;
					case '>':
						if (in.matches(n - 3, '-', '-')) {
							in.consume(n);
							return true;
						}
						//$FALL-THROUGH$
					default:
						if (n >= 0x400) {
							in.consume(n);
							n= 0;
						}
						continue ITER_CN;
					}
				}
			}
			//$FALL-THROUGH$
		
		case '/':
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'I':
		case 'J':
		case 'K':
		case 'L':
		case 'M':
		case 'N':
		case 'O':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
		case 'T':
		case 'U':
		case 'V':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			// tag start
			n= 2;
			ITER_CN: while (true) {
				switch (in.get(n++)) {
				case EOF:
					in.consume(n - 1);
					return true;
				case '"':
					n= consumeQuoteD(in, n);
					continue ITER_CN;
				case '\'':
					n= consumeQuoteS(in, n);
					continue ITER_CN;
				case '>':
					in.consume(n);
					return true;
				default:
					if (n >= 0x400) {
						in.consume(n);
						n= 0;
					}
					continue ITER_CN;
				}
			}
		
		default:
			return false;
		}
	}
	
	private int consumeQuoteD(final TextParserInput in, int n) {
		while (true) {
			switch (in.get(n++)) {
			case EOF:
				return n - 1;
			case '"':
				return n;
			default:
				if (n >= 0x400) {
					in.consume(n);
					n= 0;
				}
				continue;
			}
		}
	}
	
	private int consumeQuoteS(final TextParserInput in, int n) {
		while (true) {
			switch (in.get(n++)) {
			case EOF:
				return n - 1;
			case '\'':
				return n;
			default:
				if (n >= 0x400) {
					in.consume(n);
					n= 0;
				}
				continue;
			}
		}
	}
	
	private Match readEntity(final TextParserInput in) {
		// after: &
		int n;
		switch (in.get(1)) {
//		case EOF:
//			return null; -> default
		
		case '#':
			switch (in.get(2)) {
//			case EOF:
//				return null; -> default
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				n= 3;
				ITER_CN: while (n < 10) {
					switch (in.get(n++)) {
//					case EOF:
//						break; -> default
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						continue ITER_CN;
					case ';':
						return resolveEntity(in, Integer.parseInt(in.getString(2, n - 3), 10), n);
					default:
						break ITER_CN;
					}
				}
				return null;
			case 'x':
			case 'X':
				n= 3;
				ITER_CN: while (n < 10) {
					switch (in.get(n++)) {
//					case EOF:
//						break; // -> default
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
					case 'A':
					case 'B':
					case 'C':
					case 'D':
					case 'E':
					case 'F':
					case 'a':
					case 'b':
					case 'c':
					case 'd':
					case 'e':
					case 'f':
						continue ITER_CN;
					case ';':
						if (n > 4) {
							return resolveEntity(in, Integer.parseInt(in.getString(3, n - 4), 16), n);
						}
						break ITER_CN;
					default:
						break ITER_CN;
					}
				}
				return null;
			default:
				return null;
			}
		
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'I':
		case 'J':
		case 'K':
		case 'L':
		case 'M':
		case 'N':
		case 'O':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
		case 'T':
		case 'U':
		case 'V':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			n= 2;
			ITER_CN: while (n < 40) {
				switch (in.get(n++)) {
//				case EOF:
//					break; -> default
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
				case 'G':
				case 'H':
				case 'I':
				case 'J':
				case 'K':
				case 'L':
				case 'M':
				case 'N':
				case 'O':
				case 'P':
				case 'Q':
				case 'R':
				case 'S':
				case 'T':
				case 'U':
				case 'V':
				case 'W':
				case 'X':
				case 'Y':
				case 'Z':
				case 'a':
				case 'b':
				case 'c':
				case 'd':
				case 'e':
				case 'f':
				case 'g':
				case 'h':
				case 'i':
				case 'j':
				case 'k':
				case 'l':
				case 'm':
				case 'n':
				case 'o':
				case 'p':
				case 'q':
				case 'r':
				case 's':
				case 't':
				case 'u':
				case 'v':
				case 'w':
				case 'x':
				case 'y':
				case 'z':
					continue ITER_CN;
				case ';':
					return resolveEntity(in, in.getString(1, n - 2), n);
				default:
					break ITER_CN;
				}
			}
			return null;
		
		default:
			return null;
		}
	}
	
	private static Match resolveEntity(final TextParserInput in, final int codePoint, final int n) {
		try {
			return new Match(Character.toChars(codePoint), n);
		}
		catch (final IllegalArgumentException e) {
		}
		return null;
	}
	
	private static Match resolveEntity(final TextParserInput in, final String name, final int n) {
		final Entity entity= HtmlUtils.getNamedEntity(name);
		if (entity != null) {
			return new Match(entity.getChars(), n);
		}
		return null;
	}
	
}
