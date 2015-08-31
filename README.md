# Project Seed

This repository contains some technology I experimented on (read: quick start project).

On this branch, I'm testing auto reconnect feature of [HikariCP](http://brettwooldridge.github.io/HikariCP/).
 
Start the HelloWorld class with argument "jdbc:mysql://localhost:3306/mydb user password" using your favourite IDE and start, followed by start/stop sequence to see the behaviour.
It'll print something like this:


```bash

Hello World!
2015-08-31 18:42:08 [main] INFO  HelloWorld - Hello World from logback!
2015-08-31 18:42:08 [main] INFO  com.zaxxer.hikari.HikariDataSource - mydbpool - is starting.
2015-08-31 18:42:09 [main] INFO  HelloWorld - Data source created.
2015-08-31 18:42:16 [Thread-1] INFO  HelloWorld - Query success!
2015-08-31 18:42:17 [Thread-1] INFO  HelloWorld - Query success!
2015-08-31 18:42:18 [Thread-1] WARN  c.z.hikari.proxy.ConnectionProxy - mydbpool - Connection org.mariadb.jdbc.MySQLConnection@598220cd, created Aug 31, 18:42:09.116, last release 1010ms ago, IN_USE marked as broken because of SQLSTATE(08), ErrorCode(-1)
java.sql.SQLNonTransientConnectionException: Could not read resultset: unexpected end of stream, read 0 bytes from 4
	at org.mariadb.jdbc.internal.SQLExceptionMapper.get(SQLExceptionMapper.java:136) ~[mariadb-java-client-1.2.0.jar:na]
	at org.mariadb.jdbc.internal.SQLExceptionMapper.throwException(SQLExceptionMapper.java:106) ~[mariadb-java-client-1.2.0.jar:na]
	at org.mariadb.jdbc.MySQLStatement.executeQueryEpilog(MySQLStatement.java:252) ~[mariadb-java-client-1.2.0.jar:na]
	at org.mariadb.jdbc.MySQLStatement.execute(MySQLStatement.java:278) ~[mariadb-java-client-1.2.0.jar:na]
	at org.mariadb.jdbc.MySQLStatement.executeQuery(MySQLStatement.java:333) ~[mariadb-java-client-1.2.0.jar:na]
	at org.mariadb.jdbc.MySQLPreparedStatement.executeQuery(MySQLPreparedStatement.java:104) ~[mariadb-java-client-1.2.0.jar:na]
	at com.zaxxer.hikari.proxy.PreparedStatementProxy.executeQuery(PreparedStatementProxy.java:52) ~[HikariCP-2.4.1.jar:na]
	at com.zaxxer.hikari.proxy.HikariPreparedStatementProxy.executeQuery(HikariPreparedStatementProxy.java) [HikariCP-2.4.1.jar:na]
	at org.sql2o.Query$ResultSetIterableBase.<init>(Query.java:328) [sql2o-1.5.4.jar:na]
	at org.sql2o.Query$10.<init>(Query.java:412) [sql2o-1.5.4.jar:na]
	at org.sql2o.Query.executeAndFetchLazy(Query.java:412) [sql2o-1.5.4.jar:na]
	at org.sql2o.Query.executeAndFetch(Query.java:454) [sql2o-1.5.4.jar:na]
	at org.sql2o.Query.executeAndFetch(Query.java:445) [sql2o-1.5.4.jar:na]
	at HelloWorld.lambda$main$1(HelloWorld.java:24) [classes/:na]
	at HelloWorld$$Lambda$1/1551870003.run(Unknown Source) [classes/:na]
	at java.lang.Thread.run(Thread.java:745) [na:1.8.0_51]
Caused by: org.mariadb.jdbc.internal.common.QueryException: Could not read resultset: unexpected end of stream, read 0 bytes from 4
	at org.mariadb.jdbc.internal.mysql.MySQLProtocol.getResult(MySQLProtocol.java:970) ~[mariadb-java-client-1.2.0.jar:na]
	at org.mariadb.jdbc.internal.mysql.MySQLProtocol.executeQuery(MySQLProtocol.java:1038) ~[mariadb-java-client-1.2.0.jar:na]
	at org.mariadb.jdbc.internal.mysql.MySQLProtocol.executeQuery(MySQLProtocol.java:1020) ~[mariadb-java-client-1.2.0.jar:na]
	at org.mariadb.jdbc.MySQLStatement.execute(MySQLStatement.java:271) ~[mariadb-java-client-1.2.0.jar:na]
	... 12 common frames omitted
Caused by: java.io.EOFException: unexpected end of stream, read 0 bytes from 4
	at org.mariadb.jdbc.internal.common.packet.buffer.ReadUtil.readFully(ReadUtil.java:84) ~[mariadb-java-client-1.2.0.jar:na]
	at org.mariadb.jdbc.internal.common.packet.buffer.ReadUtil.readFully(ReadUtil.java:92) ~[mariadb-java-client-1.2.0.jar:na]
	at org.mariadb.jdbc.internal.common.packet.RawPacket.nextPacket(RawPacket.java:77) ~[mariadb-java-client-1.2.0.jar:na]
	at org.mariadb.jdbc.internal.common.packet.SyncPacketFetcher.getRawPacket(SyncPacketFetcher.java:67) ~[mariadb-java-client-1.2.0.jar:na]
	at org.mariadb.jdbc.internal.mysql.MySQLProtocol.getResult(MySQLProtocol.java:931) ~[mariadb-java-client-1.2.0.jar:na]
	... 15 common frames omitted
2015-08-31 18:42:18 [Thread-1] ERROR HelloWorld - Unable to obtain DB connection!
2015-08-31 18:42:19 [Thread-1] WARN  com.zaxxer.hikari.pool.PoolElf - mydbpool - Connection org.mariadb.jdbc.MySQLConnection@325932c7 failed alive test with exception Could not read resultset: unexpected end of stream, read 0 bytes from 4
2015-08-31 18:42:19 [Thread-1] WARN  com.zaxxer.hikari.pool.PoolElf - mydbpool - Connection org.mariadb.jdbc.MySQLConnection@30d36d4e failed alive test with exception Could not read resultset: unexpected end of stream, read 0 bytes from 4
2015-08-31 18:42:36 [Thread-1] INFO  HelloWorld - Query success!
2015-08-31 18:42:37 [Thread-1] INFO  HelloWorld - Query success!
```