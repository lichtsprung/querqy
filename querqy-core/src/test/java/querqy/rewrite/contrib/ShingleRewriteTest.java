package querqy.rewrite.contrib;

import org.junit.Test;

import querqy.model.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static querqy.QuerqyMatchers.*;

/**
 * Test for ShingleRewriter.
 */
public class ShingleRewriteTest {

    @Test
    public void testShinglingForTwoTokens() {
        Query query = new Query();
        addTerm(query, "cde");
        addTerm(query, "ajk");
        ExpandedQuery expandedQuery = new ExpandedQuery(query);
        ShingleRewriter rewriter = new ShingleRewriter();
        rewriter.rewrite(expandedQuery);

        assertThat(expandedQuery.getUserQuery(),
                bq(
                        dmq(
                                term("cde"),
                                term("cdeajk")
                        ),
                        dmq(
                                term("ajk"),
                                term("cdeajk")
                        )
                )
        );
    }
    
    @Test
    public void testThatShinglingDoesNotTriggerExceptionOnSingleTerm() throws Exception {
        Query query = new Query();
        addTerm(query, "t1");
        
        ExpandedQuery expandedQuery = new ExpandedQuery(query);
        ShingleRewriter rewriter = new ShingleRewriter();
        rewriter.rewrite(expandedQuery);

        assertThat(expandedQuery.getUserQuery(),
                bq(
                        dmq(
                                term("t1")
                        )
                )
        );
    }

    @Test
    public void testShinglingForTwoTokensWithSameField() {
        Query query = new Query();
        addTerm(query, "f1", "cde");
        addTerm(query, "f1", "ajk");
        ExpandedQuery expandedQuery = new ExpandedQuery(query);
        ShingleRewriter rewriter = new ShingleRewriter();
        rewriter.rewrite(expandedQuery);

        assertThat(expandedQuery.getUserQuery(),
                bq(
                        dmq(
                                term("f1", "cde"),
                                term("f1", "cdeajk")
                        ),
                        dmq(
                                term("f1", "ajk"),
                                term("f1", "cdeajk")
                        )
                )
        );
    }

    @Test
    public void testShinglingForTwoTokensWithSameFieldAndGeneratedFlag() {
        Query query = new Query();
        addTerm(query, "f1", "cde", true);
        addTerm(query, "f1", "ajk", true);
        ExpandedQuery expandedQuery = new ExpandedQuery(query);
        ShingleRewriter rewriter = new ShingleRewriter(true);
        rewriter.rewrite(expandedQuery);

        assertThat(expandedQuery.getUserQuery(),
                bq(
                        dmq(
                                term("f1", "cde"),
                                term("f1", "cdeajk")
                        ),
                        dmq(
                                term("f1", "ajk"),
                                term("f1", "cdeajk")
                        )
                )
        );
    }

    @Test
    public void testShinglingForTwoTokensWithDifferentFieldsDontShingle() {
        Query query = new Query();
        addTerm(query, "f1", "cde");
        addTerm(query, "f2", "ajk");
        ExpandedQuery expandedQuery = new ExpandedQuery(query);
        ShingleRewriter rewriter = new ShingleRewriter();
        rewriter.rewrite(expandedQuery);

        assertThat(expandedQuery.getUserQuery(), bq(dmq(term("cde")), dmq(term("ajk"))));
    }
    
    @Test
    public void testShinglingForTwoTokensWithOnFieldNameNullDontShingle() {
        Query query = new Query();
        addTerm(query, "f1", "cde");
        addTerm(query, "ajk");
        ExpandedQuery expandedQuery = new ExpandedQuery(query);
        ShingleRewriter rewriter = new ShingleRewriter();
        rewriter.rewrite(expandedQuery);

        assertThat(expandedQuery.getUserQuery(), bq(dmq(term("f1", "cde")), dmq(term("ajk"))));
    }

