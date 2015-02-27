// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.DbHsqldbTestCase;
import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.oom.sqlgen.DbEntitySql;
import jodd.db.oom.sqlgen.ParsedSql;
import jodd.db.oom.tst.Boy2;
import jodd.db.oom.tst.Girl;
import org.junit.Before;
import org.junit.Test;

import static jodd.db.oom.sqlgen.DbSqlBuilder.sql;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DbHintTest extends DbHsqldbTestCase {

	@Before
	public void setUp() throws Exception {
		super.setUp();

		DbOomManager.resetAll();
		DbOomManager dbOom = DbOomManager.getInstance();
		dbOom.registerEntity(Boy2.class);
		dbOom.registerEntity(Girl.class);

		q1 = sql("select $C{boy.*}, $C{girl.*} from $T{Boy2 boy} join $T{Girl girl} on $boy.id=$girl.id").parse();

		q2 = sql("select $C{boy.*}, $C{boy.girl.*} from $T{Boy2 boy} join $T{Girl girl} on $boy.id=$girl.id").parse();

		q3 = sql("select $C{boy.*}, $C{girl.*}, (select count (1) from $T{Girl girl2}) as totalGirlsCount from $T{Boy2 boy} join $T{Girl girl} on $boy.id=$girl.id").parse();

		q4 = sql("select $C{boy.*}, $C{boy.girlAlt:girl.*} from $T{Boy2 boy} join $T{Girl girl} on $boy.id=$girl.id").parse();

		q5 = sql("select $C{boy.*}, $C{boy.girlAlt:girl.*}, (select count (1) from $T{Girl girl2}) as $C{boy.totalGirls:.totalGirlsCount} from $T{Boy2 boy} join $T{Girl girl} on $boy.id=$girl.id").parse();

		q6 = sql("select $C{boy.*}, $C{boy.girlAlt:girl.[id,name]} from $T{Boy2 boy} join $T{Girl girl} on $boy.id=$girl.id").parse();
	}

	ParsedSql q1;
	ParsedSql q2;
	ParsedSql q3;
	ParsedSql q4;
	ParsedSql q5;
	ParsedSql q6;


	@Test
	public void testHint() {
		DbSession dbSession = new DbThreadSession(cp);

		// prepare data

		assertEquals(1, DbEntitySql.insert(new Girl(1, "Anna", "swim")).query().executeUpdate());
		assertEquals(1, DbEntitySql.insert(new Girl(2, "Sandra", "piano")).query().executeUpdate());
		assertEquals(1, DbEntitySql.insert(new Boy2(1, "John", 1)).query().executeUpdate());

		// select without hint

		DbOomQuery dbOomQuery = new DbOomQuery(q1);

		Object[] result = dbOomQuery.find(Boy2.class, Girl.class);

		Boy2 boy2 = (Boy2) result[0];
		assertEquals(1, boy2.id);
		assertEquals("John", boy2.name);
		assertEquals(1, boy2.girlId);
		assertNull(boy2.girl);

		Girl girl = (Girl) result[1];
		assertEquals(1, girl.id);

		// select with t-sql hint

		dbOomQuery = new DbOomQuery(q2);

		boy2 = dbOomQuery.find(Boy2.class, Girl.class);

		assertEquals(1, boy2.id);
		assertEquals("John", boy2.name);
		assertEquals(1, boy2.girlId);
		assertNotNull(boy2.girl);
		assertEquals(1, boy2.girl.id);
		assertEquals(0, boy2.totalGirls);

		// select with external hints

		dbOomQuery = new DbOomQuery(q3);
		dbOomQuery.withHints("boy", "boy.girlAlt", "boy.totalGirls");
		boy2 = dbOomQuery.find(Boy2.class, Girl.class, Integer.class);

		assertEquals(1, boy2.id);
		assertEquals("John", boy2.name);
		assertEquals(1, boy2.girlId);
		assertNotNull(boy2.girlAlt);
		assertEquals(1, boy2.girlAlt.id);
		assertEquals(2, boy2.totalGirls);

		// same select with t-sql hints

		dbOomQuery = new DbOomQuery(q4);
		boy2 = dbOomQuery.find(Boy2.class, Girl.class);

		assertEquals(1, boy2.id);
		assertEquals("John", boy2.name);
		assertEquals(1, boy2.girlId);
		assertNotNull(boy2.girlAlt);
		assertEquals(1, boy2.girlAlt.id);
		assertEquals(0, boy2.totalGirls);

		// same select with t-sql hints

		dbOomQuery = new DbOomQuery(q5);
		boy2 = dbOomQuery.find(Boy2.class, Girl.class, Integer.class);

		assertEquals(1, boy2.id);
		assertEquals("John", boy2.name);
		assertEquals(1, boy2.girlId);
		assertNotNull(boy2.girlAlt);
		assertEquals(1, boy2.girlAlt.id);
		assertEquals(2, boy2.totalGirls);
		
		
		// same select with t-sql hints

		dbOomQuery = new DbOomQuery(q6);
		boy2 = dbOomQuery.find(Boy2.class, Girl.class);

		assertEquals(1, boy2.id);
		assertEquals("John", boy2.name);
		assertEquals(1, boy2.girlId);
		assertNotNull(boy2.girlAlt);
		assertEquals(1, boy2.girlAlt.id);
		assertNotNull(boy2.girlAlt.name);
		assertNull(boy2.girlAlt.speciality);


		dbSession.closeSession();
	}

}