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

package org.apache.lucene.extra.queryParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;


/**
 * A QueryParser which constructs queries to search multiple fields.
 */
public class ExtendedQueryParser extends QueryParser {
	
	private static String checkFields(final String[] fields) {
		if (fields == null || fields.length == 0) {
			throw new IllegalArgumentException();
		}
		if (fields.length == 1) {
			return fields[0];
		}
		return null;
	}
	
	
  protected String[] fields;
  protected Map<String,Float> boosts;

  /**
   * Creates a MultiFieldQueryParser. 
   * Allows passing of a map with term to Boost, and the boost to apply to each term.
   *
   * <p>It will, when parse(String query)
   * is called, construct a query like this (assuming the query consists of
   * two terms and you specify the two fields <code>title</code> and <code>body</code>):</p>
   * 
   * <code>
   * (title:term1 body:term1) (title:term2 body:term2)
   * </code>
   *
   * <p>When setDefaultOperator(AND_OPERATOR) is set, the result will be:</p>
   *  
   * <code>
   * +(title:term1 body:term1) +(title:term2 body:term2)
   * </code>
   * 
   * <p>When you pass a boost (title=>5 body=>10) you can get </p>
   * 
   * <code>
   * +(title:term1^5.0 body:term1^10.0) +(title:term2^5.0 body:term2^10.0)
   * </code>
   *
   * <p>In other words, all the query's terms must appear, but it doesn't matter in
   * what fields they appear.</p>
   */
  public ExtendedQueryParser(final Version matchVersion, final String[] fields, final Analyzer analyzer, final Map<String,Float> boosts) {
    this(matchVersion, fields, analyzer);
    this.boosts = boosts;
  }
  
  /**
   * Creates a MultiFieldQueryParser.
   *
   * <p>It will, when parse(String query)
   * is called, construct a query like this (assuming the query consists of
   * two terms and you specify the two fields <code>title</code> and <code>body</code>):</p>
   * 
   * <code>
   * (title:term1 body:term1) (title:term2 body:term2)
   * </code>
   *
   * <p>When setDefaultOperator(AND_OPERATOR) is set, the result will be:</p>
   *  
   * <code>
   * +(title:term1 body:term1) +(title:term2 body:term2)
   * </code>
   * 
   * <p>In other words, all the query's terms must appear, but it doesn't matter in
   * what fields they appear.</p>
   */
  public ExtendedQueryParser(final Version matchVersion, final String[] fields, final Analyzer analyzer) {
    super(matchVersion, checkFields(fields), analyzer);
    this.fields = fields;
		super.setLowercaseExpandedTerms(false);
		super.setAutoGeneratePhraseQueries(true);
	}
	
	
	@Override
	public void setLowercaseExpandedTerms(final boolean lowercaseExpandedTerms) {
		throw new UnsupportedOperationException();
	}
	
	protected Query extGetFieldQuery(final String field, final String queryText,
			final boolean quoted) throws ParseException {
		if (!(field.endsWith(".txt") || field.endsWith(".html"))) {
			return new TermQuery(new Term(field, queryText));
		}
		return super.getFieldQuery(field, queryText, quoted);
	}
	
	protected Query extGetWildcardQuery(final String field, final String queryText) throws ParseException {
		if (!(field.endsWith(".txt") || field.endsWith(".html"))) {
			return super.getWildcardQuery(field, queryText.toLowerCase());
		}
		return super.getWildcardQuery(field, queryText);
	}
	
	protected Query extGetPrefixQuery(final String field, final String queryText) throws ParseException {
		if (!(field.endsWith(".txt") || field.endsWith(".html"))) {
			return super.getPrefixQuery(field, queryText.toLowerCase());
		}
		return super.getPrefixQuery(field, queryText);
	}
	