    @Test
    public void testShinglingForThreeTokens() {
        Query query = new Query();
        addTerm(query, "cde");
        addTerm(query, "ajk");
        addTerm(query, "xyz");
        ExpandedQuery expandedQuery = new ExpandedQuery(query);
        ShingleRewriter rewriter = new ShingleRewriter();
        rewriter.rewrite(expandedQuery);

        assertThat(expandedQuery.getUserQuery(),
                bq(
                        dmq(
                                term("cde"),
                                term("cdeajk")
                        ),
                        dmq(
                                term("ajk"),
                                term("cdeajk"),
                                term("ajkxyz")
                        ),
                        dmq(
                                term("xyz"),
                                term("ajkxyz")
                        )
                )
        );
    }

    @Test
    public void testShinglingForThreeTokensWithThreeTokenGenerated() {
        Query query = new Query();
        addTerm(query, "cde", true);
        addTerm(query, "ajk", true);
        addTerm(query, "xyz", true);
        ExpandedQuery expandedQuery = new ExpandedQuery(query);
        ShingleRewriter rewriter = new ShingleRewriter(true);
        rewriter.rewrite(expandedQuery);

        assertThat(expandedQuery.getUserQuery(),
                bq(
                        dmq(
                                term("cde"),
                                term("cdeajk")
                        ),
                        dmq(
                                term("ajk"),
                                term("cdeajk"),
                                term("ajkxyz")
                        ),
                        dmq(
                                term("xyz"),
                                term("ajkxyz")
                        )
                )
        );
    }



    @Test
    public void testShinglingForThreeTokensWithOneTokenGeneratedIgnoringGenerated() {
        Query query = new Query();
        addTerm(query, "cde", false);
        addTerm(query, "ajk", false);
        addTerm(query, "xyz", true);
        ExpandedQuery expandedQuery = new ExpandedQuery(query);
        ShingleRewriter rewriter = new ShingleRewriter(false);
        rewriter.rewrite(expandedQuery);

        System.out.println(expandedQuery.getUserQuery());
        assertThat(expandedQuery.getUserQuery(),
                bq(
                        dmq(
                                term("cde"),
                                term("cdeajk")
                        ),
                        dmq(
                                term("ajk"),
                                term("cdeajk")
                        ),
                        dmq(
                                term("xyz")
                        )
                )
        );
    }


    @Test
    public void testShinglingForThreeTokensWithMixedFields() {
        Query query = new Query();
        addTerm(query, "f1", "cde");
        addTerm(query, "f1", "ajk");
        addTerm(query, "f2", "xyz");
        ExpandedQuery expandedQuery = new ExpandedQuery(query);
        ShingleRewriter rewriter = new ShingleRewriter();
        rewriter.rewrite(expandedQuery);

        assertThat(expandedQuery.getUserQuery(),
                bq(
                        dmq(
                                term("f1", "cde"),
                                term("f1", "cdeajk")
                        ),
                        dmq(    
                                term("f1", "ajk"),
                                term("f1", "cdeajk")
                        ),
                        dmq(term("f2", "xyz"))
                )
        );
    }

    private void addTerm(Query query, String value) {
        addTerm(query, null, value);
    }

    private void addTerm(Query query, String field, String value) {
        DisjunctionMaxQuery dmq = new DisjunctionMaxQuery(query, Clause.Occur.SHOULD, true);
        query.addClause(dmq);
        Term term = new Term(dmq, field, value);
        dmq.addClause(term);
    }

    private void addTerm(Query query, String value, boolean isGenerated) {
        addTerm(query, null, value, isGenerated);
    }

    private void addTerm(Query query, String field, String value, boolean isGenerated) {
        DisjunctionMaxQuery dmq = new DisjunctionMaxQuery(query, Clause.Occur.SHOULD, true);
        query.addClause(dmq);
        Term term = new Term(dmq, field, value, isGenerated);
        dmq.addClause(term);
    }

}