	protected Query extGetFuzzyQuery(final String field, final String queryText, final float minSimilarity) throws ParseException {
		if (!(field.endsWith(".txt") || field.endsWith(".html"))) {
			return super.getFuzzyQuery(field, queryText.toLowerCase(), minSimilarity);
		}
		return super.getFuzzyQuery(field, queryText, minSimilarity);
	}
	
	
	@Override
	protected Query getFieldQuery(final String field, final String queryText, final int slop) throws ParseException {
		if (field == null) {
			final List<BooleanClause> clauses = new ArrayList<BooleanClause>();
			for (int i = 0; i < fields.length; i++) {
				final Query q = extGetFieldQuery(fields[i], queryText, true);
				if (q != null) {
					//If the user passes a map of boosts
					if (boosts != null) {
						//Get the boost from the map and apply them
						final Float boost = boosts.get(fields[i]);
						if (boost != null) {
							q.setBoost(boost.floatValue());
						}
					}
					applySlop(q,slop);
					clauses.add(new BooleanClause(q, Occur.SHOULD));
				}
			}
			if (clauses.size() == 0) {
				return null;
			}
			return getBooleanQuery(clauses, true);
		}
		final Query q = extGetFieldQuery(field, queryText, true);
		applySlop(q,slop);
		return q;
	}
	
	private void applySlop(final Query q, final int slop) {
		if (q instanceof PhraseQuery) {
			((PhraseQuery) q).setSlop(slop);
		} else if (q instanceof MultiPhraseQuery) {
			((MultiPhraseQuery) q).setSlop(slop);
		}
	}
	
	@Override
	protected Query getFieldQuery(final String field, final String queryText, final boolean quoted) throws ParseException {
		if (field == null) {
			final List<BooleanClause> clauses = new ArrayList<BooleanClause>();
			for (int i = 0; i < fields.length; i++) {
				final Query q = extGetFieldQuery(fields[i], queryText, quoted);
				if (q != null) {
					//If the user passes a map of boosts
					if (boosts != null) {
						//Get the boost from the map and apply them
						final Float boost = boosts.get(fields[i]);
						if (boost != null) {
							q.setBoost(boost.floatValue());
						}
					}
					clauses.add(new BooleanClause(q, Occur.SHOULD));
				}
			}
			if (clauses.size() == 0) {
				return null;
			}
			return getBooleanQuery(clauses, true);
		}
		final Query q = extGetFieldQuery(field, queryText, quoted);
		return q;
	}


  @Override
  protected Query getFuzzyQuery(final String field, final String termStr, final float minSimilarity) throws ParseException
  {
    if (field == null) {
      final List<BooleanClause> clauses = new ArrayList<BooleanClause>();
      for (int i = 0; i < fields.length; i++) {
        clauses.add(new BooleanClause(extGetFuzzyQuery(fields[i], termStr, minSimilarity),
            Occur.SHOULD));
      }
      return getBooleanQuery(clauses, true);
    }
    return extGetFuzzyQuery(field, termStr, minSimilarity);
  }

  @Override
  protected Query getPrefixQuery(final String field, final String termStr) throws ParseException
  {
    if (field == null) {
      final List<BooleanClause> clauses = new ArrayList<BooleanClause>();
      for (int i = 0; i < fields.length; i++) {
        clauses.add(new BooleanClause(extGetPrefixQuery(fields[i], termStr),
            Occur.SHOULD));
      }
      return getBooleanQuery(clauses, true);
    }
    return extGetPrefixQuery(field, termStr);
  }

  @Override
  protected Query getWildcardQuery(final String field, final String termStr) throws ParseException {
    if (field == null) {
      final List<BooleanClause> clauses = new ArrayList<BooleanClause>();
      for (int i = 0; i < fields.length; i++) {
        clauses.add(new BooleanClause(extGetWildcardQuery(fields[i], termStr),
            Occur.SHOULD));
      }
      return getBooleanQuery(clauses, true);
    }
    return extGetWildcardQuery(field, termStr);
  }

  @Override
  protected Query getRangeQuery(final String field, final String part1, final String part2, final boolean inclusive) throws ParseException {
    if (field == null) {
      final List<BooleanClause> clauses = new ArrayList<BooleanClause>();
      for (int i = 0; i < fields.length; i++) {
        clauses.add(new BooleanClause(getRangeQuery(fields[i], part1, part2, inclusive),
            Occur.SHOULD));
      }
      return getBooleanQuery(clauses, true);
    }
    return super.getRangeQuery(field, part1, part2, inclusive);
  }

}